package org.regminer.commons.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.regminer.commons.constant.Constant;
import org.regminer.commons.model.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.regminer.commons.constant.Constant.SEARCH_DEPTH;

/**
 * @author Jerry Zhang <zhangjian16@fudan.edu.cn>
 * @desc
 * @date 2024/1/2 16:29
 */

public class ChangedFileUtil {
    private static final Logger logger = LogManager.getLogger(ChangedFileUtil.class);

    public static boolean searchPotentialTestFiles(RevCommit curCommit, Git git, List<TestSourceFile> testSourceFiles,
                                                   List<ResourceOrConfigFile> resourceOrConfigFiles) throws Exception {
        if (curCommit == null) {
            return false;
        }
        // 前后找 5? 个 commit
        // 先往后找
        if (searchPotentialTestFilesFromChildren(curCommit, git, testSourceFiles, resourceOrConfigFiles,
                SEARCH_DEPTH)) {
            return true;
        }
        // 再往前找
        return searchPotentialTestFilesFromParents(curCommit, git, testSourceFiles, resourceOrConfigFiles,
                SEARCH_DEPTH);
    }

    private static boolean searchPotentialTestFilesFromParents(RevCommit curCommit, Git git,
                                                               List<TestSourceFile> testSourceFiles,
                                                               List<ResourceOrConfigFile> resourceOrConfigFiles,
                                                               int maxDepth) throws Exception {
        // 拓扑排序，找到 parent 的数据
        Queue<RevCommit> queue = new LinkedList<>();
        queue.offer(curCommit);
        while (!queue.isEmpty() && maxDepth > 0) {
            int levelSize = queue.size();
            while (levelSize-- > 0) {
                RevCommit commit = queue.poll();
                for (RevCommit parent : commit.getParents()) {
                    queue.offer(parent);
                }
                List<ChangedFile> files = getLastDiffFiles(commit, git);
                if (files == null) continue;
                List<TestSourceFile> tmpTestSourceFiles = getTestSourceFiles(files);
                List<SourceCodeFile> tmpNormalJavaFiles = getSourceCodeFiles(files);
                List<ResourceOrConfigFile> tmpResourceOrConfigFiles = getResourceOrConfigFiles(files);
                if (!tmpTestSourceFiles.isEmpty() && tmpNormalJavaFiles.isEmpty()) {
                    // 找到一个就行了？
                    testSourceFiles.addAll(tmpTestSourceFiles);
                    resourceOrConfigFiles.addAll(tmpResourceOrConfigFiles);
                    return true;
                }
            }
            maxDepth--;
        }
        return false;
    }

    private static boolean searchPotentialTestFilesFromChildren(RevCommit curCommit, Git git,
                                                                List<TestSourceFile> testSourceFiles,
                                                                List<ResourceOrConfigFile> resourceOrConfigFiles,
                                                                int maxDepth) throws Exception {
        // 反向拓扑排序，保证先找到最近的子 commit
        Map<ObjectId, List<RevCommit>> childrenMap = GitUtils.buildChildrenMap(git, curCommit);
        Queue<RevCommit> commitsToCheck = new LinkedList<>();
        commitsToCheck.add(curCommit);

        while (!commitsToCheck.isEmpty() && maxDepth > 0) {
            RevCommit commit = commitsToCheck.poll();
            List<RevCommit> children = childrenMap.get(commit.getId());
            if (children != null) {
                for (RevCommit child : children) {
                    // 将子提交加入到待检查队列中
                    commitsToCheck.offer(child);
                    List<ChangedFile> files = getLastDiffFiles(child, git);
                    if (files == null) continue;
                    List<TestSourceFile> tmpTestSourceFiles = getTestSourceFiles(files);
                    List<SourceCodeFile> tmpNormalJavaFiles = getSourceCodeFiles(files);
                    List<ResourceOrConfigFile> tmpResourceOrConfigFiles = getResourceOrConfigFiles(files);
                    if (!tmpTestSourceFiles.isEmpty() && tmpNormalJavaFiles.isEmpty()) {
                        // 找到一个就行了？
                        testSourceFiles.addAll(tmpTestSourceFiles);
                        resourceOrConfigFiles.addAll(tmpResourceOrConfigFiles);
                        return true;
                    }
                }
            }
            maxDepth--;
        }
        return false;
    }

    /**
     * 获取与父亲的差别
     *
     * @param commit
     * @return
     * @throws Exception
     */
    public static List<ChangedFile> getLastDiffFiles(RevCommit commit, Git git) throws Exception {
        List<ChangedFile> files = new LinkedList<>();
        ObjectId id = commit.getTree().getId();
        ObjectId oldId = null;
        if (commit.getParentCount() > 0) {
            oldId = Optional.ofNullable(commit.getParent(0).getTree() == null ? null :
                            commit.getParent(0).getTree().getId())
                    .orElseGet(() -> {
                        try (RevWalk revWalk = new RevWalk(git.getRepository())) {
                            return revWalk.parseCommit(ObjectId.fromString(commit.getParent(0).getName())).getTree().getId();
                        } catch (Exception e) {
                            logger.error("parse parent {} failed", commit.getParent(0).getName());
                            return null;
                        }
                    });
        } else {
            return null;
        }
        try (ObjectReader reader = git.getRepository().newObjectReader()) {
            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            oldTreeIter.reset(reader, oldId);
            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            newTreeIter.reset(reader, id);
            // finally get the list of changed files
            List<DiffEntry> diffs = git.diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();
            for (DiffEntry entry : diffs) {
                getChangedFile(entry, files, git);
            }
        }
        files.forEach(file -> file.setNewCommitId(commit.getName()));
        return files;
    }

    public static List<Edit> getEdits(DiffEntry entry, Git git) throws Exception {
        List<Edit> result = new LinkedList<>();
        try (DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE)) {
            diffFormatter.setRepository(git.getRepository());
            FileHeader fileHeader = diffFormatter.toFileHeader(entry);
            List<? extends HunkHeader> hunkHeaders = fileHeader.getHunks();
            for (HunkHeader hunk : hunkHeaders) {
                result.addAll(hunk.toEditList());
            }
        }
        return result;
    }

    /**
     * 任意两个diff之间的文件路径差别
     *
     * @param oldCommit
     * @param newCommit
     * @return
     * @throws Exception
     */
    public static List<ChangedFile> getDiffFiles(RevCommit oldCommit, RevCommit newCommit, Git git) throws Exception {
        List<ChangedFile> files = new LinkedList<>();
        ObjectId id = newCommit.getTree().getId();
        ObjectId oldId = oldCommit.getTree().getId();
        try (ObjectReader reader = git.getRepository().newObjectReader()) {
            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            oldTreeIter.reset(reader, oldId);
            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            newTreeIter.reset(reader, id);
            // finally get the list of changed files
            List<DiffEntry> diffs = git.diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();
            for (DiffEntry entry : diffs) {
                getChangedFile(entry, files, git);
            }
        }
        return files;
    }

    /**
     * 获取所有测试用例文件
     *
     * @param files
     * @return
     */
    public static List<TestSourceFile> getTestSourceFiles(List<ChangedFile> files) {
        return filterFilesByType(files, TestSourceFile.class);
    }

    public static List<SourceCodeFile> getSourceCodeFiles(List<ChangedFile> files) {
        return filterFilesByType(files, SourceCodeFile.class);
    }

    public static List<ResourceOrConfigFile> getResourceOrConfigFiles(List<ChangedFile> files) {
        if (files == null) {
            return List.of();
        }
        return files.stream()
                .filter(file -> !file.getNewPath().contains("pom.xml") && !file.getNewPath().equals(Constant.NONE_PATH))
                .filter(file -> file instanceof ResourceOrConfigFile)
                .map(file -> (ResourceOrConfigFile) file)
                .collect(Collectors.toList());
    }

    private static <T extends ChangedFile> List<T> filterFilesByType(List<ChangedFile> files, Class<T> type) {
        if (files == null) {
            return List.of();
        }
        return files.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .collect(Collectors.toList());
    }

    public static void getChangedFile(DiffEntry entry, List<ChangedFile> files, Git git) throws Exception {
        String path = entry.getNewPath();
        ChangedFile file;
        if (path.contains("test") && path.endsWith(".java")) {
            file = new TestSourceFile(entry.getNewPath());
        } else if (!path.contains("test") && path.endsWith(".java")) {
            file = new SourceCodeFile(entry.getNewPath());
        } else {
            file = new ResourceOrConfigFile(entry.getNewPath());
        }
        file.setOldPath(entry.getOldPath());
        file.setEditList(getEdits(entry, git));
        files.add(file);
    }

    public static List<RelatedTestCase> convertTestFilesToTestCaseXList(List<TestSuiteFile> testSuiteFileList) {
        List<RelatedTestCase> allTestCaseXs = new ArrayList<>();
        for (TestSuiteFile testSuiteFile : testSuiteFileList) {
            List<RelatedTestCase> testCaseXs = testSuiteFile.getTestCaseList();
            allTestCaseXs.addAll(testCaseXs);
        }
        return allTestCaseXs;
    }
}

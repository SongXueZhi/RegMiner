package org.regminer.miner;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.regminer.common.constant.Configurations;
import org.regminer.common.constant.Constant;
import org.regminer.common.model.*;
import org.regminer.common.tool.RepositoryProvider;
import org.regminer.miner.core.PBFCFilterStrategy;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PotentialBFCDetector extends PBFCFilterStrategy {
    private List<String> filterList;
    private static final int SEARCH_DEPTH = 5;


    public PotentialBFCDetector(List<String> filterList) {
        this.filterList = filterList;
    }

    public List<PotentialBFC> detectPotentialBFC() throws Exception {
        // 获取所有的commit，我们需要对所有的commit进行分析
        try (Repository repo = RepositoryProvider.getRepoFromLocal(new File(Configurations.projectPath)); Git git =
                new Git(repo)) {
            Iterable<RevCommit> commits = git.log().all().call();
            // 开始迭代每一个commit
            return detectAll(commits, git);
        }
    }


    private List<PotentialBFC> detectAll(Iterable<RevCommit> commits, Git git) throws Exception {
        List<PotentialBFC> potentialRFCs = new LinkedList<PotentialBFC>();
        // 定义需要记录的实验数据
        int countAll = 0;
        // 开始迭代每一个commit
        for (RevCommit commit : commits) {
            if (!filterList.isEmpty()) {
                if (this.filterList.contains(commit.getName())) {
                    detect(commit, potentialRFCs, git);
                }
            } else {
                detect(commit, potentialRFCs, git);
            }
            countAll++;
        }
        logger.info("total " + countAll + " commits in this project");
        logger.info("pRFC in total :" + potentialRFCs.size());
        return potentialRFCs;
    }

    private void detect(RevCommit commit, List<PotentialBFC> potentialRFCs, Git git) throws Exception {
        // 如果没有父亲，那么肯定不是bfc
        if (commit.getParentCount() <= 0) {
            return;
        }
        // 1)首先我们将记录所有的标题中包含fix的commit
        String message1 = commit.getFullMessage().toLowerCase();

        // 针对标题包含fix的commit我们进一步分析本次提交修改的文件路径
        List<ChangedFile> files = getLastDiffFiles(commit, git);
        if (files == null) return;
        List<TestFile> testcaseFiles = getTestFiles(files);
        List<NormalFile> normalJavaFiles = getNormalJavaFiles(files);
        List<SourceFile> sourceFiles = getSourceFiles(files);
        // 1）若所有路径中存在任意一个路径包含test相关的Java文件则我们认为本次提交中包含测试用例。
        // 2）若所有路径中除了测试用例还包含其他的非测试用例的Java文件则commit符合条件
        if (!testcaseFiles.isEmpty() && !normalJavaFiles.isEmpty()) {
            PotentialBFC pRFC = new PotentialBFC(commit);
            pRFC.setTestCaseFiles(testcaseFiles);
            pRFC.setTestcaseFrom(PotentialBFC.TESTCASE_FROM_SELF);
            pRFC.setNormalJavaFiles(normalJavaFiles);
            pRFC.setSourceFiles(sourceFiles);
            pRFC.fileMap.put("BASE", new File(Configurations.projectPath));
            potentialRFCs.add(pRFC);
        } else if (justNormalJavaFile(files) && (message1.contains("fix") || message1.contains("repair"))) {
            //TODO 张健 我觉得 所谓的测试搜索其实就是BFC Evaluation 的过程，当找不到测试pBFC就不是真的BFC。
            // 所以我建议把测试搜索的过程放在BFC Evaluation中
            PotentialBFC pRFC = new PotentialBFC(commit);
            pRFC.setNormalJavaFiles(normalJavaFiles);
            pRFC.setTestcaseFrom(PotentialBFC.TESTCASE_FROM_SEARCH);
            pRFC.fileMap.put("BASE", new File(Configurations.projectPath));
            // todo 可能在这里找不到测试，需要尝试在本次 commit 四周寻找是否有单独的新增测试
            if (searchPotentialTestFiles(commit, git, testcaseFiles, sourceFiles)) {
                pRFC.setTestCaseFiles(testcaseFiles);
                pRFC.setSourceFiles(sourceFiles);
            }
            potentialRFCs.add(pRFC);
        }
    }

    private boolean searchPotentialTestFiles(RevCommit curCommit, Git git, List<TestFile> testFiles,
                                             List<SourceFile> sourceFiles) throws Exception {
        if (curCommit == null) {
            return false;
        }
        // 前后找 5? 个 commit
        // 先往后找
        if (searchPotentialTestFilesFromChildren(curCommit, git, testFiles, sourceFiles, SEARCH_DEPTH)) {
            return true;
        }
        // 再往前找
        return searchPotentialTestFilesFromParents(curCommit, git, testFiles, sourceFiles, SEARCH_DEPTH);
    }

    private boolean searchPotentialTestFilesFromParents(RevCommit curCommit, Git git, List<TestFile> testFiles,
                                                        List<SourceFile> sourceFiles, int maxDepth) throws Exception {
        // todo 找到 parent 的数据，按理来说不需要迁移
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
                List<TestFile> tmpTestFiles = getTestFiles(files);
                List<NormalFile> tmpNormalJavaFiles = getNormalJavaFiles(files);
                List<SourceFile> tmpSourceFiles = getSourceFiles(files);
                if (!tmpTestFiles.isEmpty() && tmpNormalJavaFiles.isEmpty()) {
                    // 找到一个就行了？
                    testFiles.addAll(tmpTestFiles);
                    sourceFiles.addAll(tmpSourceFiles);
                    return true;
                }
            }
            maxDepth--;
        }
        return false;
    }

    private boolean searchPotentialTestFilesFromChildren(RevCommit curCommit, Git git, List<TestFile> testFiles,
                                                         List<SourceFile> sourceFiles, int maxDepth) throws Exception {
        // 反向拓扑排序，保证先找到最近的子 commit
        Map<ObjectId, List<RevCommit>> childrenMap = buildChildrenMap(git, curCommit);
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
                    List<TestFile> tmpTestFiles = getTestFiles(files);
                    List<NormalFile> tmpNormalJavaFiles = getNormalJavaFiles(files);
                    List<SourceFile> tmpSourceFiles = getSourceFiles(files);
                    if (!tmpTestFiles.isEmpty() && tmpNormalJavaFiles.isEmpty()) {
                        // 找到一个就行了？
                        testFiles.addAll(tmpTestFiles);
                        sourceFiles.addAll(tmpSourceFiles);
                        return true;
                    }
                }
            }
            maxDepth--;
        }
        return false;
    }

    private Map<ObjectId, List<RevCommit>> buildChildrenMap(Git git, RevCommit endCommit) throws IOException {
        Map<ObjectId, List<RevCommit>> map = new HashMap<>();
        try (RevWalk walk = new RevWalk(git.getRepository())) {
            // 获取当前分支的最新提交
            ObjectId branchHead = git.getRepository().resolve(git.getRepository().getBranch());
            walk.markStart(walk.parseCommit(branchHead));
            for (RevCommit commit : walk) {
                for (RevCommit parent : commit.getParents()) {
                    map.computeIfAbsent(parent.getId(), k -> new ArrayList<>()).add(commit);
                }
                // 只获取 child，对于更早的就没必要构建了
                if (commit.equals(endCommit)) break;
            }
        }
        return map;
    }

    /**
     * 获取与父亲的差别
     *
     * @param commit
     * @return
     * @throws Exception
     */
    private List<ChangedFile> getLastDiffFiles(RevCommit commit, Git git) throws Exception {
        List<ChangedFile> files = new LinkedList<>();
        ObjectId id = commit.getTree().getId();
        ObjectId oldId = null;
        if (commit.getParentCount() > 0) {
            oldId = Optional.ofNullable(commit.getParent(0).getTree() == null ? null : commit.getParent(0).getTree().getId())
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

    private List<Edit> getEdits(DiffEntry entry, Git git) throws Exception {
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
    private List<ChangedFile> getDiffFiles(RevCommit oldCommit, RevCommit newCommit, Git git) throws Exception {
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
     * 判断是否只有测试文件，如果所有的修改文件路径都包含test，认为所有的 被修改文件只与测试用例有关
     *
     * @param files
     * @return
     */
    private boolean justChangeTestFileOnly(List<ChangedFile> files) {
        int num = 0;
        int num_1 = 0;
        for (ChangedFile file : files) {
            String str = file.getNewPath().toLowerCase();
            if (!str.contains("test") && str.endsWith(".java")) {
                num++;
            }
            if (str.endsWith(".java")) {
                num_1++;
            }

        }
        return (num == 0 && num_1 > 0);
    }

    /**
     * 获取所有测试用例文件
     *
     * @param files
     * @return
     */
    private List<TestFile> getTestFiles(List<ChangedFile> files) {
        List<TestFile> testFiles = new LinkedList<>();
        if (files == null) {
            return testFiles;
        }
        for (ChangedFile file : files) {
            if (file instanceof TestFile) {
                testFiles.add((TestFile) file);
            }
        }
        return testFiles;
    }

    /**
     * 获取所有普通文件
     */
    private List<NormalFile> getNormalJavaFiles(List<ChangedFile> files) {
        List<NormalFile> normalJavaFiles = new LinkedList<>();
        for (ChangedFile file : files) {
            if (file instanceof NormalFile) {
                normalJavaFiles.add((NormalFile) file);
            }
        }
        return normalJavaFiles;
    }

    private List<SourceFile> getSourceFiles(List<ChangedFile> files) {
        List<SourceFile> sourceFiles = new LinkedList<>();
        for (ChangedFile file : files) {
            if (file.getNewPath().contains("pom.xml") || file.getNewPath().equals(Constant.NONE_PATH)) {
                continue;
            }
            if (file instanceof SourceFile) {
                sourceFiles.add((SourceFile) file);
            }
        }
        return sourceFiles;
    }

    private void getChangedFile(DiffEntry entry, List<ChangedFile> files, Git git) throws Exception {
        String path = entry.getNewPath();
        if (path.contains("test") && path.endsWith(".java")) {
            ChangedFile file = new TestFile(entry.getNewPath());
            file.setOldPath(entry.getOldPath());
            file.setEditList(getEdits(entry, git));
            files.add(file);
        }
        if ((!path.contains("test")) && path.endsWith(".java")) {
            ChangedFile file = new NormalFile(entry.getNewPath());
            file.setOldPath(entry.getOldPath());
            file.setEditList(getEdits(entry, git));
            files.add(file);
        }

//      if not end with ".java",it may be source file
        if (!path.endsWith(".java")) {
            ChangedFile file = new SourceFile(entry.getNewPath());
            file.setOldPath(entry.getOldPath());
            file.setEditList(getEdits(entry, git));
            files.add(file);
        }
    }

    /**
     * 判断全部都是普通的Java文件
     *
     * @param files
     * @return
     */
    private boolean justNormalJavaFile(List<ChangedFile> files) {
        for (ChangedFile file : files) {
            String str = file.getNewPath().toLowerCase();
            // 如果有一个文件路径中不包含test
            // 便立即返回false
            if (str.contains("test")) {
                return false;
            }
        }
        return true;
    }

    /**
     * @throws Exception
     */

    @Override
    public List<PotentialBFC> filter() throws Exception {
        return detectPotentialBFC();
    }
}

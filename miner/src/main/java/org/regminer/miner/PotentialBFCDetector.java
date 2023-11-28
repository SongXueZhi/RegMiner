package org.regminer.miner;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;

import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.regminer.miner.constant.Configurations;
import org.regminer.miner.constant.Constant;

import org.regminer.miner.model.*;
import org.regminer.miner.utils.FileUtilx;
import org.regminer.miner.utils.GitUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PotentialBFCDetector {


    public List<PotentialBFC> detectPotentialBFC() throws Exception {
        // 获取所有的commit，我们需要对所有的commit进行分析
        try (Repository repo = new FileRepository(Configurations.PROJECT_PATH); Git git = new Git(repo)) {
            Iterable<RevCommit> commits = git.log().all().call();
            // 开始迭代每一个commit
            return detectAll(commits,git);
        }
    }

    public List<PotentialBFC> detectPotentialBFC(List<String> commitsFilter,Git git) throws Exception {
        // 获取所有的commit，我们需要对所有的commit进行分析
        Iterable<RevCommit> commits = git.log().all().call();
        List<PotentialBFC> potentialRFCS = detectOnFilter(commitsFilter, commits,git);
        return potentialRFCS;
    }

    private List<PotentialBFC> detectAll(Iterable<RevCommit> commits,Git git) throws Exception {
        List<PotentialBFC> potentialRFCs = new LinkedList<PotentialBFC>();
        // 定义需要记录的实验数据
        int countAll = 0;
        // 开始迭代每一个commit
        for (RevCommit commit : commits) {
            detect(commit, potentialRFCs,git);
            countAll++;
        }
        FileUtilx.log("总共分析了" + countAll + "条commit\n");
        FileUtilx.log("pRFC in total :" + potentialRFCs.size());
        return potentialRFCs;
    }

    private List<PotentialBFC> detectOnFilter(List<String> commitsFilter, Iterable<RevCommit> commits,Git git) throws Exception {
        List<PotentialBFC> potentialRFCs = new LinkedList<PotentialBFC>();
        // 定义需要记录的实验数据
        int countAll = 0;
        // 开始迭代每一个commit
        for (RevCommit commit : commits) {
            if (commitsFilter.contains(commit.getName())) {
                detect(commit, potentialRFCs,git);
                countAll++;
            }
        }
        FileUtilx.log("总共分析了" + countAll + "条commit\n");
        FileUtilx.log("pRFC in total :" + potentialRFCs.size());
        return potentialRFCs;
    }

    /**
     * 获取与父亲的差别
     *
     * @param commit
     * @return
     * @throws Exception
     */
    private List<ChangedFile> getLastDiffFiles(RevCommit commit,Git git) throws Exception {
        List<ChangedFile> files = new LinkedList<>();
        ObjectId id = commit.getTree().getId();
        ObjectId oldId = null;
        if (commit.getParentCount() > 0) {
            oldId = commit.getParent(0).getTree().getId();
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
                getChangedFile(entry, files,git);
            }
        }
        return files;
    }

    private List<Edit> getEdits(DiffEntry entry,Git git ) throws Exception {
        List<Edit> result = new LinkedList<Edit>();
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
    private List<ChangedFile> getDiffFiles(RevCommit oldCommit, RevCommit newCommit,Git git) throws Exception {
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
                getChangedFile(entry, files,git);
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

    private void getChangedFile(DiffEntry entry, List<ChangedFile> files,Git git) throws Exception {
        String path = entry.getNewPath();
        if (path.contains("test") && path.endsWith(".java")) {
            ChangedFile file = new TestFile(entry.getNewPath());
            file.setOldPath(entry.getOldPath());
            file.setEditList(getEdits(entry,git));
            files.add(file);
        }
        if ((!path.contains("test")) && path.endsWith(".java")) {
            ChangedFile file = new NormalFile(entry.getNewPath());
            file.setOldPath(entry.getOldPath());
            file.setEditList(getEdits(entry,git));
            files.add(file);
        }

//      if not end with ".java",it may be source file
        if (!path.endsWith(".java")) {
            ChangedFile file = new SourceFile(entry.getNewPath());
            file.setOldPath(entry.getOldPath());
            file.setEditList(getEdits(entry,git));
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
     * @param commit
     * @param potentialRFCs
     * @throws Exception
     */
    private void detect(RevCommit commit, List<PotentialBFC> potentialRFCs,Git git) throws Exception {
        // 1)首先我们将记录所有的标题中包含fix的commti
        String message1 = commit.getFullMessage().toLowerCase();
//        if (message1.contains("fix") || message1.contains("close")) {
        if (true) {
            // 针对标题包含fix的commit我们进一步分析本次提交修改的文件路径
            List<ChangedFile> files = getLastDiffFiles(commit,git);
            if (files == null) return;
            List<TestFile> testcaseFiles = getTestFiles(files);
            List<NormalFile> normalJavaFiles = getNormalJavaFiles(files);
            List<SourceFile> sourceFiles = getSourceFiles(files);
            // 1）若所有路径中存在任意一个路径包含test相关的Java文件则我们认为本次提交中包含测试用例。
            // 2）若所有路径中除了测试用例还包含其他的非测试用例的Java文件则commit符合条件
            if (testcaseFiles.size() > 0 && normalJavaFiles.size() > 0) {
                PotentialBFC pRFC = new PotentialBFC(commit);
                pRFC.setTestCaseFiles(testcaseFiles);
                pRFC.setTestcaseFrom(PotentialBFC.TESTCASE_FROM_SELF);
                pRFC.setNormalJavaFiles(normalJavaFiles);
                pRFC.setSourceFiles(sourceFiles);
                potentialRFCs.add(pRFC);
            } else if (justNormalJavaFile(files) && (message1.contains("fix") || message1.contains("repair"))) {
//				针对只标题只包含fix但是修改的文件路径中没有测试用例的提交
//				我们将在(c-3,c+3) 的范围内检索可能的测试用例
//				[TODO] songxuezhi
                List<PotentialTestCase> pls = findTestCommit(commit,git);
                if (pls != null && pls.size() > 0) {
                    PotentialBFC pRFC = new PotentialBFC(commit);
                    pRFC.setNormalJavaFiles(normalJavaFiles);
                    pRFC.setTestcaseFrom(PotentialBFC.TESTCASE_FROM_SEARCH);
                    pRFC.setPotentialTestCaseList(pls);
                    potentialRFCs.add(pRFC);
                }
            }
        }
    }

    /**
     * 如果一个程序中仅包含了fix但没有测试用例，那么我们将在(-3,+3)中检索是否有单独的测试用例被提交
     *
     * @param commit
     * @return
     * @throws Exception
     */
    private List<PotentialTestCase> findTestCommit(RevCommit commit,Git git) throws Exception {
        List<PotentialTestCase> potentialTestCases = new ArrayList<>();
        RevWalk revWalk = new RevWalk(git.getRepository());
        // 树结构 ^2 ^1 c ～1 ～2
        // c^1
        ObjectId newId1 = git.getRepository().resolve(commit.getName() + "~1");
        RevCommit newRev1 = null;
        if (newId1 != null) {
            newRev1 = revWalk.parseCommit(newId1);
            List<ChangedFile> files = getDiffFiles(commit, newRev1,git);
            getPotentialTestCase(files, newRev1, 1, potentialTestCases,git);
        }

        // c^2
        ObjectId newId2 = git.getRepository().resolve(commit.getName() + "~2");
        RevCommit newRev2 = null;
        if (newId1 != null && newId2 != null) {
            newRev2 = revWalk.parseCommit(newId2);
            List<ChangedFile> files = getDiffFiles(newRev1, newRev2,git);
            // 是否只有测试用例
            getPotentialTestCase(files, newRev2, 2, potentialTestCases,git);
        }
        // c~1
        int num = commit.getParentCount();
        if (num > 1) {
            List<ChangedFile> files = getDiffFiles(commit.getParent(1), commit.getParent(0),git);
            getPotentialTestCase(files, null, -1, potentialTestCases,git);
            num--;
        }
        // c~2
        if (num > 1) {
            List<ChangedFile> files = getDiffFiles(commit.getParent(1), commit.getParent(0),git);
            getPotentialTestCase(files, null, -2, potentialTestCases,git);
            num--;
        }

        return potentialTestCases;
    }

    private void getPotentialTestCase(List<ChangedFile> files, RevCommit commit, int index,
                                      List<PotentialTestCase> potentialTestCaseList,Git git) throws Exception {
        if (!justChangeTestFileOnly(files)) {
            return;
        }
        PotentialTestCase potentialTestCase = new PotentialTestCase(index);
        List<TestFile> testFiles = getTestFiles(files);
        List<SourceFile> sourceFiles = getSourceFiles(files);

        potentialTestCase.setTestFiles(testFiles);
        potentialTestCase.setSourceFiles(sourceFiles);

        if (index > 0) {
            savePotentialTestFile(files, commit, potentialTestCase,git);
        }
        potentialTestCaseList.add(potentialTestCase);

    }

    private void savePotentialTestFile(List<ChangedFile> files, RevCommit commit, PotentialTestCase potentialTestCase
            ,Git git ) {
        for (ChangedFile changedFile : files) {
            String filePath = changedFile.getNewPath();
            if (!filePath.equals(Constant.NONE_PATH)) {
                File testFile =
                        new File(Configurations.TMP_FILE + File.separator + commit.getName() + File.separator + filePath);
                try {
                    FileUtils.writeStringToFile(testFile, GitUtil.getContextWithFile(git.getRepository(), commit, filePath));
                    potentialTestCase.fileMap.put(filePath, testFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

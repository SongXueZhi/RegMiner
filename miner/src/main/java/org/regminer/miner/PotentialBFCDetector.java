package org.regminer.miner;

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
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.regminer.common.constant.Configurations;
import org.regminer.common.constant.Constant;
import org.regminer.common.model.*;
import org.regminer.common.utils.FileUtilx;
import org.regminer.miner.core.PBFCFilterStrategy;

import java.util.LinkedList;
import java.util.List;

public class PotentialBFCDetector extends PBFCFilterStrategy {


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
    private List<ChangedFile> getLastDiffFiles(RevCommit commit, Git git) throws Exception {
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
        // 如果没有父亲，那么肯定不是bfc
        if (commit.getParentCount() <= 0) {
            return;
        }
        // 1)首先我们将记录所有的标题中包含fix的commti
        String message1 = commit.getFullMessage().toLowerCase();

        // 针对标题包含fix的commit我们进一步分析本次提交修改的文件路径
        List<ChangedFile> files = getLastDiffFiles(commit, git);
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
            PotentialBFC pRFC = new PotentialBFC(commit);
            pRFC.setNormalJavaFiles(normalJavaFiles);
            pRFC.setTestcaseFrom(PotentialBFC.TESTCASE_FROM_SEARCH);
            potentialRFCs.add(pRFC);
        }
    }

    @Override
    public List<PotentialBFC> filter() throws Exception {
        return detectPotentialBFC();
    }
}

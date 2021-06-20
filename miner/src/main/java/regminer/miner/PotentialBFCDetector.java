package regminer.miner;

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
import regminer.constant.Priority;
import regminer.model.*;
import regminer.utils.FileUtilx;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PotentialBFCDetector {

    private Repository repo;
    private Git git;

    public void setRepo(Repository repo) {
        this.repo = repo;
    }

    public void setGit(Git git) {
        this.git = git;
    }

    public PotentialBFCDetector() {

    }

    public PotentialBFCDetector(Repository repo, Git git) {
        this.repo = repo;
        this.git = git;
    }

    public List<PotentialRFC> detectPotentialBFC() throws Exception {
        // 获取所有的commit，我们需要对所有的commit进行分析
        Iterable<RevCommit> commits = git.log().all().call();
        // 开始迭代每一个commit
        return detectAll(commits);
    }

    public List<PotentialRFC> detectPotentialBFC(List<String> commitsFilter) throws Exception {
        // 获取所有的commit，我们需要对所有的commit进行分析
        Iterable<RevCommit> commits = git.log().all().call();
        return  detectOnFilter(commitsFilter,commits);

    }

    private List<PotentialRFC> detectAll(Iterable<RevCommit> commits) throws Exception {
        List<PotentialRFC> potentialRFCs = new LinkedList<PotentialRFC>();
        // 定义需要记录的实验数据
        int countAll = 0;
        // 开始迭代每一个commit
        for (RevCommit commit : commits) {
            detect(commit, potentialRFCs);
            countAll++;
        }
        FileUtilx.log("总共分析了" + countAll + "条commit\n");
        FileUtilx.log("pRFC in total :" + potentialRFCs.size());
        return potentialRFCs;
    }

    private List<PotentialRFC> detectOnFilter(List<String> commitsFilter, Iterable<RevCommit> commits) throws Exception {
        List<PotentialRFC> potentialRFCs = new LinkedList<PotentialRFC>();
        // 定义需要记录的实验数据
        int countAll = 0;
        // 开始迭代每一个commit
        for (RevCommit commit : commits) {
            if(commitsFilter.contains(commit.getName())){
                detect(commit, potentialRFCs);
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
    private List<ChangedFile> getLastDiffFiles(RevCommit commit) throws Exception {
        List<ChangedFile> files = new LinkedList<>();
        ObjectId id = commit.getTree().getId();
        ObjectId oldId = null;
        if (commit.getParentCount() > 0) {
            oldId = commit.getParent(0).getTree().getId();
        } else {
            return null;
        }
        try (ObjectReader reader = repo.newObjectReader()) {
            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            oldTreeIter.reset(reader, oldId);
            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            newTreeIter.reset(reader, id);
            // finally get the list of changed files
            List<DiffEntry> diffs = git.diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();
            for (DiffEntry entry : diffs) {
                getChangedFile(entry, files);
            }
        }
        return files;
    }

    private List<Edit> getEdits(DiffEntry entry) throws Exception {
        List<Edit> result = new LinkedList<Edit>();
        try (DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE)) {
            diffFormatter.setRepository(repo);
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
    private List<String> getDiffFiles(RevCommit oldCommit, RevCommit newCommit) throws Exception {
        List<String> files = new ArrayList<>();
        ObjectId id = newCommit.getTree().getId();
        ObjectId oldId = oldCommit.getTree().getId();
        try (ObjectReader reader = repo.newObjectReader()) {
            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            oldTreeIter.reset(reader, oldId);
            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            newTreeIter.reset(reader, id);
            // finally get the list of changed files
            List<DiffEntry> diffs = git.diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();
            for (DiffEntry entry : diffs) {
                files.add(entry.getNewPath());
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
    private boolean justChangeTestFileOnly(List<String> files) {
        for (String str : files) {
            str = str.toLowerCase();
            // 如果有一个文件路径中不包含test
            // 便立即返回false
            String[] strings = str.toLowerCase().split("/");
            if (!(str.contains("test") && strings[strings.length - 1].contains(".java"))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取所有测试用例文件
     *
     * @param files
     * @return
     */
    private List<TestFile> getTestFiles(List<ChangedFile> files) {
        List<TestFile> testFiles = new LinkedList<>();
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
            if (file instanceof SourceFile) {
                sourceFiles.add((SourceFile) file);
            }
        }
        return sourceFiles;
    }

    private void getChangedFile(DiffEntry entry, List<ChangedFile> files) throws Exception {
        String path = entry.getNewPath();
        if (path.contains("test") && path.endsWith(".java")) {
            ChangedFile file = new TestFile(entry.getNewPath());
            file.setOldPath(entry.getOldPath());
            file.setEditList(getEdits(entry));
            files.add(file);
        }
        if ((!path.contains("test")) && path.endsWith(".java")) {
            ChangedFile file = new NormalFile(entry.getNewPath());
            file.setOldPath(entry.getOldPath());
            file.setEditList(getEdits(entry));
            files.add(file);
        }

//      if not end with ".java",it may be source file
        if (!path.endsWith(".java")){
            ChangedFile file = new SourceFile(entry.getNewPath());
            file.setOldPath(entry.getOldPath());
            file.setEditList(getEdits(entry));
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
    private void detect(RevCommit commit, List<PotentialRFC> potentialRFCs) throws Exception {
        // 1)首先我们将记录所有的标题中包含fix的commti
        String message1 = commit.getShortMessage().toLowerCase();
        if (message1.contains("fix") || commit.getFullMessage().contains("Closes")) {
            // 针对标题包含fix的commit我们进一步分析本次提交修改的文件路径
            List<ChangedFile> files = getLastDiffFiles(commit);
            List<TestFile> testcaseFiles = getTestFiles(files);
            List<NormalFile> normalJavaFiles = getNormalJavaFiles(files);
            List<SourceFile> sourceFiles = getSourceFiles(files);
            // 1）若所有路径中存在任意一个路径包含test相关的Java文件则我们认为本次提交中包含测试用例。
            // 2）若所有路径中除了测试用例还包含其他的非测试用例的Java文件则commit符合条件
            if (testcaseFiles.size() > 0 && normalJavaFiles.size() > 0) {
                PotentialRFC pRFC = new PotentialRFC(commit);
                pRFC.setTestCaseFiles(testcaseFiles);
                pRFC.setNormalJavaFiles(normalJavaFiles);
                pRFC.setPriority(Priority.high);
                pRFC.setSourceFiles(sourceFiles);
                potentialRFCs.add(pRFC);
            } else if (justNormalJavaFile(files)) {
//				针对只标题只包含fix但是修改的文件路径中没有测试用例的提交 
//				我们将在(c-3,c+3) 的范围内检索可能的测试用例 
//				[TODO] songxuezhi			
//				List<PotentialTestCase> pls = findTestCommit(commit, repo);
//				if (pls.size() > 0) {
//							PotentialRFC pRFC = new PotentialRFC(commit.getName());
//							pRFC.setNormalJavaFiles(normalJavaFiles);
//							pRFC.setPotentialTestcases(pls);
//							pRFC.setPriority(Priority.middle);
//							potentialRFCs.add(pRFC);
//				}
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
    private List<PotentialTestCase> findTestCommit(RevCommit commit) throws Exception {
        List<PotentialTestCase> potentialTestCases = new ArrayList<>();
        RevWalk revWalk = new RevWalk(repo);
        // 树结构 ^2 ^1 c ～1 ～2
        // c^1
        ObjectId newId1 = repo.resolve(commit.getName() + "~1");
        RevCommit newRev1 = null;
        if (newId1 != null) {
            newRev1 = revWalk.parseCommit(newId1);
            // 寻找是不是只有testcase的提交
            // 有则说明是潜在的testcase的提交
            if (justChangeTestFileOnly(getDiffFiles(commit, newRev1))) {
                potentialTestCases.add(new PotentialTestCase(newRev1.getName(), 1));
            }
        }

        // c^2
        ObjectId newId2 = repo.resolve(commit.getName() + "~2");
        RevCommit newRev2 = null;
        if (newId1 != null && newId2 != null) {
            newRev2 = revWalk.parseCommit(newId2);
            // 是否只有测试用例
            if (justChangeTestFileOnly(getDiffFiles(newRev1, newRev2))) {
                potentialTestCases.add(new PotentialTestCase(newRev2.getName(), 2));
            }
        }
        // c~1
        int num = commit.getParentCount();
        if (num > 1) {
            if (justChangeTestFileOnly(getDiffFiles(commit.getParent(1), commit.getParent(0)))) {
                potentialTestCases.add(new PotentialTestCase(commit.getParent(0).getName(), -1));
            }
            num--;
        }
        // c~2
        if (num > 1) {
            if (justChangeTestFileOnly(getDiffFiles(commit.getParent(2), commit.getParent(1)))) {
                potentialTestCases.add(new PotentialTestCase(commit.getParent(1).getName(), -2));
            }
            num--;
        }

        return potentialTestCases;
    }
}

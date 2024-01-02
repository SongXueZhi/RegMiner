package org.regminer.miner;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.regminer.common.constant.Configurations;
import org.regminer.common.model.*;
import org.regminer.common.tool.RepositoryProvider;
import org.regminer.common.utils.ChangedFileUtil;
import org.regminer.miner.core.PBFCFilterStrategy;

import java.io.File;
import java.util.*;

public class PotentialBFCDetector extends PBFCFilterStrategy {
    private List<String> filterList;


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
        List<ChangedFile> files = ChangedFileUtil.getLastDiffFiles(commit, git);
        if (files == null) return;
        List<TestFile> testcaseFiles = ChangedFileUtil.getTestFiles(files);
        List<NormalFile> normalJavaFiles = ChangedFileUtil.getNormalJavaFiles(files);
        List<SourceFile> sourceFiles = ChangedFileUtil.getSourceFiles(files);
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
        } else if (ChangedFileUtil.justNormalJavaFile(files) && (message1.contains("fix") || message1.contains("repair"))) {
            PotentialBFC pRFC = new PotentialBFC(commit);
            pRFC.setNormalJavaFiles(normalJavaFiles);
            pRFC.setTestcaseFrom(PotentialBFC.TESTCASE_FROM_SEARCH);
            pRFC.fileMap.put("BASE", new File(Configurations.projectPath));
            pRFC.setTestCaseFiles(new ArrayList<>());
            pRFC.setSourceFiles(new ArrayList<>());
            potentialRFCs.add(pRFC);
        }
    }

    /**
     * @throws Exception
     */

    @Override
    public List<PotentialBFC> filter() throws Exception {
        return detectPotentialBFC();
    }
}

package org.regminer.miner;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.regminer.commons.constant.Configurations;
import org.regminer.commons.model.*;
import org.regminer.commons.tool.RepositoryProvider;
import org.regminer.commons.utils.ChangedFileUtil;
import org.regminer.miner.core.PBFCFilterStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PotentialBFCDetector extends PBFCFilterStrategy {
    private final List<String> filterList;


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
        List<PotentialBFC> potentialRFCs = new ArrayList<>();

        boolean hasFilter = !filterList.isEmpty();

        List<RevCommit> commitList = StreamSupport.stream(commits.spliterator(), false)
                .collect(Collectors.toList());

        int countAll = commitList.size();

        commitList.stream()
                .filter(commit -> !hasFilter || filterList.contains(commit.getName()))
                .forEach(commit -> {
                    try {
                        detect(commit, potentialRFCs, git);
                    } catch (Exception e) {
                        logger.error("Error processing commit {}: {}", commit.getName(), e.getMessage());
                    }
                });

        logger.info(() -> String.format("Total %d commits in this project", countAll));
        logger.info(() -> String.format("Potential BFCs in total: %d", potentialRFCs.size()));

        return potentialRFCs;
    }

    private void detect(RevCommit commit, List<PotentialBFC> potentialRFCs, Git git) throws Exception {
        // 如果没有父亲，那么肯定不是bfc
        if (commit.getParentCount() <= 0) {
            return;
        }

        // 获取提交消息并转换为小写
        String message = commit.getFullMessage().toLowerCase();

        // 获取本次提交修改的文件路径
        List<ChangedFile> files = ChangedFileUtil.getLastDiffFiles(commit, git);
        if (files == null || files.isEmpty()) {
            return;
        }

        // 分类文件,这次是粗分只是根据文件后缀名来判断
        List<TestSourceFile> testcaseFiles = ChangedFileUtil.getTestSourceFiles(files);
        List<SourceCodeFile> normalJavaFiles = ChangedFileUtil.getSourceCodeFiles(files);
        List<ResourceOrConfigFile> resourceOrConfigFiles = ChangedFileUtil.getResourceOrConfigFiles(files);

        // 判断是否包含测试用例和普通Java文件
        boolean hasTestFiles = !testcaseFiles.isEmpty();
        boolean hasNormalJavaFiles = !normalJavaFiles.isEmpty();
        boolean hasFixInMessage = message.contains("fix") || message.contains("repair");

        if (hasTestFiles && hasNormalJavaFiles) {
            // 创建 PotentialBFC 对象
            PotentialBFC pRFC = createPotentialBFC(commit, testcaseFiles, normalJavaFiles, resourceOrConfigFiles,
                    PotentialBFC.TESTCASE_FROM_SELF);
            potentialRFCs.add(pRFC);
        } else if (hasNormalJavaFiles && hasFixInMessage) {
            // 创建 PotentialBFC 对象
            PotentialBFC pRFC = createPotentialBFC(commit, new ArrayList<>(), normalJavaFiles, new ArrayList<>(),
                    PotentialBFC.TESTCASE_FROM_SEARCH);
            potentialRFCs.add(pRFC);
        }
    }

    private PotentialBFC createPotentialBFC(RevCommit commit, List<TestSourceFile> testFiles,
                                            List<SourceCodeFile> sourceFiles,
                                            List<ResourceOrConfigFile> resourceFiles, int testcaseFrom) {
        PotentialBFC pRFC = new PotentialBFC(commit.getName());
        pRFC.setCommit(commit);
        pRFC.setTestSourceFiles(testFiles);
        pRFC.setSourceCodeFiles(sourceFiles);
        pRFC.setResourceOrConfigFiles(resourceFiles);
        pRFC.setTestcaseFrom(testcaseFrom);
        pRFC.fileMap.put("BASE", Configurations.projectPath);
        return pRFC;
    }

    /**
     * @throws Exception
     */

    @Override
    public List<PotentialBFC> filter() throws Exception {
        return detectPotentialBFC();
    }
}

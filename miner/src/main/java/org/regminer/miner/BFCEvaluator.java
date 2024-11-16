package org.regminer.miner;

import org.apache.commons.lang3.tuple.Triple;
import org.regminer.commons.constant.Configurations;
import org.regminer.commons.constant.Constant;
import org.regminer.commons.model.PotentialBFC;
import org.regminer.commons.sql.BugStorage;
import org.regminer.commons.tool.SycFileCleanup;
import org.regminer.commons.utils.MigratorUtil;
import org.regminer.ct.api.AutoCompileAndTest;
import org.regminer.ct.api.CtContext;
import org.regminer.ct.api.OriginCompileFixWay;
import org.regminer.ct.api.TestCaseParser;
import org.regminer.ct.model.CommitBuildResult;
import org.regminer.ct.model.CompileResult;
import org.regminer.ct.model.TestCaseResult;
import org.regminer.ct.model.TestResult;
import org.regminer.ct.utils.TestUtils;
import org.regminer.migrate.api.TestCaseMigrator;
import org.regminer.miner.core.BFCSearchStrategy;
import org.regminer.miner.monitor.ProgressMonitor;

import java.io.File;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class BFCEvaluator extends BFCSearchStrategy {

    TestCaseParser testCaseParser;
    TestCaseMigrator testCaseMigrator;
    BugStorage bugStorage;

    public BFCEvaluator(TestCaseParser testCaseParser, TestCaseMigrator testCaseMigrator) {
        this.testCaseParser = testCaseParser;
        this.testCaseMigrator = testCaseMigrator;
        bugStorage = new BugStorage();
    }

    @Deprecated
    public void evoluteBFCList(List<PotentialBFC> potentialRFCList) {
        List<PotentialBFC> validPotentialRFCList = potentialRFCList.stream()
                .filter(potentialRFC -> {
                    try {
                        evaluate(potentialRFC);
                        if (potentialRFC.getTestSuiteFiles() == null || potentialRFC.getTestSuiteFiles().isEmpty()) {
                            return false;
                        }
                        bugStorage.saveBFC(potentialRFC, "bfcs_enhancement");
                        return true;
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        return false;
                    }
                })
                .collect(Collectors.toList());
        potentialRFCList.clear();
        potentialRFCList.addAll(validPotentialRFCList);
        logger.info("Processed {} RFCs", validPotentialRFCList.size());
    }

    public boolean evaluate(PotentialBFC pRFC) {
        boolean result = false;
        String bfcID = pRFC.getCommit().getName();
        try {
            if (!prepareBFC(pRFC, bfcID))
                return result;

            testCaseParser.parseTestCases(pRFC);
            if (pRFC.getTestSuiteFiles() == null || pRFC.getTestSuiteFiles().isEmpty()) {
                logger.error("BFC has no test case");
                return result;
            }

            Triple<Boolean, CompileResult, CtContext> compileResultTriple = compileBFC(pRFC);
            if (!Boolean.TRUE.equals(compileResultTriple.getLeft()))
                return result;

            if (!testBFC(pRFC, compileResultTriple.getMiddle(), compileResultTriple.getRight()))
                return result;

            if (!chooseBFCP(pRFC, bfcID))
                return result;
            result = true;
        } catch (Exception e) {
            handleException(pRFC, bfcID, e);
        }
        return result;
    }

    private boolean prepareBFC(PotentialBFC pRFC, String bfcID) {
        try {
            logger.info("{} checkout ", bfcID);
            logger.info("commit message:{}", pRFC.getCommit().getShortMessage());
            logger.info("before analysis, test source files size: {}",
                    pRFC.getTestSourceFiles() == null ? 0 : pRFC.getTestSourceFiles().size());
            File bfcDirectory = MigratorUtil.checkoutCiForBFC(bfcID, bfcID);
            pRFC.fileMap.put(bfcID, bfcDirectory.getAbsolutePath());
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    private Triple<Boolean, CompileResult, CtContext> compileBFC(PotentialBFC pRFC) {
        try {
            String bfcID = pRFC.getCommit().getName();
            CtContext ctContext = new CtContext(new AutoCompileAndTest());
            ctContext.setProjectDir(Path.of(pRFC.fileMap.get(bfcID)).toFile());

            CompileResult compileResult = ctContext.compile(OriginCompileFixWay.values());
            CommitBuildResult.originalCompileResult.putIfAbsent(bfcID, compileResult);

            if (compileResult.getState() == CompileResult.CompileState.CE) {
                if (pRFC.getTestSourceFiles() != null) {
                    pRFC.getTestSourceFiles().clear();
                }
                logger.info("BFC compile error");
                return Triple.of(false, compileResult, ctContext);
            }
            return Triple.of(true, compileResult, ctContext);
        } catch (Exception ex) {
            return Triple.of(false, null, null);
        }
    }

    private boolean testBFC(PotentialBFC pRFC, CompileResult compileResult, CtContext ctContext) {
        // 5. 测试BFC
        TestResult testResult = testCaseMigrator.test(pRFC.getTestSuiteFiles(), ctContext,
                compileResult.getCompileWay());
        TestUtils.retainTestFilesMatchingStates(pRFC, testResult, List.of(TestCaseResult.TestState.PASS));
        if (pRFC.getTestSuiteFiles().isEmpty()) {
            logger.error("BFC all test fal");
            return false;
        }
        return true;
    }

    private boolean chooseBFCP(PotentialBFC pRFC, String bfcID) {
        boolean findBFCPFlag = false;
        try {
            int count = pRFC.getCommit().getParentCount();
            if (count == 0) {
                logger.error("BFC has no parent");
                return false;
            }
            for (int i = 0; i < count; i++) {
                String bfcpID = pRFC.getCommit().getParent(i).getName();
                logger.info("bfc~1 {} of {}", bfcpID, bfcID);
                TestResult bfcpTestResult = testCaseMigrator.migrateAndTest(pRFC, bfcpID);
                if (bfcpTestResult == null) { // 这说明编译失败
                    continue;
                }
                Set<String> matchTestCase = TestUtils.collectTestCases(bfcpTestResult,
                        testState -> Arrays.asList(TestCaseResult.TestState.FAL,
                                TestCaseResult.TestState.TE).contains(testState))
                        .keySet();

                if (matchTestCase.isEmpty()) {
                    logger.info("no test case match");
                    continue;
                }
                // 查找成功，删除无关的测试用例
                TestUtils.retainTestFilesMatchingFilter(pRFC, matchTestCase);
                logger.info(bfcpTestResult.getCaseResultMap());
                logger.info("bfc~1 test fal");
                // TODO:REDUCE
                findBFCPFlag = true;
                pRFC.setBuggyCommitId(bfcpID);
                try {
                    storeException(bfcpTestResult, bfcpID, bfcID);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
                break; // 跳出，找到一个就够了
            }
            if (!findBFCPFlag) {
                if (pRFC.getTestSuiteFiles() != null) {
                    pRFC.getTestSuiteFiles().clear();
                }
                logger.info("Can't find a bfc-1");
            }
        } catch (Exception exception) {
            logger.error(exception.getMessage());
        }
        return findBFCPFlag;
    }

    // private void updateProgress(String bfcID) {
    // if (Configurations.taskName.equals(Constant.BFC_TASK)) {
    // ProgressMonitor.updateState(bfcID);
    // }
    // }

    private void storeException(TestResult bfcpTestResult, String bfcpID, String bfcID) {
        File excepProjDir = new File(Configurations.exceptionUrl + File.separator + Configurations.projectName);
        logger.info("exception dir: {}", excepProjDir.getPath());
        if (!excepProjDir.exists()) {
            boolean r = excepProjDir.mkdirs();
            logger.info("create exception dir: {} {}", r, excepProjDir.getPath());
        }

        AtomicInteger cnt = new AtomicInteger(0);
        Set<String> uniqueCommands = new HashSet<>();
        bfcpTestResult.getCaseResultMap().forEach((k, v) -> {// store exception msg to file
            if ((v.getState() == TestCaseResult.TestState.FAL || v.getState() == TestCaseResult.TestState.TE) &&
                    (v.getTestCommands() != null && !uniqueCommands.contains(v.getTestCommands()))) {
                if (v.getExceptionMessage() == null || v.getExceptionMessage().isEmpty()) {
                    logger.info("exception msg is empty for {}", k);
                    return;
                }
                uniqueCommands.add(v.getTestCommands());
                String storePath = excepProjDir.getAbsolutePath() + File.separator + bfcpID.substring(0, 7) + '_'
                        + bfcID.substring(0, 7) + '_' + cnt.get() + ".txt";
                logger.info("stored to {} with {}", storePath, v.getTestCommands());
                v.execMsgToFile(storePath);
                cnt.getAndIncrement();
            }
        });

        if (cnt.get() > 0) {
            logger.info("bfc~1 exception msg stored");
        } else {
            logger.info("no exception msg stored");
        }
    }

    private void handleException(PotentialBFC pRFC, String bfcID, Exception e) {
        if (pRFC.getTestSuiteFiles() == null) {
            logger.error("pbfc test case is null");
            pRFC.setTestSuiteFiles(new ArrayList<>());
        }
        logger.info("pbfc test case size: {}", pRFC.getTestSuiteFiles().size());
        logger.error(e.getMessage());
    }

    @Override
    public void searchRealBFC(List<PotentialBFC> potentialBFCs) {
        evoluteBFCList(potentialBFCs);
    }

    @Override
    public boolean confirmPBFCtoBFC(PotentialBFC potentialBFC) {
        boolean isBFC = evaluate(potentialBFC);
        boolean checkCondition = (potentialBFC.getTestSuiteFiles() == null || potentialBFC.getTestSuiteFiles().isEmpty()
                || potentialBFC.joinTestcaseString().replace(";", "").isEmpty()
                || potentialBFC.getBuggyCommitId() == null || potentialBFC.getBuggyCommitId().isEmpty());
        isBFC = isBFC && !checkCondition;
        if (isBFC) {
            bugStorage.saveBFC(potentialBFC, "bfcs_enhancement");
        }
        return isBFC;
    }
}

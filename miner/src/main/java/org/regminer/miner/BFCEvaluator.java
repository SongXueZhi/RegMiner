package org.regminer.miner;

import org.apache.commons.lang3.tuple.Triple;
import org.regminer.common.constant.Configurations;
import org.regminer.common.constant.Constant;
import org.regminer.common.model.PotentialBFC;
import org.regminer.common.sql.BugStorage;
import org.regminer.common.tool.SycFileCleanup;
import org.regminer.common.utils.MigratorUtil;
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
import java.util.*;

public class BFCEvaluator extends BFCSearchStrategy {

    TestCaseParser testCaseParser;
    TestCaseMigrator testCaseMigrator;
    BugStorage bugStorage;

    public BFCEvaluator(TestCaseParser testCaseParser, TestCaseMigrator testCaseMigrator) {
        this.testCaseParser = testCaseParser;
        this.testCaseMigrator = testCaseMigrator;
        bugStorage = new BugStorage();
    }

    /**
     * Firstly,checkout BFC ,and manage BFC DIR In the map
     * Then try the code coverage feature
     * sort BFC
     *
     * @param potentialRFCList potential BFC list
     */
    public void evoluteBFCList(List<PotentialBFC> potentialRFCList) {
        Iterator<PotentialBFC> iterator = potentialRFCList.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            PotentialBFC potentialRFC = iterator.next();
            try {
                evolute(potentialRFC);
                if (potentialRFC.getTestCaseFiles() == null || potentialRFC.getTestCaseFiles().isEmpty()) {
                    iterator.remove();
                    continue;
                }
                ++i;
                logger.info("pRFC total: {}", i);
                bugStorage.saveBFC(potentialRFC);
            } catch (Exception e) {
                logger.error(e.getMessage());
                iterator.remove();
            }
        }
    }

    public void evolute(PotentialBFC pRFC) {
        String bfcID = pRFC.getCommit().getName();
        try {
            if (!prepareBFC(pRFC, bfcID)) return;

            Triple<Boolean, CompileResult, CtContext> compileResultTriple = compileBFC(pRFC, bfcID);
            if (!Boolean.TRUE.equals(compileResultTriple.getLeft())) return;

            if (!testBFC(pRFC, bfcID, compileResultTriple.getMiddle(), compileResultTriple.getRight())) return;

            if (!chooseBFCP(pRFC, bfcID)) return;

            updateProgress(bfcID);
        } catch (Exception e) {
            handleException(pRFC, bfcID, e);
        } finally {
            cleanUp(pRFC, bfcID);
        }
    }

    private boolean prepareBFC(PotentialBFC pRFC, String bfcID) {
        try {
            logger.info("{} checkout ", bfcID);
            logger.info("commit message:{}", pRFC.getCommit().getShortMessage());
            logger.info("before analysis, test case size: {}", pRFC.getTestCaseFiles() == null ? 0 : pRFC.getTestCaseFiles().size());
            File bfcDirectory = MigratorUtil.checkoutCiForBFC(bfcID, bfcID);
            pRFC.fileMap.put(bfcID, bfcDirectory);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    private Triple<Boolean, CompileResult, CtContext> compileBFC(PotentialBFC pRFC, String bfcID) {
        try {
            CtContext ctContext = new CtContext(new AutoCompileAndTest());
            File bfcDirectory = pRFC.fileMap.get(bfcID);
            ctContext.setProjectDir(bfcDirectory);

            CompileResult compileResult = ctContext.compile(OriginCompileFixWay.values());
            CommitBuildResult.originalCompileResult.putIfAbsent(bfcID, compileResult);

            if (compileResult.getState() == CompileResult.CompileState.CE) {
                if (pRFC.getTestCaseFiles() != null) {
                    pRFC.getTestCaseFiles().clear();
                }
                logger.info("BFC compile error");
                return Triple.of(false, compileResult, ctContext);
            }
            return Triple.of(true, compileResult, ctContext);
        } catch (Exception ex) {
            return Triple.of(false, null, null);
        }
    }

    private boolean testBFC(PotentialBFC pRFC, String bfcID, CompileResult compileResult, CtContext ctContext) {
        //4. parser Testcase
        testCaseParser.parseTestCases(pRFC);
        //5. 测试BFC
        TestResult testResult = testCaseMigrator.test(pRFC.getTestCaseFiles(), ctContext,
                compileResult.getCompileWay());
        TestUtils.retainTestFilesMatchingStates(pRFC, testResult, List.of(TestCaseResult.TestState.PASS));
        if (pRFC.getTestCaseFiles().isEmpty()) {
            logger.error("BFC all test fal");
            return false;
        }
        return true;
    }

    private boolean chooseBFCP(PotentialBFC pRFC, String bfcID) throws Exception {
        //6. 选择BFCP
        int count = pRFC.getCommit().getParentCount();
        if (count == 0) {
            logger.error("BFC has no parent");
            return false;
        }

        boolean findBFCPFlag = false;
        for (int i = 0; i < count; i++) {
            String bfcpID = pRFC.getCommit().getParent(i).getName();
            logger.info("bfc~1 {} of {}", bfcpID, bfcID);
            TestResult bfcpTestResult = testCaseMigrator.migrateAndTest(pRFC, bfcpID);
            if (bfcpTestResult == null) { //这说明编译失败
                continue;
            }

            Set<String> matchTestCase = TestUtils.collectTestCases(bfcpTestResult,
                    testState -> Arrays.asList(TestCaseResult.TestState.FAL,
                            TestCaseResult.TestState.TE).contains(testState)).keySet();

            if (matchTestCase.isEmpty()) {
                continue;
            }
            //查找成功，删除无关的测试用例
            TestUtils.retainTestFilesMatchingFilter(pRFC, matchTestCase);
            logger.info(bfcpTestResult.getCaseResultMap());
            logger.info("bfc~1 test fal");
            MigratorUtil.purgeUnlessTestcase(pRFC.getTestCaseFiles(), pRFC);
            // REDUCE
            findBFCPFlag = true;
            pRFC.setBuggyCommitId(bfcpID);
            break;  //跳出，找到一个就够了
        }
        if (!findBFCPFlag) {
            if (pRFC.getTestCaseFiles() != null) {
                pRFC.getTestCaseFiles().clear();
            }
            logger.info("Can't find a bfc-1");
            return false;
        }
        return true;
    }

    private void updateProgress(String bfcID) {
        if (Configurations.taskName.equals(Constant.BFC_TASK)) {
            ProgressMonitor.updateState(bfcID);
        }
    }

    private void handleException(PotentialBFC pRFC, String bfcID, Exception e) {
        if (pRFC.getTestCaseFiles() == null) {
            logger.error("pbfc test case is null");
            pRFC.setTestCaseFiles(new ArrayList<>());
        }
        logger.info("pbfc test case size: {}", pRFC.getTestCaseFiles().size());
        logger.error(e.getMessage());
        emptyCache(pRFC.fileMap.get(bfcID).getParentFile());
    }

    private void cleanUp(PotentialBFC pRFC, String bfcID) {
        if (Configurations.taskName.equals(Constant.BFC_TASK)) {
            emptyCache(pRFC.fileMap.get(bfcID).getParentFile());
        }
    }

    public void emptyCache(File file) {
        new SycFileCleanup().cleanDirectory(file.getParentFile());
    }

    @Override
    public void searchRealBFC(List<PotentialBFC> potentialBFCs) {
        evoluteBFCList(potentialBFCs);
    }

    @Override
    public boolean confirmPBFCtoBFC(PotentialBFC potentialBFC) {
        boolean isBFC = true;
        evolute(potentialBFC);
        if (potentialBFC.getTestCaseFiles() == null || potentialBFC.getTestCaseFiles().isEmpty()
                || potentialBFC.joinTestcaseString().replace(";", "").isEmpty()
                || potentialBFC.getBuggyCommitId() == null || potentialBFC.getBuggyCommitId().isEmpty()
        ) {
            isBFC = false;
        }
        if (isBFC) {
            bugStorage.saveBFC(potentialBFC);
        }
        return isBFC;
    }
}

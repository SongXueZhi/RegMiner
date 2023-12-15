package org.regminer.miner;

import org.regminer.common.constant.Configurations;
import org.regminer.common.constant.Constant;
import org.regminer.common.model.PotentialBFC;
import org.regminer.common.tool.SycFileCleanup;
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

import java.io.File;
import java.util.*;

public class BFCEvaluator extends BFCSearchStrategy {

    TestCaseParser testCaseParser;
    TestCaseMigrator testCaseMigrator;

    public BFCEvaluator(TestCaseParser testCaseParser, TestCaseMigrator testCaseMigrator) {
        this.testCaseParser = testCaseParser;
        this.testCaseMigrator = testCaseMigrator;
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
                logger.info("pRFC total:" + i);
            } catch (Exception e) {
                logger.error(e.getMessage());
                iterator.remove();
            }
        }
    }


    public void evolute(PotentialBFC pRFC) {
        String bfcID = pRFC.getCommit().getName();
        try {
            // 1.checkout bfc
            logger.info("{} checkout ", bfcID);
            logger.info("commit message:{}",pRFC.getCommit().getShortMessage());
            logger.info("before analysis, test case size: {}", pRFC.getTestCaseFiles().size());
            File bfcDirectory = testCaseMigrator.checkoutCiForBFC(bfcID, bfcID);
            pRFC.fileMap.put(bfcID, bfcDirectory);
            //2. 尝试编译BFC
            CtContext ctContext = new CtContext(new AutoCompileAndTest());
            ctContext.setProjectDir(bfcDirectory);

            CompileResult compileResult = ctContext.compile(OriginCompileFixWay.values());
            CommitBuildResult.originalCompileResult.putIfAbsent(bfcID, compileResult);

            if (compileResult.getState() == CompileResult.CompileState.CE) {
                if (pRFC.getTestCaseFiles() != null) {
                    pRFC.getTestCaseFiles().clear();
                }
                logger.info("BFC compile error");
                emptyCache(pRFC.fileMap.get(bfcID));
                return;
            }
            //3. parser Testcase
            testCaseParser.parseTestCases(pRFC);
            //4. 测试BFC
            TestResult testResult = testCaseMigrator.test(pRFC.getTestCaseFiles(), ctContext,
                    compileResult.getCompileWay());

            TestUtils.retainTestFilesMatchingStates(pRFC, testResult, Arrays.asList(TestCaseResult.TestState.PASS));

            if (pRFC.getTestCaseFiles().isEmpty()) {
                logger.error("BFC all test fal");
                emptyCache(pRFC.fileMap.get(bfcID));
                return;
            }

            //5. 选择BFCP
            int count = pRFC.getCommit().getParentCount();
            if (count == 0) {
                logger.error("BFC has no parent");
                emptyCache(pRFC.fileMap.get(bfcID));
                return;
            }

            boolean findBFCPFlag = false;
            for (int i = 0; i < count; i++) {
                String bfcpID = pRFC.getCommit().getParent(i).getName();
                TestResult bfcpTestResult = testCaseMigrator.migrate(pRFC, bfcpID);
                if (bfcpTestResult == null) { //这说明编译失败
                    continue;
                }

                Set<String> matchTestCase = TestUtils.collectTestCases(bfcpTestResult,
                        testState ->  Arrays.asList(TestCaseResult.TestState.FAL,
                                TestCaseResult.TestState.TE).contains(testState)).keySet();

                if (matchTestCase.isEmpty()){
                 continue;
                }
                //查找成功，删除无关的测试用例
                TestUtils.retainTestFilesMatchingFilter(pRFC, matchTestCase);
                logger.info(bfcpTestResult.getCaseResultMap());
                logger.info("bfc~1 test fal");
                testCaseMigrator.purgeUnlessTestcase(pRFC.getTestCaseFiles(), pRFC);//XXX:TestDenpendency:TEST
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
                emptyCache(pRFC.fileMap.get(bfcID));
                return;
            }
        } catch (Exception e) {
            if (pRFC.getTestCaseFiles() == null) {
                logger.error("pbfc test case is null");
                pRFC.setTestCaseFiles(new ArrayList<>());
            }
            logger.info("pbfc test case size: {}", pRFC.getTestCaseFiles().size());
            pRFC.getTestCaseFiles().clear();
            emptyCache(pRFC.fileMap.get(bfcID));
            logger.error("Exception msg: {}", e.getMessage());
            e.printStackTrace();
        }
        finally {
            if (Configurations.taskName.equals(Constant.BFC_TASK)) {
                emptyCache(pRFC.fileMap.get(bfcID));
            }
        }
    }


    public void emptyCache(File file) {
        new SycFileCleanup().cleanDirectory(file.getParentFile());
    }

    @Override
    public void searchRealBFC(List<PotentialBFC> potentialBFCs) {
        evoluteBFCList(potentialBFCs);
    }
}

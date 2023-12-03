package org.regminer.miner;

import org.regminer.common.constant.Configurations;
import org.regminer.common.model.PotentialBFC;
import org.regminer.common.tool.SycFileCleanup;
import org.regminer.common.utils.FileUtilx;
import org.regminer.ct.api.AutoCompileAndTest;
import org.regminer.ct.api.CtContext;
import org.regminer.ct.api.TestCaseParser;
import org.regminer.ct.model.CompileResult;
import org.regminer.ct.model.TestCaseResult;
import org.regminer.ct.model.TestResult;
import org.regminer.ct.utils.TestUtils;
import org.regminer.migrate.api.TestCaseMigrator;
import org.regminer.miner.core.BFCSearchStrategy;
import org.slf4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BFCEvaluator extends BFCSearchStrategy {
    protected Logger logger = org.slf4j.LoggerFactory.getLogger(BFCEvaluator.class);
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
                if (potentialRFC.getTestCaseFiles().isEmpty()) {
                    iterator.remove();
                    continue;
                }
                ++i;
                FileUtilx.log("pRFC total:" + i);
                iterator.remove();
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
            logger.info(bfcID + " checkout ");
            File bfcDirectory = testCaseMigrator.checkoutCiForBFC(bfcID, bfcID);
            pRFC.fileMap.put(bfcID, bfcDirectory);
            //2. 尝试编译BFC
            CtContext ctContext = new CtContext(new AutoCompileAndTest());
            ctContext.setProjectDir(bfcDirectory);
            CompileResult compileResult = ctContext.compile();
            if (compileResult.getState() == CompileResult.CompileState.CE) {
                pRFC.getTestCaseFiles().clear();
                logger.info("BFC compile error");
                emptyCache(bfcID);
                return;
            }
            //3. parser Testcase
            testCaseParser.parseTestCases(pRFC);
            //4. 测试BFC
            TestResult testResult = testCaseMigrator.test(pRFC.getTestCaseFiles(), ctContext,
                    compileResult.getEnvCommands());

            TestUtils.removeTestFilesInBFC(pRFC, testResult, TestCaseResult.TestState.FAL);

            if (pRFC.getTestCaseFiles().isEmpty()) {
                logger.error("BFC all test fal");
                emptyCache(bfcID);
                return;
            }

            //5. 选择BFCP
            int count = pRFC.getCommit().getParentCount();
            if (count == 0) {
                logger.error("BFC has no parent");
                emptyCache(bfcID);
                return;
            }

            boolean findBFCPFlag = false;
            for (int i = 0; i < count; i++) {
                String bfcpID = pRFC.getCommit().getParent(i).getName();
                TestResult bfcpTestResult = testCaseMigrator.migrate(pRFC, bfcpID);

                if (bfcpTestResult == null) { //这说明编译失败
                    logger.info("BFC~1 compile error");
                    continue;
                }
                TestUtils.removeTestFilesInBFC(pRFC, bfcpTestResult, TestCaseResult.TestState.PASS);

                if (!pRFC.getTestCaseFiles().isEmpty()) {
                    //查找成功，删除无关的测试用例
                    //跳出，找到一个就够了
                    logger.info("bfc~1 test fal");
                    testCaseMigrator.purgeUnlessTestcase(pRFC.getTestCaseFiles(), pRFC);//XXX:TestDenpendency:TEST
                    // REDUCE
                    findBFCPFlag = true;
                    pRFC.setBuggyCommitId(bfcpID);
                    break;
                }
            }
            if (!findBFCPFlag) {
                pRFC.getTestCaseFiles().clear();
                logger.info("Can't find a bfc-1");
                emptyCache(bfcID);
                return;
            }
        } catch (Exception e) {
            if (pRFC.getTestCaseFiles() == null) {
                pRFC.setTestCaseFiles(new ArrayList<>());
            }
            pRFC.getTestCaseFiles().clear();
            emptyCache(bfcID);
            logger.error(e.getMessage());
        }
    }


    public void emptyCache(String bfcID) {
        File bfcFile = new File(Configurations.CACHE_PATH + File.separator + bfcID);
        new SycFileCleanup().cleanDirectory(bfcFile);
    }

    @Override
    public void searchRealBFC(List<PotentialBFC> potentialBFCs) {
        evoluteBFCList(potentialBFCs);
    }
}

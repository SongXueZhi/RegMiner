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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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
                if (potentialRFC.getTestCaseFiles().isEmpty()) {
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
            logger.info(bfcID + " checkout ");
            logger.info("commit message:{}",pRFC.getCommit().getShortMessage());
            File bfcDirectory = testCaseMigrator.checkoutCiForBFC(bfcID, bfcID);
            pRFC.fileMap.put(bfcID, bfcDirectory);
            //2. 尝试编译BFC
            CtContext ctContext = new CtContext(new AutoCompileAndTest());
            ctContext.setProjectDir(bfcDirectory);

            CompileResult compileResult = ctContext.compile(OriginCompileFixWay.values());
            CommitBuildResult.originalCompileResult.putIfAbsent(bfcID, compileResult);

            if (compileResult.getState() == CompileResult.CompileState.CE) {
                pRFC.getTestCaseFiles().clear();
                logger.info("BFC compile error");
                emptyCache(pRFC.fileMap.get(bfcID));
                return;
            }
            //3. parser Testcase
            testCaseParser.parseTestCases(pRFC);
            //4. 测试BFC
            TestResult testResult = testCaseMigrator.test(pRFC.getTestCaseFiles(), ctContext,
                    compileResult.getCompileWay());

            //TODO:张建 判断搜有的测试如果都是NONTEST 则需要使用其他的测试方式
            TestUtils.removeTestFilesInBFCNotMeet(pRFC, testResult, TestCaseResult.TestState.PASS);

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
                //TODO:张建 判断搜有的测试如果都是NONTEST 则需要使用其他的测试方式
                TestUtils.removeTestFilesInBFCMeet(pRFC, bfcpTestResult, Arrays.asList(TestCaseResult.TestState.PASS,
                                TestCaseResult.TestState.CE, TestCaseResult.TestState.NOMARK,
                                TestCaseResult.TestState.UNKNOWN, TestCaseResult.TestState.NOTEST)
                        );
                logger.info(bfcpTestResult.getCaseResultMap());
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
                emptyCache(pRFC.fileMap.get(bfcID));
                return;
            }
        } catch (Exception e) {
            if (pRFC.getTestCaseFiles() == null) {
                pRFC.setTestCaseFiles(new ArrayList<>());
            }
            pRFC.getTestCaseFiles().clear();
            emptyCache(pRFC.fileMap.get(bfcID));
            logger.error(e.getMessage());
        }
        finally {
            if (Configurations.taskName.equals(Constant.BFC_TASK)) {
                emptyCache(pRFC.fileMap.get(bfcID));
            }
        }
    }


    public void emptyCache(File file) {
        new SycFileCleanup().cleanDirectory(file);
    }

    @Override
    public void searchRealBFC(List<PotentialBFC> potentialBFCs) {
        evoluteBFCList(potentialBFCs);
    }
}

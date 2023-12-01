package org.regminer.miner.migrate;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.*;
import org.regminer.common.constant.Configurations;
import org.regminer.common.model.PotentialBFC;
import org.regminer.common.model.TestFile;
import org.regminer.common.utils.FileUtilx;
import org.regminer.ct.api.AutoCompileAndTest;
import org.regminer.ct.api.CtContext;
import org.regminer.ct.model.CompileResult;
import org.regminer.ct.model.TestCaseResult;
import org.regminer.ct.model.TestResult;
import org.regminer.ct.utils.TestUtils;
import org.regminer.miner.TestCaseParser;
import org.regminer.miner.finalize.SycFileCleanup;
import org.regminer.miner.utils.CompilationUtil;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BFCEvaluator extends TestCaseMigrator {
    TestCaseParser testCaseParser = new TestCaseParser();


    protected Logger logger = org.slf4j.LoggerFactory.getLogger(BFCEvaluator.class);

    /**
     * Firstly,checkout BFC ,and manage BFC DIR In the map
     * Then try the code coverage feature
     * sort BFC
     *
     * @param potentialRFCList
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
                e.printStackTrace();
                iterator.remove();
            }
        }
    }


    public void evolute(PotentialBFC pRFC) {
        String bfcID = pRFC.getCommit().getName();
        try {
            // 1.checkout bfc
            logger.info(bfcID + " checkout ");
            File bfcDirectory = checkoutCiForBFC(bfcID, bfcID);
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
            TestResult testResult = test(pRFC.getTestCaseFiles(), ctContext, compileResult.getEnvCommands());

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
                TestResult bfcpTestResult = migrate(pRFC, bfcpID);

                if (bfcpTestResult == null) { //这说明编译失败
                    logger.info("BFC~1 compile error");
                    continue;
                }
                TestUtils.removeTestFilesInBFC(pRFC, bfcpTestResult, TestCaseResult.TestState.PASS);

                if (!pRFC.getTestCaseFiles().isEmpty()) {
                    //查找成功，删除无关的测试用例
                    //跳出，找到一个就够了
                    logger.info("bfc~1 test fal");
                    purgeUnlessTestcase(pRFC.getTestCaseFiles(), pRFC);//XXX:TestDenpendency:TEST REDUCE
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

    public void purgeUnlessTestcase(List<TestFile> testSuiteList, PotentialBFC pRFC) {
        File bfcDir = pRFC.fileMap.get(pRFC.getCommit().getName());
        for (TestFile testFile : testSuiteList) {
            String path = testFile.getNewPath();
            File file = new File(bfcDir, path);
            try {
                CompilationUnit unit = CompilationUtil.parseCompliationUnit(FileUtils.readFileToString(file,
                        "UTF-8"));
                Set<String> testCaseSet = testFile.getTestMethodMap().keySet();
                List<TypeDeclaration> types = unit.types();
                for (TypeDeclaration type : types) {
                    MethodDeclaration[] mdArray = type.getMethods();
                    for (int i = 0; i < mdArray.length; i++) {
                        MethodDeclaration method = mdArray[i];
                        String name = method.getName().toString();
                        List<ASTNode> parameters = method.parameters();
                        // SingleVariableDeclaration
                        StringJoiner sj = new StringJoiner(",", name + "(", ")");
                        for (ASTNode param : parameters) {
                            sj.add(param.toString());
                        }
                        String signature = sj.toString();
                        if ((method.toString().contains("@Test") || name.startsWith("test") || name.endsWith("test")) && !testCaseSet.contains(signature)) {
                            method.delete();
                        }
                    }
                }
                List<ImportDeclaration> imports = unit.imports();
                int len = imports.size();
                ImportDeclaration[] importDeclarations = new ImportDeclaration[len];
                for (int i = 0; i < len; i++) {
                    importDeclarations[i] = imports.get(i);
                }

                for (ImportDeclaration importDeclaration : importDeclarations) {
                    String importName = importDeclaration.getName().getFullyQualifiedName();
                    if (importName.lastIndexOf(".") > -1) {
                        importName = importName.substring(importName.lastIndexOf(".") + 1);
                    } else {
                        importName = importName;
                    }

                    boolean flag = false;
                    for (TypeDeclaration type : types) {
                        if (type.toString().contains(importName)) {
                            flag = true;
                        }
                    }
                    if (!(flag || importDeclaration.toString().contains("*"))) {
                        importDeclaration.delete();
                    }
                }
                if (file.exists()) {
                    file.delete();
                }
                FileUtils.writeStringToFile(file, unit.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

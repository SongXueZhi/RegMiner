package org.regminer.miner.migrate;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jgit.lib.Repository;
import org.jetbrains.annotations.NotNull;
import org.regminer.miner.RelatedTestCaseParser;
import org.regminer.miner.constant.Configurations;
import org.regminer.miner.constant.ExperResult;
import org.regminer.miner.coverage.CodeCoverage;
import org.regminer.miner.coverage.model.CoverNode;
import org.regminer.miner.finalize.SycFileCleanup;
import org.regminer.miner.maven.JacocoMavenManager;
import org.regminer.miner.model.ChangedFile.Type;
import org.regminer.miner.model.MigrateItem;
import org.regminer.miner.model.PotentialBFC;
import org.regminer.miner.model.RelatedTestCase;
import org.regminer.miner.model.TestFile;
import org.regminer.miner.utils.CompilationUtil;
import org.regminer.miner.utils.FileUtilx;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BFCEvaluator extends Migrator {

    Repository repo;
    String projectName = Configurations.PROJRCT_NAME;
    JacocoMavenManager jacocoMavenManager = new JacocoMavenManager();
    CodeCoverage codeCoverage = new CodeCoverage();
    BFCTracker tracker = new BFCTracker();
    RelatedTestCaseParser testCaseParser = new RelatedTestCaseParser();

    public BFCEvaluator(Repository repo) {
        this.repo = repo;
    }

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
            FileUtilx.log(bfcID + " checkout ");
            File bfcDirectory = checkout(bfcID, bfcID, "bfc");
            pRFC.fileMap.put(bfcID, bfcDirectory);

            //2. parser Testcase
            testCaseParser.parseTestCases(pRFC);

            // 3. verity have bfc~1
            if (pRFC.getCommit().getParentCount() <= 0) {
                FileUtilx.log("BFC no parent");
                pRFC.getTestCaseFiles().clear();
                emptyCache(bfcID);
                return;
            }

            //4. checkout bfc~1
            String bfcpID = pRFC.getCommit().getParent(0).getName();
            File bfcpDirectory = checkout(bfcID, bfcpID, "bfcp");// 管理每一个commit的文件路径
            pRFC.fileMap.put(bfcpID, bfcpDirectory);

            // 4.将BFC中所有与测试相关的文件迁移到BFCP,与BIC查找中的迁移略有不同
            // BFC到BFCP的迁移不做依赖分析,相关就迁移
            // 因为后续BFC的测试用例确认会删除一些TESTFILE,所以先迁移
            copyToTarget(pRFC, bfcpDirectory);

            // 4.compile BFC
            if (!compile(bfcDirectory, false)) {
                pRFC.getTestCaseFiles().clear();
                FileUtilx.log("BFC compile error");
                emptyCache(bfcID);
                return;
            }

            // 5. 测试BFC中的每一个待测试方法
//            System.out.println("before testing, bfcp test case size:" + pRFC.getTestCaseFiles().size());
            testBFC(bfcDirectory, pRFC);
//            System.out.println("after testing, bfcp test case size:" + pRFC.getTestCaseFiles().size());

            if (pRFC.getTestCaseFiles().isEmpty()) {
                FileUtilx.log("BFC all test fal");
                emptyCache(bfcID);
                return;
            }

            // 7.编译并测试BFCP
            if (!compile(bfcpDirectory, false)) {
                pRFC.getTestCaseFiles().clear();
                FileUtilx.log("BFC~1 compile error");
                emptyCache(bfcID);
                return;
            }
            // 6.测试BFCP
            String result = testBFCP(bfcpDirectory, pRFC.getTestCaseFiles());

            if (!pRFC.getTestCaseFiles().isEmpty()) {
                ExperResult.numSuc++;
                //删除无关的测试用例
                purgeUnlessTestcase(pRFC.getTestCaseFiles(), pRFC);//XXX:TestDenpendency:TEST REDUCE
                FileUtilx.log("bfc~1 test fal" + result);
            } else {
                FileUtilx.log("bfc~1 test success" + result);
                emptyCache(bfcID);
                return;
            }

            // Test buggy test in BFC get Method coverage
            if (Configurations.code_cover) {
                double rfcProb = testWithJacoco(bfcDirectory, pRFC.getTestCaseFiles());
                pRFC.setScore(rfcProb);
                FileUtilx.apendResultToFile(bfcID + "," + rfcProb + "," + combinedRegressionTestResult(pRFC), new File("bfcscore.csv"));
                emptyCache(bfcID);
            }

            pRFC.setBuggyCommitId(bfcpID);
            exec.setDirectory(new File(Configurations.PROJECT_PATH));
        } catch (Exception e) {
            if (pRFC.getTestCaseFiles()==null){
                pRFC.setTestCaseFiles(new ArrayList<>());
            }
            pRFC.getTestCaseFiles().clear();
            emptyCache(bfcID);
            e.printStackTrace();
        }
    }

    public String combinedRegressionTestResult(PotentialBFC pRFC) {
        StringJoiner sj = new StringJoiner(";", "", "");
        for (TestFile tc : pRFC.getTestCaseFiles()) {
            Map<String, RelatedTestCase> methodMap = tc.getTestMethodMap();
            if (methodMap == null) {
                continue;
            }
            for (Iterator<Map.Entry<String, RelatedTestCase>> it = methodMap.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, RelatedTestCase> entry = it.next();
                String testCase = tc.getQualityClassName() + Configurations.methodClassLinkSymbolForTest
                        + entry.getKey().split("[(]")[0];
                sj.add(testCase);
            }
        }
        return sj.toString();
    }

    public double testWithJacoco(File bfcDirectory, List<TestFile> testFiles) throws Exception {
        //add Jacoco plugin
        try {
            jacocoMavenManager.addJacocoFeatureToMaven(bfcDirectory);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        testSuite(bfcDirectory, testFiles);
        // git test coverage methods
        List<CoverNode> coverNodeList = codeCoverage.readJacocoReports(bfcDirectory);
        if (coverNodeList == null) {
            return -1;
        }
        return tracker.regressionProbCalculate(tracker.handleTasks(coverNodeList, bfcDirectory));
    }

    public boolean compile(File file, boolean record) {
        exec.setDirectory(file);
        return exec.execBuildWithResult(Configurations.compileLine, record);
    }

    public void testBFC(File file, PotentialBFC pRFC) throws Exception {
        // 一定要先设置当前文件路径
        exec.setDirectory(file);
        // 开始测试
        Iterator<TestFile> iter = pRFC.getTestCaseFiles().iterator();
        while (iter.hasNext()) {
            TestFile testFile = iter.next();

            if (testFile.getType() == Type.TEST_SUITE) {
                testSuite(testFile);
            } else {
                iter.remove();// 只保留测试文件
                continue;
            }
            Map<String, RelatedTestCase> testMethodsMap = testFile.getTestMethodMap();
            if (testMethodsMap == null || testMethodsMap.isEmpty()) {
                iter.remove(); // 如果该测试文件中没有测试成功的方法,则该TestFile移除
            }
        }
    }

    public void testSuite(TestFile testFile) {
        Map<String, RelatedTestCase> methodMap = testFile.getTestMethodMap();
        if (methodMap != null && !methodMap.isEmpty()) {
            testMethod(methodMap, testFile.getQualityClassName());
        }
    }

    public void testMethod(Map<String, RelatedTestCase> methodMap, String qualityClassName) {
        // 遍历BFC测试文件中的每一个方法,并执行测试,测试失败即移除
        for (Iterator<Map.Entry<String, RelatedTestCase>> it = methodMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, RelatedTestCase> entry = it.next();
            String testCase = qualityClassName + Configurations.methodClassLinkSymbolForTest + entry.getKey().split("[(]")[0];
            MigrateItem.MigrateFailureType type = exec.execTestWithResult(Configurations.testLine + testCase);
            if (type != MigrateItem.MigrateFailureType.TESTSUCCESS) {
                it.remove();
            }
        }
    }

    public String testBFCP(File file, List<TestFile> realTestCase) {
        exec.setDirectory(file);
        StringJoiner sj = new StringJoiner(";", "[", "]");
        Iterator<TestFile> iterator = realTestCase.iterator();
        while (iterator.hasNext()) {
            TestFile testSuite = iterator.next();
            testBFCPMethod(testSuite, sj);
            if (testSuite.getTestMethodMap().isEmpty()) {
                iterator.remove();
            }
        }
        return sj.toString();
    }

    public void testBFCPMethod(TestFile testSuite, StringJoiner sj) {
        Map<String, RelatedTestCase> methodMap = testSuite.getTestMethodMap();
        for (Iterator<Map.Entry<String, RelatedTestCase>> it = methodMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, RelatedTestCase> entry = it.next();
            String testCase = testSuite.getQualityClassName() + Configurations.methodClassLinkSymbolForTest
                    + entry.getKey().split("[(]")[0];
            MigrateItem.MigrateFailureType type = exec.execTestWithResult(Configurations.testLine + testCase);
            sj.add(testCase + ":" + type.getName());
            if (type != MigrateItem.MigrateFailureType.NONE) {
                it.remove();
            }
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
            if (file.exists()){
                file.delete();
            }
                FileUtils.writeStringToFile(file, unit.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void testSuite(File file, @NotNull List<TestFile> testSuites) {
        exec.setDirectory(file);
        Iterator<TestFile> iterator = testSuites.iterator();
        while (iterator.hasNext()) {
            TestFile testSuite = iterator.next();
            testMethod(testSuite);
        }

    }

    public void testMethod(@NotNull TestFile testSuite) {
        Map<String, RelatedTestCase> methodMap = testSuite.getTestMethodMap();
        for (Iterator<Map.Entry<String, RelatedTestCase>> it = methodMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, RelatedTestCase> entry = it.next();
            String testCase = testSuite.getQualityClassName() + Configurations.methodClassLinkSymbolForTest
                    + entry.getKey().split("[(]")[0];
            exec.execTestWithResult(Configurations.testLine + testCase);
        }
    }
}

package regminer.miner.migrate;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jgit.lib.Repository;
import regminer.constant.Conf;
import regminer.constant.ExperResult;
import regminer.coverage.CodeCoverage;
import regminer.coverage.model.CoverNode;
import regminer.finalize.SycFileCleanup;
import regminer.maven.JacocoMavenManager;
import regminer.model.ChangedFile.Type;
import regminer.model.MigrateItem.MigrateFailureType;
import regminer.model.PotentialRFC;
import regminer.model.RelatedTestCase;
import regminer.model.TestFile;
import regminer.utils.CompilationUtil;
import regminer.utils.FileUtilx;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TestCaseDeterminer extends Migrator {
    final static int COVER_THRESHOLD = 3;
    int i = 0;
    int j = 0;
    Repository repo;
    String projectName = Conf.PROJRCT_NAME;
    JacocoMavenManager jacocoMavenManager = new JacocoMavenManager();
    CodeCoverage codeCoverage = new CodeCoverage();
    BFCTracker tracker = new BFCTracker();

    public TestCaseDeterminer(Repository repo) {
        this.repo = repo;
    }

    public void determine(PotentialRFC pRFC) throws Exception {

        // 1.准备BFC
        String bfcID = pRFC.getCommit().getName();
        FileUtilx.log(bfcID + "开始执行测试约减");
        File bfcDirectory = checkout(bfcID, bfcID, "bfc");
        pRFC.fileMap.put(bfcID, bfcDirectory); // 管理每一个commit的文件路径

        // 2.准备BFCP
        if (pRFC.getCommit().getParentCount() <= 0) { // 首先判断BFCP是否存在
            emptyCache(bfcID);
        }
        String bfcpID = pRFC.getCommit().getParent(0).getName();
        File bfcpDirectory = checkout(bfcID, bfcpID, "bfcp");// 管理每一个commit的文件路径
        pRFC.fileMap.put(bfcpID, bfcpDirectory);

        // 4.将BFC中所有与测试相关的文件迁移到BFCP,与BIC查找中的迁移略有不同
        // BFC到BFCP的迁移不做依赖分析,相关就迁移
        // 因为后续BFC的测试用例确认会删除一些TESTFILE,所以先迁移
        copyToTarget(pRFC, bfcpDirectory);

        // 4.编译BFC
        if (!comiple(bfcDirectory, false)) {
            pRFC.getTestCaseFiles().clear();
            FileUtilx.log("BFC构建失败");
            emptyCache(bfcID);
            return;
        }

        // 5. 测试BFC中的每一个待测试方法
        if (Conf.code_cover) {
           if(!testWithJacoco(bfcDirectory,pRFC)){
               return;
           }
        } else {
            testBFC(bfcDirectory, pRFC);
        }

        if (pRFC.getTestCaseFiles().size() <= 0) {
            FileUtilx.log("BFC 没有测试成功的方法");
            emptyCache(bfcID);
            return;
        }

        // 7.编译并测试BFCP
        if (!comiple(bfcpDirectory, true)) {
            pRFC.getTestCaseFiles().clear();
            FileUtilx.log("BFCp迁移后编译失败");
            emptyCache(bfcID);
            return;
        }
        // 6.测试BFCP
        String result = testBFCP(bfcpDirectory, pRFC.getTestCaseFiles());

        if (pRFC.getTestCaseFiles().size() > 0) {
            ExperResult.numSuc++;
            //删除无关的测试用例
            purgeUnlessTestcase(pRFC.getTestCaseFiles(), pRFC);
            FileUtilx.log("迁移成功" + result);
        } else {
            FileUtilx.log("迁移失败" + result);
            emptyCache(bfcID);
            return;
        }
        exec.setDirectory(new File(Conf.PROJECT_PATH));
//		ExperResult.numSuc++;
    }

    public boolean testWithJacoco(File bfcDirectory, PotentialRFC pRFC) throws Exception {
        //add Jacoco plugin
        try {
            jacocoMavenManager.addJacocoFeatureToMaven(bfcDirectory);
        } catch (Exception e) {
            return true;
        }
        testBFC(bfcDirectory, pRFC);
        // git test coverage methods
        List<CoverNode> coverNodeList = codeCoverage.readJacocoReports(bfcDirectory);
        float cmfr = tracker.effectiveMethodAverageCoverage(tracker.handleTasks(coverNodeList, bfcDirectory));
        return cmfr > COVER_THRESHOLD;
    }

    public boolean comiple(File file, boolean record) throws Exception {
        exec.setDirectory(file);
        return exec.execBuildWithResult(Conf.compileLine, record);
    }

    public void testBFC(File file, PotentialRFC pRFC) throws Exception {
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
            Map testMethodsMap = testFile.getTestMethodMap();
            if (testMethodsMap == null || testMethodsMap.size() == 0) {
                iter.remove(); // 如果该测试文件中没有测试成功的方法,则该TestFile移除
            }
        }
    }

    public void testSuite(TestFile testFile) throws Exception {
        Map<String, RelatedTestCase> methodMap = testFile.getTestMethodMap();
        if (methodMap != null && methodMap.size() > 0) {
            testMethod(methodMap, testFile.getQualityClassName());
        }
    }

    public void testMethod(Map<String, RelatedTestCase> methodMap, String qualityClassName) throws Exception {
        // 遍历BFC测试文件中的每一个方法,并执行测试,测试失败即移除
        for (Iterator<Map.Entry<String, RelatedTestCase>> it = methodMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, RelatedTestCase> entry = it.next();
            String testCase = qualityClassName + Conf.methodClassLinkSymbolForTest + entry.getKey().split("[(]")[0];
            MigrateFailureType type = exec.execTestWithResult(Conf.testLine + testCase);
            if (type != MigrateFailureType.TESTSUCCESS) {
                it.remove();
            }
        }
    }

    public String testBFCP(File file, List<TestFile> realTestCase) throws Exception {
        exec.setDirectory(file);
        StringJoiner sj = new StringJoiner(";", "[", "]");
        Iterator<TestFile> iterator = realTestCase.iterator();
        while (iterator.hasNext()) {
            TestFile testSuite = iterator.next();
            testBFCPMethod(testSuite, sj);
            if (testSuite.getTestMethodMap().size() == 0) {
                iterator.remove();
            }
        }
        return sj.toString();
    }

    public void testBFCPMethod(TestFile testSuite, StringJoiner sj) throws Exception {
        Map<String, RelatedTestCase> methodMap = testSuite.getTestMethodMap();
        for (Iterator<Map.Entry<String, RelatedTestCase>> it = methodMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, RelatedTestCase> entry = it.next();
            String testCase = testSuite.getQualityClassName() + Conf.methodClassLinkSymbolForTest
                    + entry.getKey().split("[(]")[0];
            MigrateFailureType type = exec.execTestWithResult(Conf.testLine + testCase);
            sj.add(testCase + ":" + type.getName());
            if (type != MigrateFailureType.NONE) {
                it.remove();
            }
        }
    }


    public void emptyCache(String bfcID) {
        File bfcFile = new File(Conf.CACHE_PATH + File.separator + bfcID);
        new SycFileCleanup().cleanDirectory(bfcFile);
    }

    public void purgeUnlessTestcase(List<TestFile> testSuiteList, PotentialRFC pRFC) {
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
                FileUtils.forceDeleteOnExit(file);
                FileUtils.writeStringToFile(file, unit.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

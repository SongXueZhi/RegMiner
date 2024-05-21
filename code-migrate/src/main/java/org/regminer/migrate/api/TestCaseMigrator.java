package org.regminer.migrate.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.regminer.commons.model.*;
import org.regminer.commons.utils.*;
import org.regminer.ct.api.AutoCompileAndTest;
import org.regminer.ct.api.CtContext;
import org.regminer.ct.api.OriginCompileFixWay;
import org.regminer.ct.model.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sxz
 */
public class TestCaseMigrator {
    protected Logger logger = LogManager.getLogger(TestCaseMigrator.class);

    public TestResult migrateAndTest(PotentialBFC pRFC, String bic) throws Exception {
        logger.info("start to migrate in {}", pRFC.getCommit().getName());
        File bicDirectory = MigratorUtil.checkoutCiForBFC(pRFC.getCommit().getName(), bic);
        pRFC.fileMap.put(bic, bicDirectory.getAbsolutePath());
        CtContext ctContext = new CtContext(new AutoCompileAndTest());
        ctContext.setProjectDir(bicDirectory);

        CompileResult compileResult =
                CommitBuildResult.originalCompileResult.containsKey(bic) ?
                        CommitBuildResult.originalCompileResult.get(bic) :
                        ctContext.compile(OriginCompileFixWay.values());
        CommitBuildResult.originalCompileResult.putIfAbsent(bic, compileResult);

        if (compileResult.getState() == CompileResult.CompileState.CE) {
            logger.debug("compile error before migrate");
            return null;
        }
        List<TestSuiteFile> testSuiteFiles = new ArrayList<>(pRFC.getTestSuiteFiles());
        TestResult finalResult = new TestResult();
        List<TestSuiteFile> ctTestSuiteFiles = new ArrayList<>();
        // 多个测试文件同时迁移容易导致编译失败，逐个尝试
        for (TestSuiteFile testSuiteFile : testSuiteFiles) {
            List<TestSourceFile> curTestSourceFiles = new ArrayList<>();
            curTestSourceFiles.add(testSuiteFile);
            pRFC.setTestSourceFiles(curTestSourceFiles);
            MigratorUtil.mergeTwoVersion_BaseLine(pRFC, bicDirectory);
            // 编译
            compileResult = compile(bic, ctContext, compileResult);
            // 编译成功后执行测试
            if (compileResult.getState() == CompileResult.CompileState.SUCCESS) {
                TestResult result = test(pRFC.getTestSuiteFiles(), ctContext, compileResult.getCompileWay());
                if (result.getCaseResultMap().values().stream().allMatch(testCaseResult -> testCaseResult.getState() == TestCaseResult.TestState.CE)) {
                    // 恢复原始情况
                    bicDirectory = MigratorUtil.checkoutCiForBFC(pRFC.getCommit().getName(), bic);
                    ctTestSuiteFiles.add(testSuiteFile);
                } else {
                    finalResult.getCaseResultMap().putAll(result.getCaseResultMap());
                }
            } else {
                // 恢复原始情况
                bicDirectory = MigratorUtil.checkoutCiForBFC(pRFC.getCommit().getName(), bic);
                ctTestSuiteFiles.add(testSuiteFile);
            }
        }
        if (finalResult.isEmpty()) {
            migrateTestMethodAndTest(pRFC, bic, ctTestSuiteFiles, ctContext, compileResult, finalResult);
        }
        // 恢复测试文件列表，接下来的流程还会用到
        pRFC.setTestSuiteFiles(testSuiteFiles);
        return finalResult;
    }

    private void migrateTestMethodAndTest(PotentialBFC pRFC, String bic, List<TestSuiteFile> ceTestSuitedFiles,
                                          CtContext ctContext, CompileResult compileResult, TestResult finalResult) throws IOException {
        File bicDirectory = MigratorUtil.checkoutCiForBFC(pRFC.getCommit().getName(), bic);

        Map<String, String> path2ContentMap = new HashMap<>();
        List<TestSourceFile> underTestDirJavaFiles = pRFC.getTestDependFile();
        List<ResourceOrConfigFile> resourceOrConfigFiles = pRFC.getResourceOrConfigFiles();
        MergeTask mergeJavaFileTask = new MergeTask();
        mergeJavaFileTask.addAll(underTestDirJavaFiles).addAll(resourceOrConfigFiles).compute();

        for (Map.Entry<String, ChangedFile> entry : mergeJavaFileTask.getMap().entrySet()) {
            String code = GitUtils.getFileContentAtCommit(bicDirectory, entry.getValue().getNewCommitId(), entry.getKey());
            path2ContentMap.put(entry.getKey(), code);
        }
        for (TestSuiteFile testSuiteFile : ceTestSuitedFiles) {
            List<TestSuiteFile> curTestFiles = new ArrayList<>();
            curTestFiles.add(testSuiteFile);
            pRFC.setTestSuiteFiles(curTestFiles);
            if (testSuiteFile.getTestMethodMap().values().stream().findFirst().isPresent()) {
                String filePath = testSuiteFile.getTestMethodMap().values().stream().findFirst().get().getRelativeFilePath();
                String realPath = bicDirectory+File.separator+filePath;
                // TODO Song Xuezhi 这里的代码有问题，应该是将测试方法迁移到源代码中，而不是将源代码迁移到测试代码中。
//                for (Map.Entry<String, RelatedTestCase> entry : testSourceFile.getTestMethodMap().entrySet()) {
//                    // 迁移方法体
//                    code = CompilationUtil.addOrReplaceMethod(code, entry.getValue().getMethod());
//                }
//                path2ContentMap.put(filePath, code);
                logger.info("migrate test methods from {} to {}", testSuiteFile.getNewCommitId(), filePath);
                MigratorUtil.mergeFiles(path2ContentMap, bicDirectory);
                // 编译
                compileResult = compile(bic, ctContext, compileResult);
                // 编译成功后执行测试
                if (compileResult.getState() == CompileResult.CompileState.SUCCESS) {
                    finalResult.getCaseResultMap().putAll(test(pRFC.getTestSuiteFiles(), ctContext, compileResult.getCompileWay()).getCaseResultMap());
                } else {
                    // 恢复原始情况
                    bicDirectory = MigratorUtil.checkoutCiForBFC(pRFC.getCommit().getName(), bic);
                    path2ContentMap.remove(filePath);
                }
            }
        }
    }

    private CompileResult compile(String bic, CtContext ctContext, CompileResult compileResult) {
        CompileTestEnv env = CommitBuildResult.originalCompileResult.containsKey(bic) ?
                CommitBuildResult.originalCompileResult.get(bic).getCompileWay() : compileResult.getCompileWay();
        return env == null ? ctContext.compile(OriginCompileFixWay.values()) : ctContext.compile(env);
    }

    //TODO Song Xuezhi 现在这样的重构必然会造成损失，有些项目（maven低版本）没有办法只测试一个具体的方法，可能只能测试一个类。
    // 但这个问题，我觉的大概可能在未来项目构建模块解决。
    // 我认为这件事情的优先级和compile同等。
    public TestResult test(List<TestSuiteFile> testSuiteFiles, CtContext ctContext, CompileTestEnv compileTestEnv) {
        TestResult testResult = ctContext.test(ChangedFileUtil.convertTestFilesToTestCaseXList(testSuiteFiles), compileTestEnv);
        return testResult;
    }

}

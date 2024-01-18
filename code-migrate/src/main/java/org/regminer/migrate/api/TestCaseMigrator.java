package org.regminer.migrate.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.regminer.common.code.analysis.CompilationUtil;
import org.regminer.common.model.*;
import org.regminer.common.utils.*;
import org.regminer.ct.api.AutoCompileAndTest;
import org.regminer.ct.api.CtContext;
import org.regminer.ct.api.OriginCompileFixWay;
import org.regminer.ct.model.CommitBuildResult;
import org.regminer.ct.model.CompileResult;
import org.regminer.ct.model.CompileTestEnv;
import org.regminer.ct.model.TestResult;

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
        pRFC.fileMap.put(bic, bicDirectory);
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
        List<TestFile> testFiles = new ArrayList<>(pRFC.getTestCaseFiles());
        TestResult finalResult = new TestResult();
        List<TestFile> ceTestFiles = new ArrayList<>();
        // 多个测试文件同时迁移容易导致编译失败，逐个尝试
        for (TestFile testFile : testFiles) {
            pRFC.setTestCaseFiles(List.of(testFile));
            MigratorUtil.mergeTwoVersion_BaseLine(pRFC, bicDirectory);
            // 编译
            CompileTestEnv env = CommitBuildResult.originalCompileResult.containsKey(bic) ?
                    CommitBuildResult.originalCompileResult.get(bic).getCompileWay() :compileResult.getCompileWay();
            compileResult = env == null ? ctContext.compile(OriginCompileFixWay.values()) : ctContext.compile(env);
            // 编译成功后执行测试
            if (compileResult.getState() == CompileResult.CompileState.SUCCESS) {
                finalResult.getCaseResultMap().putAll(test(pRFC.getTestCaseFiles(), ctContext, compileResult.getCompileWay()).getCaseResultMap());
            } else {
                // 恢复原始情况
                bicDirectory = MigratorUtil.checkoutCiForBFC(pRFC.getCommit().getName(), bic);
                ceTestFiles.add(testFile);
            }
        }
        migrateTestMethodAndTest(pRFC, bic, ceTestFiles, ctContext, compileResult, finalResult);
        if (!finalResult.isEmpty()) {
            return finalResult;
        }
        // 如果都编译失败，应该恢复测试文件。
        pRFC.setTestCaseFiles(testFiles);
        logger.debug("compile error after migrate: {}", compileResult.getState());
        return null; //或许返回NULL可能会引发空指针，但在当前阶段是合理的，如果编译失败，就没有测试结果。
    }

    private void migrateTestMethodAndTest(PotentialBFC pRFC, String bic, List<TestFile> ceTestFiles,
                                          CtContext ctContext, CompileResult compileResult, TestResult finalResult) throws IOException {
        File bicDirectory = MigratorUtil.checkoutCiForBFC(pRFC.getCommit().getName(), bic);
        Map<String, String> path2ContentMap = new HashMap<>();
        List<TestFile> underTestDirJavaFiles = pRFC.getTestRelates();
        List<SourceFile> sourceFiles = pRFC.getSourceFiles();
        MergeTask mergeJavaFileTask = new MergeTask();
        mergeJavaFileTask.addAll(underTestDirJavaFiles).addAll(sourceFiles).compute();//XXX
        for (Map.Entry<String, ChangedFile> entry : mergeJavaFileTask.getMap().entrySet()) {
            String code = GitUtils.getFileContentAtCommit(bicDirectory, entry.getValue().getNewCommitId(), entry.getKey());
            path2ContentMap.put(entry.getKey(), code);
        }
        for (TestFile testFile : ceTestFiles) {
            for (Map.Entry<String, RelatedTestCase> entry : testFile.getTestMethodMap().entrySet()) {
                // 迁移方法体
                String code = FileUtilx.readContentFromFile(new File(bicDirectory, entry.getValue().getRelativeFilePath()));
                String newCode = CompilationUtil.addOrReplaceMethod(code, entry.getValue().getMethod());
                path2ContentMap.put(entry.getValue().getRelativeFilePath(), newCode);
                logger.info("migrate test methods from {} to {}", testFile.getNewCommitId(), entry.getValue().getRelativeFilePath());
                MigratorUtil.mergeFiles(path2ContentMap, bicDirectory);
                // 编译
                CompileTestEnv env = CommitBuildResult.originalCompileResult.containsKey(bic) ?
                        CommitBuildResult.originalCompileResult.get(bic).getCompileWay() :compileResult.getCompileWay();
                compileResult = env == null ? ctContext.compile(OriginCompileFixWay.values()) : ctContext.compile(env);
                // 编译成功后执行测试
                if (compileResult.getState() == CompileResult.CompileState.SUCCESS) {
                    finalResult.getCaseResultMap().putAll(test(pRFC.getTestCaseFiles(), ctContext, compileResult.getCompileWay()).getCaseResultMap());
                } else {
                    // 恢复原始情况
                    bicDirectory = MigratorUtil.checkoutCiForBFC(pRFC.getCommit().getName(), bic);
                    path2ContentMap.remove(entry.getValue().getRelativeFilePath());
                }
            }
        }
    }

    //TODO Song Xuezhi 现在这样的重构必然会造成损失，有些项目（maven低版本）没有办法只测试一个具体的方法，可能只能测试一个类。
    // 但这个问题，我觉的大概可能在未来项目构建模块解决。
    // 我认为这件事情的优先级和compile同等。
    public TestResult test(List<TestFile> testFiles, CtContext ctContext, CompileTestEnv compileTestEnv) {
        TestResult testResult = ctContext.test(ChangedFileUtil.convertTestFilesToTestCaseXList(testFiles), compileTestEnv);
        return testResult;
    }

}

package org.regminer.migrate.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.regminer.common.model.PotentialBFC;
import org.regminer.common.model.RelatedTestCase;
import org.regminer.common.model.TestFile;
import org.regminer.ct.api.AutoCompileAndTest;
import org.regminer.ct.api.OriginCompileFixWay;
import org.regminer.ct.api.CtContext;
import org.regminer.ct.model.CommitBuildResult;
import org.regminer.ct.model.CompileResult;
import org.regminer.ct.model.CompileTestEnv;
import org.regminer.ct.model.TestResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sxz
 */
public class TestCaseMigrator extends Migrator {
    protected Logger logger = LogManager.getLogger(Migrator.class);

    public TestResult migrate(PotentialBFC pRFC, String bic) throws Exception {
        logger.info("start to migrate in {}", pRFC.getCommit().getName());
        File bicDirectory = checkoutCiForBFC(pRFC.getCommit().getName(), bic);
        pRFC.fileMap.put(bic, bicDirectory);
        CtContext ctContext = new CtContext(new AutoCompileAndTest());
        ctContext.setProjectDir(bicDirectory);

        CompileResult compileResult =
                CommitBuildResult.originalCompileResult.containsKey(bic)?
                        CommitBuildResult.originalCompileResult.get(bic) :
                        ctContext.compile(OriginCompileFixWay.values());
        CommitBuildResult.originalCompileResult.putIfAbsent(bic,compileResult);

        if (compileResult.getState() == CompileResult.CompileState.CE) {
            logger.debug("compile error before migrate");
            return null;
        }
        mergeTwoVersion_BaseLine(pRFC, bicDirectory);
        // 编译
        compileResult = ctContext.compile(compileResult.getCompileWay());
        //编译成功后执行测试
        if (compileResult.getState() == CompileResult.CompileState.SUCCESS) {
            return test(pRFC.getTestCaseFiles(), ctContext, compileResult.getCompileWay());
        } else {
            logger.debug("compile error after migrate");
            return null; //或许返回NULL可能会引发空指针，但在当前阶段是合理的，如果编译失败，就没有测试结果。
        }
    }


    public List<RelatedTestCase> convertTestFilesToTestCaseXList(List<TestFile> testFiles) {
        List<RelatedTestCase> allTestCaseXs = new ArrayList<>();

        for (TestFile testFile : testFiles) {
            List<RelatedTestCase> testCaseXs = testFile.getTestCaseList();
            allTestCaseXs.addAll(testCaseXs);
        }

        return allTestCaseXs;
    }

    //TODO Song Xuezhi 现在这样的重构必然会造成损失，有些项目（maven低版本）没有办法只测试一个具体的方法，可能只能测试一个类。
    // 但这个问题，我觉的大概可能在未来项目构建模块解决。
    // 我认为这件事情的优先级和compile同等。
    public TestResult test(List<TestFile> testFiles, CtContext ctContext, CompileTestEnv compileTestEnv) {
        TestResult testResult = ctContext.test(convertTestFilesToTestCaseXList(testFiles), compileTestEnv);
        return testResult;
    }

}

package org.regminer.miner.migrate;

import org.jetbrains.annotations.NotNull;
import org.regminer.common.model.PotentialBFC;
import org.regminer.common.model.TestCaseX;
import org.regminer.common.model.TestFile;
import org.regminer.ct.api.AutoCompileAndTest;
import org.regminer.ct.api.CtContext;
import org.regminer.ct.model.CompileResult;
import org.regminer.ct.model.CtCommands;
import org.regminer.ct.model.TestResult;
import org.regminer.migrate.api.Migrator;
import org.slf4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sxz
 */
public class TestCaseMigrator extends Migrator {
    public final static int PASS = 0;
    public final static int FAL = 1;
    public final static int CE = -1;
    public final static int UNRESOLVE = -2;

    protected Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    public TestResult migrate(@NotNull PotentialBFC pRFC, String bic) throws Exception {
        File bicDirectory = checkoutCiForBFC(pRFC.getCommit().getName(), bic);
        pRFC.fileMap.put(bic, bicDirectory);
        mergeTwoVersion_BaseLine(pRFC,bicDirectory);
        // 编译
        CtContext ctContext  = new CtContext(new AutoCompileAndTest());
        ctContext.setProjectDir(bicDirectory);
        CompileResult compileResult = ctContext.compile();
        //编译成功后执行测试
        if (compileResult.getState() == CompileResult.CompileState.SUCCESS) {
            return test(pRFC.getTestCaseFiles(), ctContext, compileResult.getEnvCommands());
        } else {
           return null; //或许返回NULL可能会引发空指针，但在当前阶段是合理的，如果编译失败，就没有测试结果。
        }
    }


    public List<TestCaseX> convertTestFilesToTestCaseXList(List<TestFile> testFiles) {
        List<TestCaseX> allTestCaseXs = new ArrayList<>();

        for (TestFile testFile : testFiles) {
            List<TestCaseX> testCaseXs = testFile.toTestCaseXList();
            allTestCaseXs.addAll(testCaseXs);
        }

        return allTestCaseXs;
    }

    //TODO Song Xuezhi 现在这样的重构必然会造成损失，有些项目（maven低版本）没有办法只测试一个具体的方法，可能只能测试一个类。
    // 但这个问题，我觉的大概可能在未来项目构建模块解决。
    // 我认为这件事情的优先级和compile同等。
    public  TestResult test(List<TestFile> testFiles, CtContext ctContext, CtCommands ctCommands){
        TestResult testResult  = ctContext.test(convertTestFilesToTestCaseXList(testFiles),ctCommands);
        return  testResult;
    }
}

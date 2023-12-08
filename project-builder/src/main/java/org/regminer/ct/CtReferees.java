package org.regminer.ct;

import org.jetbrains.annotations.NotNull;
import org.regminer.common.exec.ExecResult;
import org.regminer.ct.model.CompileResult;
import org.regminer.ct.model.TestCaseResult;

import java.util.logging.LogManager;
import java.util.logging.Logger;

public class CtReferees {
    static Logger logger = LogManager.getLogManager().getLogger("org.regminer.ct.CtReferees");

    public static CompileResult.CompileState JudgeCompileState(String message) {
        return message.toLowerCase().contains("build success") ? CompileResult.CompileState.SUCCESS :
                CompileResult.CompileState.CE;
    }

    public static TestCaseResult judgeTestCaseResult(ExecResult execResult) {

        TestCaseResult testCaseResult = new TestCaseResult();
        if (execResult.isTimeOut()){
            testCaseResult.setState(TestCaseResult.TestState.TE);
            return testCaseResult;
        }

        testCaseResult.setUsageTime(execResult.getUsageTime());
        String message = execResult.getMessage();

        // 检查message是否为null
        if (message != null) {
            message = message.toLowerCase();
            TestCaseResult.TestState testState = getTestState(message);
            testCaseResult.setState(testState);
        } else {
            testCaseResult.setState(TestCaseResult.TestState.UNKNOWN);
        }
//        testCaseResult.setExceptionMessage(spiltExceptionMessage(message, testState));
        return testCaseResult;
    }

    @NotNull
    private static TestCaseResult.TestState getTestState(String message) {
        TestCaseResult.TestState testState;
        if (message.contains("build success")) {
            testState = TestCaseResult.TestState.PASS;
        } else if (message.contains("compilation error") || message.contains("compilation failure")) {
            testState = TestCaseResult.TestState.CE;
        } else if (message.contains("no test")) {
            testState = TestCaseResult.TestState.NOTEST;
        } else {
            testState = TestCaseResult.TestState.FAL;
        }
        return testState;
    }

    public static String spiltExceptionMessage(String message, TestCaseResult.TestState testState) {
        if (testState != TestCaseResult.TestState.FAL) {
            return null;
        }
        String testResult = null;
        try {
            int splitStartNum = message.indexOf("t e s t s\n[info] ") + ("t e s t s\n[info] ").length();
            int splitEndNum = message.indexOf("[info] results:");
            testResult = message.substring(splitStartNum, splitEndNum).replace("-", "")
                    .replace("[info] ", "").replace("[error] ", "")
                    .replace("[info]", "").replace("[error]", "");
            System.out.println(testResult);
        } catch (Exception e) {
            logger.info(e.getLocalizedMessage());
        }

        return testResult;
    }

}

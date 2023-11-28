package org.regminer.ct;

import org.regminer.ct.model.CompileResult;
import org.regminer.ct.model.ExecResult;
import org.regminer.ct.model.TestCaseResult;

public class CtReferees {

    public static CompileResult.CompileState JudgeCompileState(String message) {
        return message.toLowerCase().contains("build success") ? CompileResult.CompileState.SUCCESS :
                CompileResult.CompileState.CE;
    }

    public static TestCaseResult judgeTestCaseResult(ExecResult execResult) {

        TestCaseResult testCaseResult = new TestCaseResult();
        testCaseResult.setUsageTime(execResult.getUsageTime());
        String message = execResult.getMessage();

        message = message.toLowerCase();
        TestCaseResult.TestState testState;
        if (execResult.isTimeOut()) {
            testState = TestCaseResult.TestState.TE;
        } else if (message.contains("build success")) {
            testState = TestCaseResult.TestState.PASS;
        } else if (message.contains("compilation error") || message.contains("compilation failure")) {
            testState = TestCaseResult.TestState.CE;
        } else if (message.contains("no test")) {
            testState = TestCaseResult.TestState.NOTEST;
        } else {
            testState = TestCaseResult.TestState.FAL;
        }
        testCaseResult.setState(testState);
        testCaseResult.setExceptionMessage(spiltExceptionMessage(message,testState));
        return testCaseResult;
    }

    public static String spiltExceptionMessage(String message, TestCaseResult.TestState testState) {
        if(testState != TestCaseResult.TestState.FAL){
            return null;
        }
        int splitStartNum = message.indexOf("t e s t s\n[info] ") + ("t e s t s\n[info] ").length();
        int splitEndNum = message.indexOf("[info] results:");
        String testResult = message.substring(splitStartNum, splitEndNum).replace("-","")
                .replace("[info] ","").replace("[error] ","")
                .replace("[info]","").replace("[error]","");
        System.out.println(testResult);
        return testResult;
    }

}

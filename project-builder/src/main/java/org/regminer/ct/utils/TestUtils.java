package org.regminer.ct.utils;

import org.regminer.common.model.TestCaseX;
import org.regminer.ct.model.TestCaseResult;
import org.regminer.ct.model.TestResult;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: sxz
 * @Date: 2023/11/29/22:54
 * @Description:
 */
public class TestUtils {
    public  static Map<String, TestCaseResult> collectTestCasesByState(TestResult testResult,
                                                                       TestCaseResult.TestState testState) {
        return testResult.getCaseResultMap().entrySet().stream()
                .filter(entry -> entry.getValue().getState() == testState)
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
    }

    public  static Map<String, TestCaseResult> collectTestCasesBreakState(TestResult testResult,
                                                                       TestCaseResult.TestState testState) {
        return testResult.getCaseResultMap().entrySet().stream()
                .filter(entry -> entry.getValue().getState() != testState)
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
    }
}

package org.regminer.ct.model;

import java.util.concurrent.ConcurrentHashMap;

public class TestResult {
    private ConcurrentHashMap<String, TestCaseResult> caseResultMap = new ConcurrentHashMap<>();

    public void takeTestCaseResult(String key, TestCaseResult testCaseResult) {
        caseResultMap.put(key, testCaseResult);
    }

    public boolean exists(String key) {
        return caseResultMap.containsKey(key);
    }

    public ConcurrentHashMap<String, TestCaseResult> getCaseResultMap() {
        return caseResultMap;
    }

}

package org.regminer.miner.ct.model;

import java.util.concurrent.ConcurrentHashMap;

public class TestResult {
    private ConcurrentHashMap<String, TestCaseResult> caseResultMap = new ConcurrentHashMap<>();

    public void takeTestCaseResult(String key, TestCaseResult testCaseResult) {
        caseResultMap.put(key, testCaseResult);
    }

    public ConcurrentHashMap<String, TestCaseResult> getCaseResultMap() {
        return caseResultMap;
    }
}

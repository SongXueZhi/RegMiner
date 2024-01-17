package org.regminer.ct.model;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TestResult {
    private final ConcurrentMap<String, TestCaseResult> caseResultMap = new ConcurrentHashMap<>();

    public void takeTestCaseResult(String key, TestCaseResult testCaseResult) {
        caseResultMap.put(key, testCaseResult);
    }

    public boolean exists(String key) {
        return caseResultMap.containsKey(key);
    }
    public boolean isEmpty() {
        return caseResultMap.isEmpty();
    }

    public ConcurrentMap<String, TestCaseResult> getCaseResultMap() {
        return caseResultMap;
    }

}

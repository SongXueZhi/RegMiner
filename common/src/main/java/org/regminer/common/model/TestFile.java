package org.regminer.common.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestFile extends ChangedFile {
    public Type type;
    private Map<String, RelatedTestCase> testMethodMap = new HashMap<>();
    public TestFile(String newPath) {
        super(newPath);
    }


    public Map<String, RelatedTestCase> getTestMethodMap() {
        return testMethodMap;
    }

    public void setTestMethodMap(Map<String, RelatedTestCase> testMethodMap) {
        this.testMethodMap = testMethodMap;
    }

    public List<RelatedTestCase> getTestCaseList() {
        return new ArrayList<>(testMethodMap.values());
    }

}

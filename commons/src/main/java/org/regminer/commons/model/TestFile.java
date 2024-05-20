package org.regminer.commons.model;

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

    public String joinTestcaseString() {
        if (testMethodMap == null || testMethodMap.isEmpty()) {
            return "";
        }
        ArrayList<RelatedTestCase> testCaseArrayList = new ArrayList<>(testMethodMap.values());
        StringBuilder sb = new StringBuilder(testCaseArrayList.get(0).getName());
        for (int i = 1; i < testCaseArrayList.size(); i++) {
            sb.append(";").append(testCaseArrayList.get(i).getName());
        }
        return sb.toString();
    }

}

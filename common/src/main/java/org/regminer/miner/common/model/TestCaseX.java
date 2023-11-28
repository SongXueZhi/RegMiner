package org.regminer.miner.common.model;

import java.util.Objects;

public class TestCaseX {
    private String filePath;
    private String packageName;
    private String className;
    private String methodName;

    @Override
    public String toString() {
        return "TestCaseX{" +
                "filePath='" + filePath + '\'' +
                ", packageName='" + packageName + '\'' +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestCaseX)) return false;
        TestCaseX testCaseX = (TestCaseX) o;
        return Objects.equals(getFilePath(), testCaseX.getFilePath())
                && Objects.equals(getPackageName(), testCaseX.getPackageName())
                && Objects.equals(getClassName(), testCaseX.getClassName())
                && Objects.equals(getMethodName(), testCaseX.getMethodName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFilePath(), getPackageName(), getClassName(), getMethodName());
    }

    public String getName() {
        return String.join(".", packageName, className, methodName);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}

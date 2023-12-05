package org.regminer.common.model;

import java.util.Objects;

public class RelatedTestCase {
    private String filePath;
    private String enclosingClassName;
    private String methodName;
    private Methodx method;
    public Methodx getMethod() {
        return method;
    }

    public void setMethod(Methodx method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "TestCaseX{" +
                "filePath='" + filePath + '\'' +
                ", className='" + enclosingClassName + '\'' +
                ", methodName='" + methodName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RelatedTestCase)) return false;
        RelatedTestCase testCaseX = (RelatedTestCase) o;
        return Objects.equals(getFilePath(), testCaseX.getFilePath())
                && Objects.equals(getEnclosingClassName(), testCaseX.getEnclosingClassName())
                && Objects.equals(getMethodName(), testCaseX.getMethodName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFilePath(), getEnclosingClassName(), getMethodName());
    }

    public String getName() {
        return String.join(".", enclosingClassName, methodName);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    public String getEnclosingClassName() {
        return enclosingClassName;
    }

    public void setEnclosingClassName(String enclosingClassName) {
        this.enclosingClassName = enclosingClassName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}

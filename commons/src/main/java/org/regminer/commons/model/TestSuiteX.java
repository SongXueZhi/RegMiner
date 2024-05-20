package org.regminer.commons.model;


import org.apache.commons.collections4.list.SetUniqueList;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author: sxz
 * @Date: 2022/06/08/21:42
 * @Description:
 */
public class TestSuiteX {
    public final List<RelatedTestCase> testCaseXList = SetUniqueList.setUniqueList(new LinkedList<>());
    private String filePath;
    private String packageName;
    private String className;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("testCaseXList", testCaseXList)
                .append("filePath", filePath)
                .append("packageName", packageName)
                .append("className", className)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof TestSuiteX)) return false;

        TestSuiteX that = (TestSuiteX) o;

        return new EqualsBuilder().append(testCaseXList, that.testCaseXList).append(getFilePath(),
                that.getFilePath()).append(getPackageName(), that.getPackageName()).append(getClassName(),
                that.getClassName()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(testCaseXList).append(getFilePath()).append(getPackageName()).append(getClassName()).toHashCode();
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
}

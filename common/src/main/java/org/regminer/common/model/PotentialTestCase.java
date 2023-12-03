package org.regminer.common.model;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PotentialTestCase {


    public Map<String, File> fileMap = new HashMap();
    List<TestFile> testFiles;
    List<SourceFile> sourceFiles;
    int level;

    public PotentialTestCase(int index) {
        this.level = index;
    }

    public int getIndex() {
        return level;
    }

    public void setIndex(int index) {
        this.level = index;
    }

    public List<TestFile> getTestFiles() {
        return testFiles;
    }

    public void setTestFiles(List<TestFile> testFiles) {
        this.testFiles = testFiles;
    }

    public List<SourceFile> getSourceFiles() {
        return sourceFiles;
    }

    public void setSourceFiles(List<SourceFile> sourceFiles) {
        this.sourceFiles = sourceFiles;
    }
}

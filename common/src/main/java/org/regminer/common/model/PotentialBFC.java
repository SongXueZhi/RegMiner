package org.regminer.common.model;

import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PotentialBFC {
    public final static int TESTCASE_FROM_SELF = 0;
    public final static int TESTCASE_FROM_SEARCH = 1;
    private final RevCommit commit;
    private final List<TestFile> testRelates = new ArrayList<TestFile>(); //under test dir but not testcase
    public Map<String, File> fileMap = new HashMap<>();
    private String buggyCommitId;
    private int priority;
    private int testcaseFrom;
    private Double score;
    private List<NormalFile> normalJavaFiles; //which contains fix path
    private List<TestFile> testCaseFiles;    // All File Under test dir
    private List<SourceFile> sourceFiles = new ArrayList<>(); //config file or data for test

    private List<PotentialTestCase> potentialTestCaseList;


    public PotentialBFC(RevCommit commit) {
        this.commit = commit;
    }

    public List<PotentialTestCase> getPotentialTestCaseList() {
        return potentialTestCaseList;
    }

    public void setPotentialTestCaseList(List<PotentialTestCase> potentialTestCaseList) {
        this.potentialTestCaseList = potentialTestCaseList;
    }

    public RevCommit getCommit() {
        return commit;
    }

    public List<NormalFile> getNormalJavaFiles() {
        return normalJavaFiles;
    }

    public void setNormalJavaFiles(List<NormalFile> normalJavaFiles) {
        this.normalJavaFiles = normalJavaFiles;
    }

    public List<TestFile> getTestCaseFiles() {
        return testCaseFiles;
    }

    public void setTestCaseFiles(List<TestFile> testCaseFiles) {
        this.testCaseFiles = testCaseFiles;
    }

    public List<TestFile> getTestRelates() {
        return testRelates;
    }


    public List<SourceFile> getSourceFiles() {
        return sourceFiles;
    }

    public void setSourceFiles(List<SourceFile> sourceFiles) {
        this.sourceFiles = sourceFiles;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getBuggyCommitId() {
        return buggyCommitId;
    }

    public void setBuggyCommitId(String buggyCommitId) {
        this.buggyCommitId = buggyCommitId;
    }

    public int getTestcaseFrom() {
        return testcaseFrom;
    }

    public void setTestcaseFrom(int testcaseFrom) {
        this.testcaseFrom = testcaseFrom;
    }

    public String joinTestcaseString() {
        if (testCaseFiles == null || testCaseFiles.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(testCaseFiles.get(0).joinTestcaseString());
        for (int i = 1; i < testCaseFiles.size(); i++) {
            sb.append(";").append(testCaseFiles.get(i).joinTestcaseString());
        }
        return sb.toString();
    }
}

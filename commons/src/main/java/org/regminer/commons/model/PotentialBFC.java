package org.regminer.commons.model;

import lombok.Getter;
import lombok.Setter;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Setter
@Getter
public class PotentialBFC implements Serializable {
    public final static int TESTCASE_FROM_SELF = 0;
    public final static int TESTCASE_FROM_SEARCH = 1;
    private final String commitName;
    public Map<String, String> fileMap = new HashMap<>();
    private RevCommit commit;
    private String buggyCommitId;
    private int testcaseFrom;
    private Double score;
    private List<SourceCodeFile> sourceCodeFiles; //which contains fix path
    private List<TestSourceFile> testSourceFiles;// All File Under test dir
    private List<TestSuiteFile> testSuiteFiles;
    private List<ResourceOrConfigFile> resourceOrConfigFiles = new ArrayList<>(); //config file or data for test

    public PotentialBFC(RevCommit commit) {
        this.commit = commit;
        this.commitName = commit.getName();
    }

    public PotentialBFC(String commitName) {
        this.commitName = commitName;
    }

    public String joinTestcaseString() {
        if (testSuiteFiles == null || testSuiteFiles.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(testSuiteFiles.get(0).joinTestcaseString());
        for (int i = 1; i < testSuiteFiles.size(); i++) {
            sb.append(";").append(testSuiteFiles.get(i).joinTestcaseString());
        }
        return sb.toString();
    }


    public List<TestSourceFile> getTestDependFile() {
        return testSourceFiles.stream().filter(
                testSourceFile -> testSourceFile.getType() == ChangedFile.Type.TEST_DEPEND).collect(Collectors.toList());
    }
}

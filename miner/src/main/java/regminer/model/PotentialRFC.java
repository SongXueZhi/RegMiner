package regminer.model;

import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.util.*;

public class PotentialRFC {
    public Map<String, File> fileMap = new HashMap<>();
    private RevCommit commit;
    private int priority;
    private List<NormalFile> normalJavaFiles;
    private List<TestFile> testCaseFiles;
    private List<TestFile> testSuites = new ArrayList<TestFile>();
    private List<TestFile> testRelates = new ArrayList<TestFile>();
    private List<SourceFile> sourceFiles = new ArrayList<>();
    private Set<String> testCaseSet;

//	private List<PotentialTestCase> potentialTestcases;

    public PotentialRFC(RevCommit commit) {
        this.commit = commit;
    }

    public RevCommit getCommit() {
        return commit;
    }

    public void setCommit(RevCommit commit) {
        this.commit = commit;
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

//	public List<regminer.model.PotentialTestCase> getPotentialTestcases() {
//		return potentialTestcases;
//	}
//
//	public void setPotentialTestcases(List<regminer.model.PotentialTestCase> potentialTestcases) {
//		this.potentialTestcases = potentialTestcases;
//	}

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Set<String> getTestCaseSet() {
        return testCaseSet;
    }

    public void setTestCaseSet(Set<String> testCaseSet) {
        this.testCaseSet = testCaseSet;
    }

    public List<TestFile> getTestSuites() {
        return testSuites;
    }

    public void setTestSuites(List<TestFile> testSuites) {
        this.testSuites = testSuites;
    }

    public List<TestFile> getTestRelates() {
        return testRelates;
    }

    public void setTestRelates(List<TestFile> testRelates) {
        this.testRelates = testRelates;
    }

    public List<SourceFile> getSourceFiles() {
        return sourceFiles;
    }

    public void setSourceFiles(List<SourceFile> sourceFiles) {
        this.sourceFiles = sourceFiles;
    }
}

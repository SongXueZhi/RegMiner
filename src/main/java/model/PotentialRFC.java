package model;

import java.util.List;
import java.util.Set;

import org.eclipse.jgit.revwalk.RevCommit;

public class PotentialRFC {
	private RevCommit commit;
	private int priority;
	private List<NormalFile> normalJavaFiles;
	private List<TestFile> testCaseFiles;
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

//	public List<model.PotentialTestCase> getPotentialTestcases() {
//		return potentialTestcases;
//	}
//
//	public void setPotentialTestcases(List<model.PotentialTestCase> potentialTestcases) {
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


}

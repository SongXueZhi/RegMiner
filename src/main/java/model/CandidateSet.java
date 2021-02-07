package model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;

public class CandidateSet {
	List<RevCommit> candidateList = new ArrayList<RevCommit>();

	public List<RevCommit> getCandidateList() {
		return candidateList;
	}

	public void setCandidateList(List<RevCommit> candidateList) {
		this.candidateList = candidateList;
	}

}

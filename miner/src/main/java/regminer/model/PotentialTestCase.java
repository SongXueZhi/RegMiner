package regminer.model;

public class PotentialTestCase {
	String commitId;
	int level;
	
	public PotentialTestCase(String commitId,int index) {
		this.commitId=commitId;
		this.level=index;
	}
	

	public String getCommitId() {
		return commitId;
	}

	public void setCommitId(String commitId) {
		this.commitId = commitId;
	}

	public int getIndex() {
		return level;
	}

	public void setIndex(int index) {
		this.level = index;
	}
}

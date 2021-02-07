package model;

public class SZZBFCObject {
	String creationdate;
	String resolutiondate;
	String hash;
	String commitdate;

	public SZZBFCObject(String creationdate, String resolutiondate, String hash, String commitdate) {
		this.creationdate = creationdate;
		this.resolutiondate = resolutiondate;
		this.hash = hash;
		this.commitdate = commitdate;
	}
	public String getCreationdate() {
		return creationdate;
	}

	public void setCreationdate(String creationdate) {
		this.creationdate = creationdate;
	}

	public String getResolutiondate() {
		return resolutiondate;
	}

	public void setResolutiondate(String resolutiondate) {
		this.resolutiondate = resolutiondate;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getCommitdate() {
		return commitdate;
	}

	public void setCommitdate(String commitdate) {
		this.commitdate = commitdate;
	}
}

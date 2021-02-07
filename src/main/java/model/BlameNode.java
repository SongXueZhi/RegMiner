package model;

import org.eclipse.jgit.revwalk.RevCommit;

public class BlameNode {
	RevCommit commit;
	int line;
	int[] pair;

	public int[] getPair() {
		return pair;
	}

	public void setPair(int[] pair) {
		this.pair = pair;
	}

	public RevCommit getCommit() {
		return commit;
	}

	public void setCommit(RevCommit commit) {
		this.commit = commit;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BlameNode)) {
			return false;
		}
		BlameNode node = (BlameNode) obj;
		return node.commit.equals(this.commit) && node.line == this.line;
	}
}

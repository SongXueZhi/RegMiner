package model;

public class Method {

	private String signature;
	private int startLine;
	private int stopLine;
	private String simpleName;

	public Method(String signature) {
		this.signature = signature;
	}

	public Method(String signature, int startLine, int stopLine, String simpleName) {
		this.signature = signature;
		this.startLine = startLine;
		this.stopLine = stopLine;
		this.simpleName = simpleName;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public int getStartLine() {
		return startLine;
	}

	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}

	public int getStopLine() {
		return stopLine;
	}

	public void setStopLine(int stopLine) {
		this.stopLine = stopLine;
	}

	public String getSimpleName() {
		return simpleName;
	}

	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}

}

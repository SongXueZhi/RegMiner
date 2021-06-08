package regminer.model;

public class RelatedTestCase {
	Methodx method;
	Type type;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Methodx getMethod() {
		return method;
	}

	public void setMethod(Methodx method) {
		this.method = method;
	}

	public enum Type {
		Created, modified
	}
}

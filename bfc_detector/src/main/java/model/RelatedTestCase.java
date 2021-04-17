package model;

public class RelatedTestCase {
	Method method;
	Type type;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public enum Type {
		Created, modified
	}
}

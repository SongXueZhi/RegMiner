package regminer.callgraph.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.Method;

public class CallNode {
	private String className;
	private Type type;
	private Method method;
	private String methodName;
	private Field filed;
	private String filedName;
	public List<CallNode> childList = new ArrayList<>();

	public enum Type {
		filed, method
    }

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

	public Field getFiled() {
		return filed;
	}

	public void setFiled(Field filed) {
		this.filed = filed;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getFiledName() {
		return filedName;
	}

	public void setFiledName(String filedName) {
		this.filedName = filedName;
	}
}

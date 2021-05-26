package ast;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;

import model.CallNode;

public class MethodCaller {

	public CallNode getMethodCall(Method method, JavaClass clazz) {
		MethodGen mg = new MethodGen(method, clazz.getClassName(), new ConstantPoolGen(clazz.getConstantPool()));
		BcelMethodVisitor visitor = new BcelMethodVisitor(mg, clazz);
		return visitor.got();
	}

}

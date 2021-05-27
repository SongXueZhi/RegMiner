package callgraph;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;

import callgraph.model.CallNode;
/**
 * @author sxz
 */
public class MethodCaller {

	public CallNode getMethodCall(Method method, JavaClass clazz) {
		MethodGen mg = new MethodGen(method, clazz.getClassName(), new ConstantPoolGen(clazz.getConstantPool()));
		BcelMethodInvokeVisitor visitor = new BcelMethodInvokeVisitor(mg, clazz);
		return visitor.got();
	}

}

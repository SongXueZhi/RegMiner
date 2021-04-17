package ast;

import java.util.ArrayList;
import java.util.List;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;

public class ClassVisitor extends EmptyVisitor {
	private JavaClass clazz;
	private ConstantPoolGen constants;
	private String classReferenceFormat;
	private final DynamicCallManager DCManager = new DynamicCallManager();
	private List<String> methodCalls = new ArrayList<>();

	public ClassVisitor(JavaClass jc) {
		clazz = jc;
		constants = new ConstantPoolGen(clazz.getConstantPool());
		classReferenceFormat = "C:" + clazz.getClassName() + " %s";
	}

	@Override
	public void visitJavaClass(JavaClass jc) {
		jc.getConstantPool().accept(this);
		Method[] methods = jc.getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			DCManager.retrieveCalls(method, jc);
			DCManager.linkCalls(method);
			method.accept(this);

		}
	}

	@Override
	public void visitConstantPool(ConstantPool constantPool) {
		for (int i = 0; i < constantPool.getLength(); i++) {
			Constant constant = constantPool.getConstant(i);
			if (constant == null)
				continue;
			if (constant.getTag() == 7) {
				String referencedClass = constantPool.constantToString(constant);
				System.out.println(String.format(classReferenceFormat, referencedClass));
			}
		}
	}

	@Override
	public void visitMethod(Method method) {
		MethodGen mg = new MethodGen(method, clazz.getClassName(), constants);
		MethodVisitor visitor = new MethodVisitor(mg, clazz);
		methodCalls.addAll(visitor.start());
	}

	public ClassVisitor start() {
		visitJavaClass(clazz);
		return this;
	}

	public List<String> methodCalls() {
		return this.methodCalls;
	}
}

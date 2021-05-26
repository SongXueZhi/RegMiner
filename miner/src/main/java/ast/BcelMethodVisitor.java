package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ConstantPushInstruction;
import org.apache.bcel.generic.EmptyVisitor;
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.INVOKEDYNAMIC;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionConst;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ReturnInstruction;

import model.CallNode;
import utils.CodeUtil;

/**
 * The simplest of method visitors, prints any invoked method signature for all
 * method invocations.
 * 
 * Class copied with modifications from CJKM: http://www.spinellis.gr/sw/ckjm/
 */
public class BcelMethodVisitor extends EmptyVisitor {

	JavaClass visitedClass;
	private final MethodGen mg;
	private final ConstantPoolGen cp;
	private String format;
	private final List<String> methodCalls = new ArrayList<>();
	public CallNode node = new CallNode();

	public BcelMethodVisitor(MethodGen m, JavaClass jc) {
		visitedClass = jc;
		mg = m;
		cp = mg.getConstantPool();
		node.setType(CallNode.Type.method);
		node.setMethodName(CodeUtil.getMethodSig(mg.getMethod().getName(), mg.getArgumentTypes()));
		node.setMethod(mg.getMethod());
	}

	public List<String> start() {
		if (mg.isAbstract() || mg.isNative())
			return Collections.emptyList();

		for (InstructionHandle ih = mg.getInstructionList().getStart(); ih != null; ih = ih.getNext()) {
			Instruction i = ih.getInstruction();

			if (!visitInstruction(i))
				i.accept(this);
		}
		return methodCalls;
	}

	public CallNode got() {
		if (mg.isAbstract() || mg.isNative())
			return null;

		for (InstructionHandle ih = mg.getInstructionList().getStart(); ih != null; ih = ih.getNext()) {
			Instruction i = ih.getInstruction();
			if (!visitInstruction(i))
				i.accept(this);
		}
		return node;
	}

	private boolean visitInstruction(Instruction i) {
		short opcode = i.getOpcode();
		return ((InstructionConst.getInstruction(opcode) != null) && !(i instanceof ConstantPushInstruction)
				&& !(i instanceof ReturnInstruction));
	}

	@Override
	public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i) {
		CallNode child = new CallNode();
		child.setClassName(i.getClassName(cp));
		child.setType(CallNode.Type.method);

		child.setMethodName(CodeUtil.getMethodSig(i.getMethodName(cp), i.getArgumentTypes(cp)));
		node.childList.add(child);
	}

	@Override
	public void visitINVOKEINTERFACE(INVOKEINTERFACE i) {
		CallNode child = new CallNode();
		child.setClassName(i.getClassName(cp));
		child.setType(CallNode.Type.method);
		child.setMethodName(CodeUtil.getMethodSig(i.getMethodName(cp), i.getArgumentTypes(cp)));
		node.childList.add(child);
	}

	@Override
	public void visitINVOKESPECIAL(INVOKESPECIAL i) {
		CallNode child = new CallNode();
		child.setClassName(i.getClassName(cp));
		child.setType(CallNode.Type.method);
		child.setMethodName(CodeUtil.getMethodSig(i.getMethodName(cp), i.getArgumentTypes(cp)));
		node.childList.add(child);
	}

	@Override
	public void visitINVOKESTATIC(INVOKESTATIC i) {
		CallNode child = new CallNode();
		child.setClassName(i.getClassName(cp));
		child.setType(CallNode.Type.method);
		child.setMethodName(CodeUtil.getMethodSig(i.getMethodName(cp), i.getArgumentTypes(cp)));
		node.childList.add(child);
	}

	@Override
	public void visitINVOKEDYNAMIC(INVOKEDYNAMIC i) {
		CallNode child = new CallNode();
		child.setClassName(i.getClassName(cp));
		child.setType(CallNode.Type.method);
		child.setMethodName(CodeUtil.getMethodSig(i.getMethodName(cp), i.getArgumentTypes(cp)));
		node.childList.add(child);
	}

//	@Override
//	public void visitGETFIELD(final GETFIELD i) {
//		CallNode child = new CallNode();
//		child.setType(CallNode.Type.filed);
//		child.setFiledName(i.getFieldName(cp));
//		node.childList.add(child);
//	}
//
//	@Override
//	public void visitGETSTATIC(final GETSTATIC i) {
//		CallNode child = new CallNode();
//		child.setType(CallNode.Type.filed);
//		child.setFiledName(i.getFieldName(cp));
//		node.childList.add(child);
//	}
}
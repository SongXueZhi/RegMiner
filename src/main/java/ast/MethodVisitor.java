package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ConstantPushInstruction;
import org.apache.bcel.generic.EmptyVisitor;
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
import org.apache.bcel.generic.Type;

/**
 * The simplest of method visitors, prints any invoked method signature for all
 * method invocations.
 * 
 * Class copied with modifications from CJKM: http://www.spinellis.gr/sw/ckjm/
 */
public class MethodVisitor extends EmptyVisitor {

	JavaClass visitedClass;
	private MethodGen mg;
	private ConstantPoolGen cp;
	private String format;
	private List<String> methodCalls = new ArrayList<>();

	public MethodVisitor(MethodGen m, JavaClass jc) {
		visitedClass = jc;
		mg = m;
		cp = mg.getConstantPool();
		format = "M:" + visitedClass.getClassName() + ":" + mg.getName() + "(" + argumentList(mg.getArgumentTypes())
				+ ")" + " " + "(%s)%s:%s(%s)";
	}

	private String argumentList(Type[] arguments) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arguments.length; i++) {
			if (i != 0) {
				sb.append(",");
			}
			sb.append(arguments[i].toString());
		}
		return sb.toString();
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

	private boolean visitInstruction(Instruction i) {
		short opcode = i.getOpcode();
		return ((InstructionConst.getInstruction(opcode) != null) && !(i instanceof ConstantPushInstruction)
				&& !(i instanceof ReturnInstruction));
	}

	@Override
	public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i) {
		methodCalls.add(String.format(format, "M", i.getReferenceType(cp), i.getMethodName(cp),
				argumentList(i.getArgumentTypes(cp))));
	}

	@Override
	public void visitINVOKEINTERFACE(INVOKEINTERFACE i) {
		methodCalls.add(String.format(format, "I", i.getReferenceType(cp), i.getMethodName(cp),
				argumentList(i.getArgumentTypes(cp))));
	}

	@Override
	public void visitINVOKESPECIAL(INVOKESPECIAL i) {
		methodCalls.add(String.format(format, "O", i.getReferenceType(cp), i.getMethodName(cp),
				argumentList(i.getArgumentTypes(cp))));
	}

	@Override
	public void visitINVOKESTATIC(INVOKESTATIC i) {
		methodCalls.add(String.format(format, "S", i.getReferenceType(cp), i.getMethodName(cp),
				argumentList(i.getArgumentTypes(cp))));
	}

	@Override
	public void visitINVOKEDYNAMIC(INVOKEDYNAMIC i) {
		methodCalls.add(
				String.format(format, "D", i.getType(cp), i.getMethodName(cp), argumentList(i.getArgumentTypes(cp))));
	}
}
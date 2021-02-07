package ast;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class MemberRetriever extends ASTVisitor{
	private ArrayList<ASTNode> nodeList = new ArrayList<>();

//	public boolean visit(TypeDeclaration type) {
//		boolean isInnerClass = false;
//		ASTNode parent = type.getParent();
//		while (!(parent instanceof CompilationUnit)) {
//			if (parent instanceof TypeDeclaration) {
//				isInnerClass = true;
//				break;
//			}
//			parent = parent.getParent();
//		}
//
//		if (isInnerClass) {
//			nodeList.add(type);
//			return false;
//		} else {
//			return true;
//		}
//	}

	public boolean visit(MethodDeclaration method) {
		this.nodeList.add(method);
		return false;
	}

	public ArrayList<ASTNode> getMemberList() {
		return this.nodeList;
	}
}

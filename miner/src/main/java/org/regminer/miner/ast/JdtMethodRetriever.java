package org.regminer.miner.ast;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import java.util.ArrayList;

public class JdtMethodRetriever extends ASTVisitor {
	private final ArrayList<MethodDeclaration> nodeList = new ArrayList<>();

	public boolean visit(MethodDeclaration method) {
		this.nodeList.add(method);
		return false;
	}

	public ArrayList<MethodDeclaration> getMemberList() {
		return this.nodeList;
	}

	@SuppressWarnings("unlikely-arg-type")
	public MethodDeclaration getMethodDeclabyName(String name) {
		for (MethodDeclaration md : nodeList) {
			if (md.getName().toString().equals(name)) {
				return md;
			}
		}
		return null;
	}
}

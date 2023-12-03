package org.regminer.common.ast;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdtFieldRetriever extends ASTVisitor {
	public Map<FieldDeclaration, List<VariableDeclarationFragment>> fieldMap = new HashMap<>();

	public boolean visit(FieldDeclaration field) {
		List<VariableDeclarationFragment> nodeList = new ArrayList<>();
		for (Object o : field.fragments()) {
			VariableDeclarationFragment vd = (VariableDeclarationFragment) o;
			nodeList.add(vd);
		}
		fieldMap.put(field, nodeList);
		return false;
	}
}

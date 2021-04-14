package ast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.MalformedTreeException;

public class ImportTrimmer {
	// 修剪AST的import
	public ASTRewrite prune(CompilationUnit unit) throws IOException, MalformedTreeException, BadLocationException {

		ASTRewrite rewriter = ASTRewrite.create(unit.getAST());
		List<ImportDeclaration> importNodeList = unit.imports();
		List<TypeDeclaration> types = unit.types();
		for (ImportDeclaration importDeclaration : importNodeList) {
			String str = importDeclaration.getName().toString();
			String pattern = str.substring(str.lastIndexOf(".") + 1, str.length());
			boolean flag = false;
			for (TypeDeclaration type : types) {
				if (type.toString().contains(pattern)) {
					flag = true;
				}
			}
			if (flag) {
				rewriter.remove(importDeclaration, null);
			}
		}
		unit.recordModifications();
		return rewriter;
	}

	public void getMethodCallGraph(ASTNode node, CompilationUnit unit) {
		MethodInvokeRetriever rt = new MethodInvokeRetriever();
		node.accept(rt);
		for (MethodInvocation node1 : rt.mList) {
		}
	}

	class MethodInvokeRetriever extends ASTVisitor {
		public List<MethodInvocation> mList = new ArrayList<>();

		@Override
		public boolean visit(MethodInvocation method) {
			this.mList.add(method);
			return true;
		}

	}
}

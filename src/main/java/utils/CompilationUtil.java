package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import ast.MemberRetriever;
import ast.TypeRetriever;
import model.Method;

public class CompilationUtil {
	public static CompilationUnit parseCompliationUnit(String fileContent) {
		ASTParser parser = ASTParser.newParser(AST.JLS13); // handles JDK 1.0, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6

		parser.setSource(fileContent.toCharArray());
		// In order to parse 1.6 code, some compiler options need to be set to 1.6
		Map<String, String> options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, options);
		parser.setCompilerOptions(options);

		CompilationUnit result = (CompilationUnit) parser.createAST(null);
		return result;
	}

	public static List<Method> getAllMethod(String codeContent) {
		List<Method> methods = new ArrayList<>();
		MemberRetriever retriever = new MemberRetriever();
		CompilationUnit unit = parseCompliationUnit(codeContent);
		unit.accept(retriever);
		List<ASTNode> methodNodes = retriever.getMemberList();
		for (ASTNode node : methodNodes) {
			MethodDeclaration methodDeclaration = (MethodDeclaration) node;
			String simpleName = methodDeclaration.getName().toString();
			StringBuilder sb = new StringBuilder(simpleName);
			List<ASTNode> parameters = methodDeclaration.parameters();
			// SingleVariableDeclaration
			for (ASTNode param : parameters) {
				sb.append("[").append(param.toString()).append("]");
			}
			String signature = sb.toString();
			int startLine = unit.getLineNumber(methodDeclaration.getStartPosition()) - 1;
			int endLine = unit.getLineNumber(methodDeclaration.getStartPosition() + node.getLength()) - 1;
			methods.add(new Method(signature, startLine, endLine, simpleName));
		}
		return methods;
	}

	public static String getQualityClassName(String codeContent) {
		String result;
		CompilationUnit unit = parseCompliationUnit(codeContent);
		TypeRetriever retriever = new TypeRetriever();
		unit.accept(retriever);
		result = retriever.getQualityName();
		return result;
	}
}

package regminer.ast;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class JdtClassRetriever extends ASTVisitor {
	private final List<TypeDeclaration> nodeList = new ArrayList<>();
	private String packageName;

	public boolean visit(TypeDeclaration type) {
		boolean isInnerClass = false;
		ASTNode parent = type.getParent();
		while (!(parent instanceof CompilationUnit)) {
			if (parent instanceof TypeDeclaration) {
				isInnerClass = true;
				break;
			}
			parent = parent.getParent();
		}

		if (isInnerClass) {
			return false;
		} else {
			nodeList.add(type);
			return true;
		}
	}

	public List<TypeDeclaration> getNodeList() {
		return nodeList;
	}

	@Override
	public boolean visit(PackageDeclaration n) {
		setPackageName(n.getName().toString());
		return false;
	}

	public String getQualityName() {
		return this.getPackageName() + "." + nodeList.get(0).getName().toString();
	}

	public String getPackageName() {
		return packageName;
	}

	private void setPackageName(String packageName) {
		this.packageName = packageName;
	}
}

package regminer.callgraph;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

public class MethodRetriever {

    public List<MethodDeclaration> getMethodList(CompilationUnit unit) {
        List<MethodDeclaration> methodDeclarationList = new ArrayList<>();
        new MethodGetterVisitor().visit(unit, methodDeclarationList);
        return methodDeclarationList;
    }


    private class MethodGetterVisitor extends VoidVisitorAdapter<Object> {

        @SuppressWarnings("unchecked")
        @Override
        public void visit(MethodDeclaration n, Object arg) {
            List<MethodDeclaration> methodDeclarationMap = (List<MethodDeclaration>) arg;
            methodDeclarationMap.add(n);
        }
    }
}

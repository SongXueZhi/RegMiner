package regminer.callgraph;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserMethodDeclaration;
import regminer.callgraph.model.CallRoot;
import regminer.callgraph.model.Callee;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CalleeResolver {
    private int deep;

    public CalleeResolver(int deep) {
        this.deep = deep;
    }

    public int getDeep() {
        return deep;
    }

    public void setDeep(int deep) {
        this.deep = deep;
    }

    public List<CallRoot> getAllMethodCallRoot(Map<String, MethodDeclaration> methodDeclarationMap, File dir) {
        List<CallRoot> callRoots = new ArrayList<>();
        for (Map.Entry<String, MethodDeclaration> methodDeclarationEntry : methodDeclarationMap.entrySet()) {
            callRoots.add(constructCallGraph(methodDeclarationEntry.getValue(), dir));
        }
        return callRoots;
    }

    public List<CallRoot> getAllMethodCallRoot(List<MethodDeclaration> methodDeclarationMap,File dir) {
        List<CallRoot> callRoots = new ArrayList<>();
        for (MethodDeclaration m : methodDeclarationMap) {
            callRoots.add(constructCallGraph(m, dir));
        }
        return callRoots;
    }

    public CallRoot constructCallGraph(MethodDeclaration methodDeclaration, File dir) {
        CallRoot root = new CallRoot(methodDeclaration);
        root.setDeep(deep);
        root.setChilds(getCallee(methodDeclaration, dir));
        if (deep > 1) {
            for (Callee callee : root.getChilds()) {
                callee.setChild(getCallee(callee.getMethodDeclaration(), dir));
            }
        }
        return root;
    }

    private List<Callee> getCallee(MethodDeclaration declaration, File dir) {
        List<Callee> calleeList = new ArrayList<>();
        List<MethodCallExpr> methodCallExprList = declaration.findAll(MethodCallExpr.class);
        for (MethodCallExpr methodCallExpr : methodCallExprList) {
            try {
                ResolvedMethodDeclaration resolvedMethodDeclaration = methodCallExpr.resolve();
                if (resolvedMethodDeclaration instanceof JavaParserMethodDeclaration) {
                    JavaParserMethodDeclaration jpmd = (JavaParserMethodDeclaration) resolvedMethodDeclaration;
                    MethodDeclaration mdec = jpmd.getWrappedNode();
                    if (!calleeList.contains(mdec)){
                    calleeList.add(new Callee(mdec));
                    }
                }
            } catch (Exception e) {
            }
        }
        return calleeList;
    }
}

package regminer.callgraph.model;

import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;


public class CallRoot {
    MethodDeclaration methodDeclaration;

    int deep;
    List<Callee> childs;

    public CallRoot(MethodDeclaration methodDeclaration) {
        this.methodDeclaration = methodDeclaration;
    }

    public MethodDeclaration getMethodDeclaration() {
        return methodDeclaration;
    }

    public void setMethodDeclaration(MethodDeclaration methodDeclaration) {
        this.methodDeclaration = methodDeclaration;
    }

    public List<Callee> getChilds() {
        return childs;
    }

    public void setChilds(List<Callee> childs) {
        this.childs = childs;
    }

    public int getDeep() {
        return deep;
    }

    public void setDeep(int deep) {
        this.deep = deep;
    }

    public List<MethodDeclaration> computeAllMethodDeclaration() {
        List<MethodDeclaration> result = new ArrayList<>();
        result.add(methodDeclaration);
        //deep 0
        if (childs == null) {
            return result;
        }
        //deep 1
        for (Callee callee : childs) {
            MethodDeclaration m =callee.getMethodDeclaration();
            if (!result.contains(m)) {
                result.add(m);
            }
            //deep 2
            if (callee.getChild() != null) {
                for (Callee callee1 : callee.getChild()) {
                    MethodDeclaration m1 = callee1.getMethodDeclaration();
                    if (!result.contains(m1)){
                        result.add(m1);
                    }

                }
            }
        }
        return result;
    }
}

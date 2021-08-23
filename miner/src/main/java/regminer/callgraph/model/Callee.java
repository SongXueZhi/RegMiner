package regminer.callgraph.model;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

import java.util.List;

public class Callee{
    MethodDeclaration methodDeclaration;
    List<Callee> child;

     public Callee(MethodDeclaration methodDeclaration){
         this.methodDeclaration = methodDeclaration;
     }

    public MethodDeclaration getMethodDeclaration() {
        return methodDeclaration;
    }

    public void setMethodDeclaration(MethodDeclaration methodDeclaration) {
        this.methodDeclaration = methodDeclaration;
    }

    public List<Callee> getChild() {
        return child;
    }

    public void setChild(List<Callee> child) {
        this.child = child;
    }
}
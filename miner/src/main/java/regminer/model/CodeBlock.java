package regminer.model;

import org.eclipse.jdt.core.dom.MethodDeclaration;

public class CodeBlock {


    private boolean ifMethod;
    private MethodDeclaration methodDeclaration;
    private int startLine;
    private int stopLine;
    private String newPath;

    public boolean isIfMethod() {
        return ifMethod;
    }

    public void setIfMethod(boolean ifMethod) {
        this.ifMethod = ifMethod;
    }

    public MethodDeclaration getMethodDeclaration() {
        return methodDeclaration;
    }

    public void setMethodDeclaration(MethodDeclaration methodDeclaration) {
        this.methodDeclaration = methodDeclaration;
    }

    public int getStartLine() {
        return startLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public int getStopLine() {
        return stopLine;
    }

    public void setStopLine(int stopLine) {
        this.stopLine = stopLine;
    }

    public String getNewPath() {
        return newPath;
    }

    public void setNewPath(String newPath) {
        this.newPath = newPath;
    }
}

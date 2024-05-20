package org.regminer.commons.model;

import org.eclipse.jdt.core.dom.MethodDeclaration;

/**
 * @author sxz
 * 该类进一步的封装了MethodDeclaration
 */
public class Methodx {

    private String enclosingClassName;
    private String signature;
    private int startLine;
    private int stopLine;
    private String simpleName;
    private MethodDeclaration methodDeclaration;

    public Methodx(String signature) {
        this.signature = signature;
    }

    public Methodx(String enclosingClassName, String signature, int startLine, int stopLine, String simpleName,
                   MethodDeclaration methodDeclaration) {
        this.enclosingClassName = enclosingClassName;
        this.signature = signature;
        this.startLine = startLine;
        this.stopLine = stopLine;
        this.simpleName = simpleName;
        this.methodDeclaration = methodDeclaration;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
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

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public MethodDeclaration getMethodDeclaration() {
        return methodDeclaration;
    }

    public void setMethodDeclaration(MethodDeclaration methodDeclaration) {
        this.methodDeclaration = methodDeclaration;
    }

    public String getEnclosingClassName() {
        return enclosingClassName;
    }

    public void setEnclosingClassName(String enclosingClassName) {
        this.enclosingClassName = enclosingClassName;
    }

}

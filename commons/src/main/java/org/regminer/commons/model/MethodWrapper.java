package org.regminer.commons.model;

import lombok.Getter;
import lombok.Setter;
import spoon.reflect.declaration.CtMethod;

/**
 * @author sxz
 * 该类进一步的封装了MethodDeclaration
 */
@Getter
@Setter
public class MethodWrapper {

    private String enclosingClassName;
    private String signature;
    private int startLine;
    private int stopLine;
    private String simpleName;
    private CtMethod<?> ctMethod;

    public MethodWrapper(String signature) {
        this.signature = signature;
    }

    public MethodWrapper(String enclosingClassName, String signature, int startLine, int stopLine, String simpleName,
                         CtMethod<?> ctMethod) {
        this.enclosingClassName = enclosingClassName;
        this.signature = signature;
        this.startLine = startLine;
        this.stopLine = stopLine;
        this.simpleName = simpleName;
        this.ctMethod = ctMethod;
    }
}

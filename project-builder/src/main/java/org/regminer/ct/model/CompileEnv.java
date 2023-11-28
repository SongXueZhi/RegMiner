package org.regminer.ct.model;


import org.regminer.ct.domain.Compiler;
import org.regminer.ct.domain.JDK;

public class CompileEnv {
    private Compiler compiler;
    private JDK jdk;
    private boolean isMultipleModules;

    public Compiler getCompiler() {
        return compiler;
    }

    public void setCompiler(Compiler compiler) {
        this.compiler = compiler;
    }

    public JDK getJdk() {
        return jdk;
    }

    public void setJdk(JDK jdk) {
        this.jdk = jdk;
    }

    public boolean isMultipleModules() {
        return isMultipleModules;
    }

    public void setMultipleModules(boolean multipleModules) {
        isMultipleModules = multipleModules;
    }

}

package org.regminer.miner.ct.model;


import org.regminer.miner.ct.domain.Compiler;
import org.regminer.miner.ct.domain.JDK;

public class CompileWay {
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

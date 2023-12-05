package org.regminer.ct.model;


import org.regminer.ct.domain.Compiler;
import org.regminer.ct.domain.JDK;

import java.io.File;

public class CompileTestEnv {
    private Compiler compiler;
    private JDK jdk;
    private boolean isMultipleModules;
    private File projectDir;
    private CtCommands ctCommand;
    private String osName;


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

    public File getProjectDir() {
        return projectDir;
    }

    public void setProjectDir(File projectDir) {
        this.projectDir = projectDir;
    }

    public CtCommands getCtCommand() {
        return ctCommand;
    }

    public void setCtCommand(CtCommands ctCommand) {
        this.ctCommand = ctCommand;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }
}

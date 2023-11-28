package org.regminer.ct.api;

import org.regminer.ct.domain.JDK;
import org.regminer.ct.model.CompileResult;
import org.regminer.ct.model.CtCommands;
import org.regminer.ct.model.TestResult;
import org.regminer.miner.common.model.TestCaseX;

import java.io.File;
import java.util.List;

abstract class Strategy {
    File projectDir;
    JDK[] jdkSearchRange;

    public File getProjectDir() {
        return projectDir;
    }

    public void setProjectDir(File projectDir) {
        this.projectDir = projectDir;
    }

    public JDK[] getJdkSearchRange() {
        return jdkSearchRange;
    }

    public void setJdkSearchRange(JDK[] jdkSearchRange) {
        this.jdkSearchRange = jdkSearchRange;
    }

    public abstract CompileResult compile();

    public abstract TestResult test(List<TestCaseX> testCaseXES, CtCommands recordCommands, boolean parallel);
}

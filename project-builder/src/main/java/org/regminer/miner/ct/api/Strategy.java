package org.regminer.miner.ct.api;

import org.regminer.miner.common.model.TestCaseX;
import org.regminer.miner.ct.domain.JDK;
import org.regminer.miner.ct.model.CompileResult;
import org.regminer.miner.ct.model.EnvCommands;
import org.regminer.miner.ct.model.TestResult;

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

    public abstract TestResult test(List<TestCaseX> testCaseXES, EnvCommands recordCommands, boolean parallel);
}

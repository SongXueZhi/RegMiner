package org.regminer.ct.api;

import org.regminer.ct.domain.JDK;
import org.regminer.ct.model.CompileResult;
import org.regminer.ct.model.CtCommands;
import org.regminer.ct.model.TestResult;
import org.regminer.miner.common.model.TestCaseX;

import java.io.File;
import java.util.List;

public class CtContext {
    private Strategy strategy;

    public CtContext(Strategy strategy) {
        this.strategy = strategy;
    }

    public CtContext setProjectDir(File projectDir) {
        strategy.setProjectDir(projectDir);
        return this;
    }

    public CtContext setJdkSearchRange(JDK[] jdkSearchRange) {
        strategy.setJdkSearchRange(jdkSearchRange);
        return this;
    }

    public CompileResult compile() {
        return this.strategy.compile();
    }


    public TestResult test(List<TestCaseX> testCaseXES, CtCommands envCommands) {
        return this.strategy.test(testCaseXES, envCommands, false);
    }

    public TestResult test(List<TestCaseX> testCaseXES, CtCommands envCommands, boolean parallel) {
        return this.strategy.test(testCaseXES, envCommands, parallel);
    }
}

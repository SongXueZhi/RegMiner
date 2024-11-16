package org.regminer.ct.api;

import org.regminer.commons.model.RelatedTestCase;
import org.regminer.ct.domain.JDK;
import org.regminer.ct.model.CompileResult;
import org.regminer.ct.model.CompileTestEnv;
import org.regminer.ct.model.TestResult;

import java.io.File;
import java.util.List;

public class CtContext {
    private final Strategy strategy;

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

    public CompileResult compile(CompileTestEnv compileTestEnv) {
        return this.strategy.compile(compileTestEnv);
    }


    public CompileResult compile(OriginCompileFixWay... compileFixWays) {
        return this.strategy.compile(compileFixWays);
    }


    public TestResult test(List<RelatedTestCase> testCaseXES, CompileTestEnv compileTestEnv) {
        return this.strategy.test(testCaseXES, compileTestEnv);
    }


}

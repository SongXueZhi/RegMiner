package org.regminer.ct.api;

import org.apache.commons.lang3.SerializationUtils;
import org.regminer.common.constant.Configurations;
import org.regminer.common.exec.ExecResult;
import org.regminer.common.exec.Executor;
import org.regminer.common.model.RelatedTestCase;
import org.regminer.common.utils.OSUtils;
import org.regminer.ct.CtReferees;
import org.regminer.ct.domain.Compiler;
import org.regminer.ct.domain.JDK;
import org.regminer.ct.model.*;
import org.regminer.ct.utils.CtUtils;

import java.util.List;
import java.util.stream.Stream;

public class BaseCompileAndTest extends Strategy {

    @Override
    public CompileResult compile() {
        CompileTestEnv compileTestEnv = new CompileTestEnv();
        compileTestEnv.setCtCommand(new CtCommands());
        compileTestEnv.setCompiler(Compiler.MVN);
        String osType = Configurations.osName;
        compileTestEnv.setOsName(osType);
        compileTestEnv.getCtCommand().takeCommand(CtCommands.CommandKey.JDK, JDK.J8.getCommand());

        String compileCommand = Compiler.MVN.getCompileCommand(osType);

        compileTestEnv.getCtCommand().takeCommand(CtCommands.CommandKey.COMPILE, compileCommand);

        return new CompileResult(
                CtReferees.JudgeCompileState(new Executor().setDirectory(projectDir)
                        .exec(compileTestEnv.getCtCommand().compute()).getMessage()), compileTestEnv.getCtCommand()
        );

    }

    @Override
    public CompileResult compile(CompileTestEnv compileTestEnv) {
        return null;
    }

    @Override
    public CompileResult compile(OriginCompileFixWay... compileFixWay) {
        return null;
    }

    @Override
    public TestResult test(List<RelatedTestCase> testCaseXES, CompileTestEnv compileTestEnv) {
        TestResult testResult = new TestResult();
        String osName = compileTestEnv.getOsName();
        Compiler compiler = compileTestEnv.getCompiler();
        CtCommands envCommands = SerializationUtils.clone(compileTestEnv.getCtCommand());

        envCommands.remove(CtCommands.CommandKey.COMPILE);

        Stream<RelatedTestCase> stream = testCaseXES.stream();
        stream.forEach(testCaseX -> {
            String testCommand = CtUtils.combineTestCommand(testCaseX, compiler, osName);
            envCommands.takeCommand(CtCommands.CommandKey.TEST, testCommand);

            ExecResult execResult = new Executor().setDirectory(projectDir).exec(envCommands.compute());
            TestCaseResult testCaseResult = CtReferees.judgeTestCaseResult(execResult);
            testCaseResult.setTestCommands(testCommand);
            testResult.takeTestCaseResult(testCaseX.toString(), testCaseResult);
            envCommands.remove(CtCommands.CommandKey.TEST);

        });
        return testResult;
    }

}

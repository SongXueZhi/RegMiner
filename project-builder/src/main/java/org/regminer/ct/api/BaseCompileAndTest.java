package org.regminer.ct.api;

import org.apache.commons.lang3.SerializationUtils;
import org.regminer.common.exec.ExecResult;
import org.regminer.ct.CtReferees;
import org.regminer.ct.domain.Compiler;
import org.regminer.ct.domain.JDK;
import org.regminer.common.exec.Executor;
import org.regminer.ct.model.*;
import org.regminer.ct.utils.CtUtils;
import org.regminer.common.utils.OSUtils;
import org.regminer.common.model.TestCaseX;

import java.util.List;
import java.util.stream.Stream;

public class BaseCompileAndTest extends Strategy {

    @Override
    public CompileResult compile() {
        CtCommands envCommands = new CtCommands();
        envCommands.setCompiler(Compiler.MVN);
        String osType = OSUtils.getOSType();
        envCommands.setOsName(osType);
        envCommands.takeCommand(CtCommands.CommandKey.JDK, JDK.J8.getCommand());

        String compileCommand = Compiler.MVN.getCompileCommand(osType);

        envCommands.takeCommand(CtCommands.CommandKey.COMPILE, compileCommand);

        return new CompileResult(
                CtReferees.JudgeCompileState(new Executor(osType).setDirectory(projectDir)
                        .exec(envCommands.compute()).getMessage()), envCommands
        );

    }

    @Override
    public TestResult test(List<TestCaseX> testCaseXES, CtCommands recordCommands, boolean parallel) {
        TestResult testResult = new TestResult();
        String osName = recordCommands.getOsName();
        Compiler compiler = recordCommands.getCompiler();
        CtCommands envCommands = SerializationUtils.clone(recordCommands);

        envCommands.remove(CtCommands.CommandKey.COMPILE);

        Stream<TestCaseX> stream = parallel ? testCaseXES.parallelStream() : testCaseXES.stream();
        stream.forEach(testCaseX -> {
            String testCommand = CtUtils.combineTestCommand(testCaseX, compiler, osName);
            envCommands.takeCommand(CtCommands.CommandKey.TEST, testCommand);

            ExecResult execResult = new Executor(osName).setDirectory(projectDir).exec(envCommands.compute());
            TestCaseResult testCaseResult = CtReferees.judgeTestCaseResult(execResult);
            testCaseResult.setTestCommands(testCommand);
            testResult.takeTestCaseResult(testCaseX.toString(), testCaseResult);
            envCommands.remove(CtCommands.CommandKey.TEST);

        });
        return testResult;
    }

}

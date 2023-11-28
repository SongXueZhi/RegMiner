package org.regminer.miner.ct.api;

import org.apache.commons.lang3.SerializationUtils;
import org.regminer.miner.common.model.TestCaseX;
import org.regminer.miner.ct.CtReferees;
import org.regminer.miner.ct.domain.Compiler;
import org.regminer.miner.ct.domain.JDK;
import org.regminer.miner.ct.exec.Executor;
import org.regminer.ct.model.*;
import org.regminer.miner.ct.utils.CtUtils;
import org.regminer.miner.ct.utils.OSUtils;
import org.regminer.miner.ct.model.*;

import java.util.List;
import java.util.stream.Stream;

public class BaseCompileAndTest extends Strategy {

    @Override
    public CompileResult compile() {
        EnvCommands envCommands = new EnvCommands();
        envCommands.setCompiler(Compiler.MVN);
        String osType = OSUtils.getOSType();
        envCommands.setOsName(osType);
        envCommands.takeCommand(EnvCommands.CommandKey.JDK, JDK.J8.getCommand());

        String compileCommand = Compiler.MVN.getCompileCommand(osType);

        envCommands.takeCommand(EnvCommands.CommandKey.COMPILE, compileCommand);

        return new CompileResult(
                CtReferees.JudgeCompileState(new Executor(osType).setDirectory(projectDir)
                        .exec(envCommands.compute()).getMessage()), envCommands
        );

    }

    @Override
    public TestResult test(List<TestCaseX> testCaseXES, EnvCommands recordCommands, boolean parallel) {
        TestResult testResult = new TestResult();
        String osName = recordCommands.getOsName();
        Compiler compiler = recordCommands.getCompiler();
        EnvCommands envCommands = SerializationUtils.clone(recordCommands);

        envCommands.remove(EnvCommands.CommandKey.COMPILE);

        Stream<TestCaseX> stream = parallel ? testCaseXES.parallelStream() : testCaseXES.stream();
        stream.forEach(testCaseX -> {
            String testCommand = CtUtils.combineTestCommand(testCaseX, compiler, osName);
            envCommands.takeCommand(EnvCommands.CommandKey.TEST, testCommand);

            ExecResult execResult = new Executor(osName).setDirectory(projectDir).exec(envCommands.compute());
            TestCaseResult testCaseResult = CtReferees.judgeTestCaseResult(execResult);
            testCaseResult.setTestCommands(testCommand);
            testResult.takeTestCaseResult(testCaseX.toString(), testCaseResult);
            envCommands.remove(EnvCommands.CommandKey.TEST);

        });
        return testResult;
    }

}

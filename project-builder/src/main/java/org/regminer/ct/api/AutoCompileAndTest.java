package org.regminer.ct.api;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.tools.ant.DirectoryScanner;
import org.regminer.common.exec.ExecResult;
import org.regminer.common.exec.Executor;
import org.regminer.common.model.RelatedTestCase;
import org.regminer.common.utils.OSUtils;
import org.regminer.ct.CtReferees;
import org.regminer.ct.domain.Compiler;
import org.regminer.ct.domain.JDKs;
import org.regminer.ct.model.*;
import org.regminer.ct.utils.CtUtils;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

public class AutoCompileAndTest extends Strategy {

    public AutoCompileAndTest() {
        super();

    }

    @Override
    public CompileResult compile() {
        CompileResult compileResult = new CompileResult();

        CompileTestEnv compileTestEnv = initializeCompileTestEnv();
        initializeCompileCommand(compileTestEnv);

        String message =
                new Executor(compileTestEnv.getOsName()).setDirectory(projectDir).exec(compileTestEnv.getCtCommand().compute()).getMessage();

        CompileResult.CompileState compileState = CtReferees.JudgeCompileState(message);
        if (compileState == CompileResult.CompileState.CE) {
            compileResult.setExceptionMessage(message);
        } else {
            compileResult.setCompileWay(compileTestEnv);
        }
        return compileResult;

    }

    @Override
    public CompileResult compile(CompileTestEnv compileTestEnv) {
        CompileResult compileResult = new CompileResult();
        String message =
                new Executor(compileTestEnv.getOsName()).setDirectory(projectDir).exec(compileTestEnv.getCtCommand().compute()).getMessage();

        CompileResult.CompileState compileState = CtReferees.JudgeCompileState(message);
        compileResult.setState(compileState);
        if (compileState == CompileResult.CompileState.CE) {
            compileResult.setExceptionMessage(message);
        } else {
            compileResult.setCompileWay(compileTestEnv);
        }
        return compileResult;
    }

    private void initializeCompileCommand(CompileTestEnv compileTestEnv) {
        compileTestEnv.getCtCommand().takeCommand(CtCommands.CommandKey.JDK,
                JDKs.jdkSearchRange[JDKs.getCurIndex()].getCommand());
        compileTestEnv.getCtCommand().takeCommand(CtCommands.CommandKey.COMPILE,
                compileTestEnv.getCompiler().getCompileCommand(compileTestEnv.getOsName()));
    }

    private CompileTestEnv initializeCompileTestEnv() {
        CompileTestEnv compileTestEnv = new CompileTestEnv(); //编译、测试环境
        compileTestEnv.setCtCommand(new CtCommands());// 编译和测试命令集合
        compileTestEnv.setOsName(OSUtils.getOSType());
        compileTestEnv.setProjectDir(projectDir);
        compileTestEnv.setCompiler(detectBuildTool(projectDir));//配置编译器，例如mvn、gradle
        return compileTestEnv;
    }

    @Override
    public CompileResult compile(CompileFixWay... compileFixWays) {
        CompileResult compileResult = new CompileResult();

        CompileTestEnv compileTestEnv = initializeCompileTestEnv();
        initializeCompileCommand(compileTestEnv);

        String message =
                new Executor(compileTestEnv.getOsName()).setDirectory(projectDir).exec(compileTestEnv.getCtCommand().compute()).getMessage();

        CompileResult.CompileState compileState = CtReferees.JudgeCompileState(message);
        compileResult.setState(compileState);
        if (compileState == CompileResult.CompileState.SUCCESS) {
            compileResult.setCompileWay(compileTestEnv);
            return compileResult;
        }
        //TODO 根据编译失败的原因，选择不同的修复方式
        //装填编译命令，包括环境配置和编译指令
        //获取最高分的JDK，作为环境配置
        for (CompileFixWay compileFixWay : compileFixWays) {
            compileResult = compileFixWay.fix(compileTestEnv);
        }

        return compileResult;
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

            ExecResult execResult = new Executor(osName).setDirectory(projectDir).exec(envCommands.compute());
            TestCaseResult testCaseResult = CtReferees.judgeTestCaseResult(execResult);
            testCaseResult.setTestCommands(testCommand);
            testResult.takeTestCaseResult(testCaseX.toString(), testCaseResult);

        });
        return testResult;
    }


    public Compiler detectBuildTool(File projectDir) {
        Compiler buildTool = Compiler.MVN;
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(projectDir);
        scanner.setIncludes(new String[]{"pom.xml", "**\\pom.xml", "build.gradle", "**\\build.gradle",
                "maven-wrapper.properties", "**\\maven-wrapper.properties",
                "gradle-wrapper.properties", "**\\gradle-wrapper.properties"});
        scanner.setCaseSensitive(true);
        scanner.scan();
        String[] files = scanner.getIncludedFiles();
        for (String file : files) {
            if (file.contains("maven-wrapper.properties")) {
                buildTool = Compiler.MVNW;
                break;
            } else if (file.contains("gradle-wrapper.properties")) {
                buildTool = Compiler.GRADLEW;
                break;
            } else if (file.contains("pom.xml")) {
                buildTool = Compiler.MVN;
            } else if (file.contains("build.gradle")) {
                buildTool = Compiler.GRADLE;
            } else {
                System.out.println("没有找到构建工具配置文件，默认使用mvn命令");
            }
        }
        return buildTool;
    }

}

package org.regminer.ct.api;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.tools.ant.DirectoryScanner;
import org.regminer.common.exec.ExecResult;
import org.regminer.common.exec.Executor;
import org.regminer.ct.CtReferees;
import org.regminer.ct.domain.Compiler;
import org.regminer.ct.domain.JDK;
import org.regminer.ct.model.*;
import org.regminer.ct.utils.CtUtils;
import org.regminer.common.utils.OSUtils;
import org.regminer.common.model.TestCaseX;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

public class AutoCompileAndTest extends Strategy{
    @Override
    public CompileResult compile() {
        CompileEnv compileEnv = new CompileEnv();
        CtCommands envCommands = new CtCommands();
        compileEnv.setCompiler(detectBuildTool(projectDir));
        envCommands.setCompiler(compileEnv.getCompiler());
        String osType = OSUtils.getOSType();
        envCommands.setOsName(osType);
        String compileCommand = compileEnv.getCompiler().getCompileCommand(osType);

        CompileResult.CompileState compileState = CompileResult.CompileState.CE;
        CompileResult compileResult = new CompileResult(compileState);
        String message = "";

        for (JDK jdk : JDK.values()) {
            envCommands.takeCommand(CtCommands.CommandKey.JDK, jdk.getCommand());
            envCommands.takeCommand(CtCommands.CommandKey.COMPILE, compileCommand);
            message = new Executor(osType).setDirectory(projectDir).exec(envCommands.compute()).getMessage();
            compileState = CtReferees.JudgeCompileState(message);
            if(compileState == CompileResult.CompileState.SUCCESS){
                compileEnv.setJdk(jdk);
                compileResult = new CompileResult(compileState,envCommands,compileEnv);
                break;
            }else {
                envCommands.remove(CtCommands.CommandKey.JDK);
                envCommands.remove(CtCommands.CommandKey.COMPILE);
            }
        }
        if(compileState == CompileResult.CompileState.CE){
            compileResult.setExceptionMessage(message);
        }

        return compileResult;

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
            envCommands.remove(CtCommands.CommandKey.COMPILE);

        });
        return testResult;    }

    public Compiler detectBuildTool(File projectDir){
        Compiler buildTool = Compiler.MVN;
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(projectDir);
        scanner.setIncludes(new String[] {"pom.xml","**\\pom.xml","build.gradle","**\\build.gradle",
                "maven-wrapper.properties","**\\maven-wrapper.properties",
                "gradle-wrapper.properties","**\\gradle-wrapper.properties"});
        scanner.setCaseSensitive(true);
        scanner.scan();
        String[] files = scanner.getIncludedFiles();
        for(String file: files){
            if(file.contains("maven-wrapper.properties")){
                buildTool = Compiler.MVNW;
                break;
            } else if(file.contains("gradle-wrapper.properties")){
                buildTool = Compiler.GRADLEW;
                break;
            } else if(file.contains("pom.xml")){
                buildTool = Compiler.MVN;
            }else if(file.contains("build.gradle")){
                buildTool = Compiler.GRADLE;
            }else {
                System.out.println("没有找到构建工具配置文件，默认使用mvn命令");
            }
        }
        return buildTool;
    }

 }

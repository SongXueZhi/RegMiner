package org.regminer.ct.api;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.tools.ant.DirectoryScanner;
import org.regminer.common.constant.Configurations;
import org.regminer.common.constant.Constant;
import org.regminer.common.exec.ExecResult;
import org.regminer.common.exec.Executor;
import org.regminer.common.model.ModuleNode;
import org.regminer.common.model.RelatedTestCase;
import org.regminer.common.tool.parser.ModuleParser;
import org.regminer.ct.CtReferees;
import org.regminer.ct.domain.Compiler;
import org.regminer.ct.domain.JDKs;
import org.regminer.ct.model.*;
import org.regminer.ct.utils.CtUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

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
                new Executor().setDirectory(projectDir).exec(compileTestEnv.getCtCommand().compute()).getMessage();

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
                new Executor().setDirectory(projectDir).exec(compileTestEnv.getCtCommand().compute()).getMessage();

        CompileResult.CompileState compileState = CtReferees.JudgeCompileState(message);
        compileResult.setState(compileState);
        if (compileState == CompileResult.CompileState.CE) {
            compileResult.setExceptionMessage(message);
        } else {
            compileResult.setCompileWay(compileTestEnv);
        }
        return compileResult;
    }


    @Override
    public CompileResult compile(OriginCompileFixWay... compileFixWays) {
        CompileResult compileResult = new CompileResult();

        CompileTestEnv compileTestEnv = initializeCompileTestEnv();
        initializeCompileCommand(compileTestEnv);

        String message =
                new Executor().setDirectory(projectDir).exec(compileTestEnv.getCtCommand().compute()).getMessage();

        CompileResult.CompileState compileState = CtReferees.JudgeCompileState(message);
        compileResult.setState(compileState);
        compileResult.setExceptionMessage(message);
        if (compileState == CompileResult.CompileState.SUCCESS) {
            compileResult.setCompileWay(compileTestEnv);
            return compileResult;
        }
        //TODO 根据编译失败的原因，选择不同的修复方式
        //装填编译命令，包括环境配置和编译指令
        //获取最高分的JDK，作为环境配置
        //排序。迁移前，优先修复 pom 问题
        List<OriginCompileFixWay> originCompileFixWayList = Arrays.asList(compileFixWays);
        originCompileFixWayList.sort(Comparator.comparing(OriginCompileFixWay::getOrder));
        for (OriginCompileFixWay compileFixWay : originCompileFixWayList) {
            compileResult = compileFixWay.fix(compileTestEnv, compileResult.getExceptionMessage());
            if (compileResult.getState() == CompileResult.CompileState.SUCCESS) {
                logger.info("command: {}", compileTestEnv.getCtCommand().compute());
                return compileResult;
            }
        }
        return compileResult;
    }

    private void initializeCompileCommand(CompileTestEnv compileTestEnv) {
        compileTestEnv.getCtCommand().takeCommand(CtCommands.CommandKey.JDK,
                JDKs.jdkSearchRange[JDKs.getCurIndex()].getCommand());
        compileTestEnv.getCtCommand().takeCommand(CtCommands.CommandKey.COMPILE,
                compileTestEnv.getCompiler().getCompileCommand(compileTestEnv.getOsName(), ""));
    }

    private CompileTestEnv initializeCompileTestEnv() {
        CompileTestEnv compileTestEnv = new CompileTestEnv(); //编译、测试环境
        compileTestEnv.setCtCommand(new CtCommands());// 编译和测试命令集合
        compileTestEnv.setOsName(Configurations.osName);
        compileTestEnv.setProjectDir(projectDir);
        compileTestEnv.setCompiler(detectBuildTool(projectDir));//配置编译器，例如mvn、gradle
        // 解析模块
        compileTestEnv.setModuleNode(parseModuleNode(projectDir));
        compileTestEnv.setMultipleModules(compileTestEnv.getModuleNode() != null &&
                !compileTestEnv.getModuleNode().getSubModules().isEmpty());
        return compileTestEnv;
    }

    @Override
    public TestResult test(List<RelatedTestCase> testCaseXES, CompileTestEnv compileTestEnv) {
        TestResult testResult = new TestResult();
        Map<String, Integer> methodCountPerClass = countMethodsPerClass(testCaseXES);
        Set<String> classesToTestWhole = determineClassesForWholeTest(methodCountPerClass);

        testIndividually(testCaseXES, compileTestEnv, classesToTestWhole, testResult);
        testAsClass(testCaseXES, compileTestEnv, classesToTestWhole, testResult);

        return testResult;
    }

    private Map<String, Integer> countMethodsPerClass(List<RelatedTestCase> testCaseXES) {
        Map<String, Integer> methodCount = new HashMap<>();
        for (RelatedTestCase testCase : testCaseXES) {
            String className = testCase.getEnclosingClassName();
            methodCount.put(className, methodCount.getOrDefault(className, 0) + 1);
        }
        return methodCount;
    }

    private Set<String> determineClassesForWholeTest(Map<String, Integer> methodCountPerClass) {
        return methodCountPerClass.entrySet().stream()
                .filter(entry -> entry.getValue() >= Constant.TEST_CASE_THRESHOLD)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    private void testIndividually(List<RelatedTestCase> testCaseXES, CompileTestEnv compileTestEnv,
                                  Set<String> classesToTestWhole, TestResult testResult) {
        CtCommands envCommands = SerializationUtils.clone(compileTestEnv.getCtCommand());
        envCommands.remove(CtCommands.CommandKey.COMPILE);

        String osName = compileTestEnv.getOsName();
        Compiler compiler = compileTestEnv.getCompiler();
        ModuleNode moduleNode = compileTestEnv.getModuleNode();

        for (RelatedTestCase testCase : testCaseXES) {
            String className = testCase.getEnclosingClassName();
            if (!classesToTestWhole.contains(className)) {
                String testCommand = CtUtils.combineTestCommand(testCase, compiler, osName, moduleNode);
                envCommands.takeCommand(CtCommands.CommandKey.TEST, testCommand);
                ExecResult execResult = new Executor().setDirectory(compileTestEnv.getProjectDir()).exec(envCommands.compute(), 2);
                TestCaseResult testCaseResult = CtReferees.judgeTestCaseResult(execResult);
                testCaseResult.setTestCommands(testCommand);
                logger.info("command: {}, result: {}", testCommand, testCaseResult.getState());
                if (testCaseResult.getState() == TestCaseResult.TestState.NOTEST) {
                    // 如果测试结果为 NOTEST，将其类添加到整体测试列表中
                    classesToTestWhole.add(className);
                } else {
                    // 否则，添加测试结果到测试结果集
                    testResult.takeTestCaseResult(testCase.toString(), testCaseResult);
                }
            }
        }
    }

    private void testAsClass(List<RelatedTestCase> testCaseXES, CompileTestEnv compileTestEnv,
                             Set<String> classesToTestWhole, TestResult testResult) {
        CtCommands envCommands = SerializationUtils.clone(compileTestEnv.getCtCommand());
        envCommands.remove(CtCommands.CommandKey.COMPILE);

        String osName = compileTestEnv.getOsName();
        Compiler compiler = compileTestEnv.getCompiler();
        Map<String, List<RelatedTestCase>> classToTestWholeMap = testCaseXES.stream()
                .filter(relatedTestCase -> classesToTestWhole.contains(relatedTestCase.getEnclosingClassName()))
                .collect(Collectors.groupingBy(RelatedTestCase::getEnclosingClassName));

        for (Map.Entry<String, List<RelatedTestCase>> testWholeEntry : classToTestWholeMap.entrySet()) {
            // 测试整个类
            String testCommand = CtUtils.combineTestClassCommand(testWholeEntry.getValue().get(0), compiler,
                    osName, compileTestEnv.getModuleNode());
            envCommands.takeCommand(CtCommands.CommandKey.TEST, testCommand);
            ExecResult execResult = new Executor().setDirectory(compileTestEnv.getProjectDir()).exec(envCommands.compute(), 2);
            TestCaseResult classTestCaseResult = CtReferees.judgeTestCaseResult(execResult);
            classTestCaseResult.setTestCommands(testCommand);
            logger.info("command: {}, result: {}", testCommand, classTestCaseResult.getState());
            // 将类的测试结果应用于该类的所有测试案例
            for (RelatedTestCase testCase : testWholeEntry.getValue()) {
                if (!testResult.exists(testCase.toString())) {
                    testResult.takeTestCaseResult(testCase.toString(), classTestCaseResult);
                }
            }
        }
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

    public ModuleNode parseModuleNode(File projectDir) {
        for (ModuleParser parser : ModuleParser.values()) {
            try {
                return parser.parser(projectDir);
            } catch (Exception e) {
                e.printStackTrace();
                // 解析不存在则换下一个解析器
            }
        }
        return null;
    }

}

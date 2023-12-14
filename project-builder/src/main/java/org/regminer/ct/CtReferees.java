package org.regminer.ct;

import org.jetbrains.annotations.NotNull;
import org.regminer.common.exec.ExecResult;
import org.regminer.ct.model.CompileResult;
import org.regminer.ct.model.TestCaseResult;

import java.util.*;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CtReferees {
    static Logger logger = LogManager.getLogManager().getLogger("org.regminer.ct.CtReferees");

    public static CompileResult.CompileState JudgeCompileState(String message) {
        return message.toLowerCase().contains("build success") ? CompileResult.CompileState.SUCCESS :
                CompileResult.CompileState.CE;
    }

    public static TestCaseResult judgeTestCaseResult(ExecResult execResult) {

        TestCaseResult testCaseResult = new TestCaseResult();
        if (execResult.isTimeOut()){
            testCaseResult.setState(TestCaseResult.TestState.TE);
            return testCaseResult;
        }

        testCaseResult.setUsageTime(execResult.getUsageTime());
        String message = execResult.getMessage();

        // 检查message是否为null
        if (message != null) {
            message = message.toLowerCase();
            TestCaseResult.TestState testState = getTestState(message);
            testCaseResult.setState(testState);
        } else {
            testCaseResult.setState(TestCaseResult.TestState.UNKNOWN);
        }
//        testCaseResult.setExceptionMessage(spiltExceptionMessage(message, testState));
        return testCaseResult;
    }

    @NotNull
    private static TestCaseResult.TestState getTestState(String message) {
        TestCaseResult.TestState testState;
        if (message.contains("build success")) {
            testState = TestCaseResult.TestState.PASS;
        } else if (message.contains("compilation error") || message.contains("compilation failure")) {
            testState = TestCaseResult.TestState.CE;
        } else if (message.contains("no test")) {
            testState = TestCaseResult.TestState.NOTEST;
        } else {
            testState = TestCaseResult.TestState.FAL;
        }
        return testState;
    }

    public static String spiltExceptionMessage(String message, TestCaseResult.TestState testState) {
        if (testState != TestCaseResult.TestState.FAL) {
            return null;
        }
        String testResult = null;
        try {
            int splitStartNum = message.indexOf("t e s t s\n[info] ") + ("t e s t s\n[info] ").length();
            int splitEndNum = message.indexOf("[info] results:");
            testResult = message.substring(splitStartNum, splitEndNum).replace("-", "")
                    .replace("[info] ", "").replace("[error] ", "")
                    .replace("[info]", "").replace("[error]", "");
            System.out.println(testResult);
        } catch (Exception e) {
            logger.info(e.getLocalizedMessage());
        }

        return testResult;
    }
    public static List<String> detectProblematicDependencies(String message) {
        Set<String> problematicDependencies = new HashSet<>();
        // 查找依赖问题的正则表达式
        // [FATAL] ... com.fasterxml.jackson.core:jackson-databind:2.9.8 ... com.fasterxml.jackson:jackson-base:pom:2.9.8-SNAPSHOT
        // 匹配形如 "groupId:artifactId[:type]:version" 的依赖项
        String regex = "([a-zA-Z0-9\\.\\-_]+:[a-zA-Z0-9\\.\\-_]+(:[a-zA-Z0-9\\.\\-_]+)?:[a-zA-Z0-9\\.\\-_]+)";
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(message);

        // 遍历日志消息以查找与依赖相关的部分
        while (matcher.find()) {
            String dependency = matcher.group(1);
            // 将找到的依赖项添加到列表中
            problematicDependencies.add(dependency);
        }

        return new ArrayList<>(problematicDependencies);
    }

    public static Map<String, List<String>> detectClassNameConflicts(String logMessage) {
        Map<String, List<String>> fileToConflictingPackages = new HashMap<>();

        // 查找类名冲突问题的正则表达式
        String regex = "\\[ERROR\\] ([^\\[]+?):\\[\\d+,\\d+\\] 对.*的引用不明确\\s+(.*) 中的类 (.*) 和 (.*) 中的类 (.*) 都匹配";
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(logMessage);

        // 遍历日志消息以查找与类名冲突相关的部分
        while (matcher.find()) {
            String fileName = matcher.group(1).trim(); // 冲突的文件
            String package1 = matcher.group(3).trim(); // 第一个冲突的完整类名
            String package2 = matcher.group(5).trim(); // 第二个冲突的完整类名

            // 将包名添加到对应文件名的集合中
            fileToConflictingPackages.computeIfAbsent(fileName, k -> new ArrayList<>()).add(package1);
            fileToConflictingPackages.get(fileName).add(package2);
        }

        return fileToConflictingPackages;
    }
}

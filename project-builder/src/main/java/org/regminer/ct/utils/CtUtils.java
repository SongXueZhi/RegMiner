package org.regminer.ct.utils;

import org.regminer.common.model.ModuleNode;
import org.regminer.common.model.RelatedTestCase;
import org.regminer.ct.domain.Compiler;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CtUtils {
    public static String combineTestCommand(RelatedTestCase testCaseX, Compiler compiler,
                                            String osName, ModuleNode moduleNode) {
        String result = testCaseX.getEnclosingClassName();
        String modulePath = getModulePath(moduleNode, testCaseX);
        switch (compiler) {
            case GRADLE:
                return Compiler.GRADLE.getTestCommand(osName, modulePath) + result + "." + testCaseX.getMethodName();
            case GRADLEW:
                return Compiler.GRADLEW.getTestCommand(osName, modulePath) + result + "." + testCaseX.getMethodName();
            case MVNW:
                return Compiler.MVNW.getTestCommand(osName, modulePath) + result + "#" + testCaseX.getMethodName();
            case MVN:
            default:
                return Compiler.MVN.getTestCommand(osName, modulePath) + result + "#" + testCaseX.getMethodName();
        }
    }

    public static String combineTestClassCommand(RelatedTestCase relatedTestCase, Compiler compiler,
                                                 String osName, ModuleNode moduleNode) {
        String className = relatedTestCase.getEnclosingClassName();
        String modulePath = getModulePath(moduleNode, relatedTestCase);
        switch (compiler) {
            case GRADLE:
                return Compiler.GRADLE.getTestCommand(osName, modulePath) + className;
            case GRADLEW:
                return Compiler.GRADLEW.getTestCommand(osName, modulePath) + className;
            case MVNW:
                return Compiler.MVNW.getTestCommand(osName, modulePath) + className;
            case MVN:
            default:
                return Compiler.MVN.getTestCommand(osName, modulePath) + className;
        }
    }


    public static String getModulePath(ModuleNode rootModule, RelatedTestCase testCase) {
        List<String> path = new ArrayList<>();
        Path testFilePath = Paths.get(testCase.getRelativeFilePath());
        String targetModuleName = testFilePath.getName(0).toString().toLowerCase();
        if (findModulePath(rootModule, targetModuleName, path)) {
            // 移除根模块并返回路径
            path.remove(0);
            return String.join(File.separator, path);
        }
        return "";
    }

    private static boolean findModulePath(ModuleNode moduleNode, String targetModuleName, List<String> path) {
        path.add(moduleNode.getName());
        // 检查当前模块是否为目标模块
        if (moduleNode.getName().toLowerCase().equals(targetModuleName)) {
            return true;
        }
        // 递归检查子模块
        for (ModuleNode subModule : moduleNode.getSubModules()) {
            if (findModulePath(subModule, targetModuleName, path)) {
                return true;
            }
        }
        // 未找到路径，回溯
        path.remove(path.size() - 1);
        return false;
    }
}

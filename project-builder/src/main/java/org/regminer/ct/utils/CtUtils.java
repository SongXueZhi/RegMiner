package org.regminer.ct.utils;

import org.regminer.common.model.ModuleNode;
import org.regminer.common.model.RelatedTestCase;
import org.regminer.ct.domain.Compiler;

import java.io.File;

public class CtUtils {
    public static String combineTestCommand(RelatedTestCase testCaseX, Compiler compiler,
                                            String osName, ModuleNode moduleNode) {
        String result = testCaseX.getEnclosingClassName();
        String modulePath = getModulePath(moduleNode, testCaseX, "");
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
        String modulePath = getModulePath(moduleNode, relatedTestCase, "");
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

    private static String getModulePath(ModuleNode moduleNode, RelatedTestCase testCase, String parentPath) {
        String currentPath = parentPath.isEmpty() ? moduleNode.getName() : parentPath + File.separator + moduleNode.getName();
        // 检查该模块是否包含测试用例的路径
        String pModule = testCase.getRelativeFilePath().toLowerCase().split(File.separator)[0];
        if (moduleNode.getName().toLowerCase().equals(pModule)) {
            return moduleNode.getName();
        }

        // 递归检查所有子模块
        for (ModuleNode subModule : moduleNode.getSubModules()) {
            String subModulePath = getModulePath(subModule, testCase, currentPath);
            if (subModulePath != null && !subModulePath.isEmpty()) {
                return subModulePath;
            }
        }

        // 如果没有找到匹配的模块，返回 null 或空字符串
        return "";
    }
}

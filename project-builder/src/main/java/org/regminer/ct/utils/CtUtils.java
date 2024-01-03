package org.regminer.ct.utils;

import org.regminer.common.model.RelatedTestCase;
import org.regminer.ct.domain.Compiler;

public class CtUtils {

    public static String combineTestCommand(RelatedTestCase testCaseX, Compiler compiler, String osName) {
        //TODO handle multi-modules project
        String result = testCaseX.getEnclosingClassName();
        switch (compiler) {
            case GRADLE:
                return Compiler.GRADLE.getTestCommand(osName) + result + "." + testCaseX.getMethodName();
            case GRADLEW:
                return Compiler.GRADLEW.getTestCommand(osName) + result + "." + testCaseX.getMethodName();
            case MVNW:
                return Compiler.MVNW.getTestCommand(osName) + result + "#" + testCaseX.getMethodName();
            case MVN:
            default:
                return Compiler.MVN.getTestCommand(osName) + result + "#" + testCaseX.getMethodName();
        }
    }

    public static String combineTestClassCommand(RelatedTestCase relatedTestCase, Compiler compiler, String osName) {
        String result = relatedTestCase.getEnclosingClassName();
        return combineTestClassCommand(result, compiler, osName);
    }

    public static String combineTestClassCommand(String className, Compiler compiler, String osName) {
        switch (compiler) {
            case GRADLE:
                return Compiler.GRADLE.getTestCommand(osName) + className;
            case GRADLEW:
                return Compiler.GRADLEW.getTestCommand(osName) + className;
            case MVNW:
                return Compiler.MVNW.getTestCommand(osName) + className;
            case MVN:
            default:
                return Compiler.MVN.getTestCommand(osName) + className;
        }
    }
}

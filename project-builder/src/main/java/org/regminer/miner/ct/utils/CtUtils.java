package org.regminer.miner.ct.utils;

import org.regminer.miner.common.model.TestCaseX;
import org.regminer.miner.ct.domain.Compiler;

public class CtUtils {

    public static String combineTestCommand(TestCaseX testCaseX, Compiler compiler, String osName) {
        //TODO handle multi-modules project
        String result = testCaseX.getPackageName() + "." + testCaseX.getClassName();
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
}

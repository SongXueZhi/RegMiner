package org.regminer.ct.domain;

import org.apache.commons.lang3.StringUtils;
import org.regminer.commons.model.OS;

public enum Compiler {
    MVN {
        @Override
        public String getCompileCommand(String osVersion, boolean isMultipleModules, String modulePath) {
            return isMultipleModules ? "mvn clean install -Dmaven.test.skip=true" : "mvn clean compile test-compile";
        }

        @Override
        public String getTestCommand(String osVersion, String modulePath) {
            return "mvn " + (StringUtils.isEmpty(modulePath) ? "" : "-pl " + modulePath + " ") + "test -Dtest=";
        }
    },
    GRADLE {
        @Override
        public String getCompileCommand(String osVersion, boolean isMultipleModules, String modulePath) {
            return isMultipleModules ? "gradle build -x test -x checkstyleMain -x checkstyleTest" : "gradle compileJava compileTestJava";
        }

        @Override
        public String getTestCommand(String osVersion, String modulePath) {
            return "gradle " + (StringUtils.isEmpty(modulePath) ? "" : modulePath + ":") + "test --tests ";
        }
    },
    MVNW {
        @Override
        public String getCompileCommand(String osVersion, boolean isMultipleModules, String modulePath) {
            if (osVersion.equals(OS.WINDOWS)) {
                return isMultipleModules ? "mvnw.exe clean install -Dmaven.test.skip=true" : "mvnw.exe compile test-compile";
            } else {
                return isMultipleModules ? "./mvn clean install -Dmaven.test.skip=true" : "./mvn compile test-compile";
            }
        }

        @Override
        public String getTestCommand(String osVersion, String modulePath) {
            if (osVersion.equals(OS.WINDOWS)) {
                return "mvnw.exe test -Dtest=";
            } else {
                return "./mvnw " + (StringUtils.isEmpty(modulePath) ? "" : "-pl " + modulePath + " ") + "test -Dtest=";
            }
        }
    },
    GRADLEW {
        @Override
        public String getCompileCommand(String osVersion, boolean isMultipleModules, String modulePath) {
            if (osVersion.equals(OS.WINDOWS)) {
                return isMultipleModules ? "gradlew.exe build -x test -x checkstyleMain -x checkstyleTest" : "gradlew.exe compileJava compileTestJava";
            } else {
                return isMultipleModules ? "./gradlew build -x test -x checkstyleMain -x checkstyleTest" : "./gradlew compileJava compileTestJava";
            }
        }

        @Override
        public String getTestCommand(String osVersion, String modulePath) {
            if (osVersion.equals(OS.WINDOWS)) {
                return "gradlew.exe " + (StringUtils.isEmpty(modulePath) ? "" : modulePath + ":") + "test --tests";
            } else {
                return "./gradlew " + (StringUtils.isEmpty(modulePath) ? "" : modulePath + ":") + "test --tests ";
            }
        }
    };

    public abstract String getCompileCommand(String osVersion, boolean isMultipleModules, String modulePath);

    public abstract String getTestCommand(String osVersion, String modulePath);
}

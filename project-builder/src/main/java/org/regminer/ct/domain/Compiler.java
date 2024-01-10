package org.regminer.ct.domain;

import org.apache.commons.lang3.StringUtils;
import org.regminer.common.model.OS;

public enum Compiler {
    //TODO handle multi-modules project
    MVN {
        @Override
        public String getCompileCommand(String osVersion, String modulePath) {
            return "mvn clean compile test-compile";
        }

        @Override
        public String getTestCommand(String osVersion, String modulePath) {
            return "mvn " + (StringUtils.isEmpty(modulePath) ? "" : "-pl " + modulePath + " ") + "test -Dtest=";
        }
    },
    GRADLE {
        @Override
        public String getCompileCommand(String osVersion, String modulePath) {
            return "gradle compileJava compileTestJava";
        }

        @Override
        public String getTestCommand(String osVersion, String modulePath) {
            return "gradle test --tests ";
        }
    },
    MVNW {
        @Override
        public String getCompileCommand(String osVersion, String modulePath) {
            if (osVersion.equals(OS.WINDOWS)) {
                return "mvnw.exe compile test-compile";
            } else {
                return "./mvn compile test-compile";
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
        public String getCompileCommand(String osVersion, String modulePath) {
            if (osVersion.equals(OS.WINDOWS)) {
                return "gradlew.exe compileJava compileTestJava";
            } else {
                return "./gradlew compileJava compileTestJava";
            }
        }

        @Override
        public String getTestCommand(String osVersion, String modulePath) {
            if (osVersion.equals(OS.WINDOWS)) {
                return "gradlew.exe test --tests";
            } else {
                return "./gradlew test --tests ";
            }
        }
    };

    public abstract String getCompileCommand(String osVersion, String modulePath);

    public abstract String getTestCommand(String osVersion, String modulePath);
}

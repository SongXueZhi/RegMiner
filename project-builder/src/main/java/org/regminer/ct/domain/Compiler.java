package org.regminer.ct.domain;

public enum Compiler {
    MVN {
        @Override
        public String getCompileCommand(String osVersion) {
            return "mvn compile test-compile";
        }

        @Override
        public String getTestCommand(String osVersion) {
            return "mvn test -Dtest=";
        }
    },
    GRADLE {
        @Override
        public String getCompileCommand(String osVersion) {
            return "gradle compileJava compileTestJava";
        }

        @Override
        public String getTestCommand(String osVersion) {
            return "gradle test --tests ";
        }
    },
    MVNW {
        @Override
        public String getCompileCommand(String osVersion) {
            if (osVersion.equals(OS.WINDOWS)) {
                return "mvnw.exe compile test-compile";
            } else {
                return "./mvn compile test-compile";
            }
        }

        @Override
        public String getTestCommand(String osVersion) {
            if (osVersion.equals(OS.WINDOWS)) {
                return "mvnw.exe test -Dtest=";
            } else {
                return "./mvnw test -Dtest=";
            }
        }
    },
    GRADLEW {
        @Override
        public String getCompileCommand(String osVersion) {
            if (osVersion.equals(OS.WINDOWS)) {
                return "gradlew.exe compileJava compileTestJava";
            } else {
                return "./gradlew compileJava compileTestJava";
            }
        }

        @Override
        public String getTestCommand(String osVersion) {
            if (osVersion.equals(OS.WINDOWS)) {
                return "gradlew.exe test --tests";
            } else {
                return "./gradlew test --tests ";
            }
        }
    };

    public abstract String getCompileCommand(String osVersion);

    public abstract String getTestCommand(String osVersion);
}

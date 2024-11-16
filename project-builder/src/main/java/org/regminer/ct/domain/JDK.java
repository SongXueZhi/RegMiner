package org.regminer.ct.domain;

import org.regminer.commons.constant.Configurations;

public enum JDK {
    J7 {
        @Override
        public String getCommand() {
            if (Configurations.jenv_enable) {
                return "jenv local 1.7";
            } else {
                return "export JAVA_HOME=" + Configurations.j7File;
            }
        }
    },
    J8 {
        @Override
        public String getCommand() {
            if (Configurations.jenv_enable) {
                return "jenv local 1.8";
            } else {
                return "export JAVA_HOME=" + Configurations.j8File;
            }
        }
    },
    J11 {
        @Override
        public String getCommand() {
            if (Configurations.jenv_enable) {
                return "jenv local 11";
            } else {
                return "export JAVA_HOME=" + Configurations.j11File;
            }
        }
    },
    J17 {
        @Override
        public String getCommand() {
            if (Configurations.jenv_enable) {
                return "jenv local 17";
            } else {
                return "export JAVA_HOME=" + Configurations.j17File;
            }
        }
    };

    public abstract String getCommand();
}

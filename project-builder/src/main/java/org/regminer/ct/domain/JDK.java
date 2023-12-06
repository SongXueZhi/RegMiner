package org.regminer.ct.domain;

import org.regminer.common.constant.Configurations;

public enum JDK {

    J6 {
        @Override
        public String getCommand() {
            return "export JAVA_HOME=" + Configurations.j6File;
        }
    },

    J7 {
        @Override
        public String getCommand() {
            return "export JAVA_HOME=" + Configurations.j7File;

        }
    },
    J8 {
        @Override
        public String getCommand() {
            return "export JAVA_HOME=" + Configurations.j8File;
        }
    },
    J9 {
        @Override
        public String getCommand() {
            return "export JAVA_HOME=" + Configurations.j9File;
        }
    },

    J10 {
        @Override
        public String getCommand() {
            return "export JAVA_HOME=" + Configurations.j10File;
        }
    },

    J11 {
        @Override
        public String getCommand() {
            return "export JAVA_HOME=" + Configurations.j11File;
        }
    },
    J12 {
        @Override
        public String getCommand() {
            return "export JAVA_HOME=" + Configurations.j12File;
        }
    },

    J13 {
        @Override
        public String getCommand() {
            return "export JAVA_HOME=" + Configurations.j13File;
        }
    },
    J14 {
        @Override
        public String getCommand() {
            return "export JAVA_HOME=" + Configurations.j14File;
        }
    },
    J15 {
        @Override
        public String getCommand() {
            return "export JAVA_HOME=" + Configurations.j15File;
        }
    },

    J16 {
        @Override
        public String getCommand() {
            return "export JAVA_HOME=" + Configurations.j16File;
        }
    },
    J17 {
        @Override
        public String getCommand() {
            return "export JAVA_HOME=" + Configurations.j17File;
        }
    };

    public abstract String getCommand();
}

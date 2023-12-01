package org.regminer.ct.domain;

import org.regminer.common.constant.ConfigLoader;

public enum JDK {

    J8 {
        @Override
        public String getCommand() {
            return "export JAVA_HOME=" + ConfigLoader.j8File;
        }
    },

    J7 {
        @Override
        public String getCommand() {
            return "export JAVA_HOME=" + ConfigLoader.j7File;

        }
    },

    J11 {
        @Override
        public String getCommand() {
            return "export JAVA_HOME=" + ConfigLoader.j11File;
        }
    },

    J17 {
        @Override
        public String getCommand() {
            return "export JAVA_HOME=" + ConfigLoader.j17File;
        }
    },


    J15 {
        @Override
        public String getCommand() {
            return "export JAVA_HOME=" + ConfigLoader.j15File;
        }
    },
    J6 {
        @Override
        public String getCommand() {
            return "export JAVA_HOME=" + ConfigLoader.j6File;
        }
    },
    J9 {
        @Override
        public String getCommand() {
            return "export JAVA_HOME=" + ConfigLoader.j9File;
        }
    },
    J10 {
        @Override
        public String getCommand() {
            return "export JAVA_HOME=" + ConfigLoader.j10File;
        }
    },
    J12 {
        @Override
        public String getCommand() {
            return "export JAVA_HOME=" + ConfigLoader.j12File;
        }
    },
    J13 {
        @Override
        public String getCommand() {
            return "export JAVA_HOME=" + ConfigLoader.j13File;
        }
    },
    J14 {
        @Override
        public String getCommand() {
            return "export JAVA_HOME=" + ConfigLoader.j14File;
        }
    },
    J16 {
        @Override
        public String getCommand() {
            return "export JAVA_HOME=" + ConfigLoader.j16File;
        }
    };

    public abstract String getCommand();
}

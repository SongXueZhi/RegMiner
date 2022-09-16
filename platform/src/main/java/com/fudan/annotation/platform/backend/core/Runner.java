package com.fudan.annotation.platform.backend.core;

import java.io.File;

/**
 * description: revision run
 *
 * @author Richy
 * create: 2022-03-07 10:36
 **/
public class Runner {
    protected File revDir;
    protected String testCase;

    public Runner(File revDir, String testCase) {
        this.revDir = revDir;
        this.testCase = testCase;
    }

    public void getRunCode() {
        this.run();
    }

    private void run() {
        // execute the test
        String buildCommand = "mvn compile";
        String testCommand = "mvn test -Dtest=" + this.testCase + " " + "-Dmaven.test.failure.ignore=true";
        try {
             new Executor().setDirectory(this.revDir).exec(buildCommand,0);
             new Executor().setDirectory(this.revDir).exec(testCommand, 5);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
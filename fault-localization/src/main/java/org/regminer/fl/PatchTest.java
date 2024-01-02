package org.regminer.fl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PatchTest {

    final static Logger logger = LogManager.getLogger(PatchTest.class);

    String testFilePath;

    private List<String> testCases;

    private String jvmPath;

    private String classpath;

    private String externalProjPath;

    private String savePath;

    private String replicateLogPath;

    private String outputDir;

    private String binJavaDir;

    private String binTestDir;

    private String extraFailedMeththodPath;

    String flag;

    boolean runTestMethods = false;

    public PatchTest(String savePath, String jvmPath, String externalProjPath, String outputDir, String binJavaDir, String binTestDir, String classpath, String extraFailedMeththodPath) {
        this.savePath = savePath;
        this.jvmPath = jvmPath;
        this.externalProjPath = externalProjPath;
        this.outputDir = outputDir;
        this.binJavaDir = binJavaDir;
        this.binTestDir = binTestDir;
        this.extraFailedMeththodPath = extraFailedMeththodPath;
        this.classpath = classpath;
        replicateLogPath = this.outputDir + "/replicate.log";
    }

    public void configure(String testFilePath, boolean runTestMethods) {
        this.testFilePath = testFilePath;
        this.runTestMethods = runTestMethods;
        flag = "file";
    }

    public void configure(List<String> testCases, boolean runTestMethods) {
        this.testCases = testCases;
        this.runTestMethods = runTestMethods;
        flag = "str";
    }

    public List<String> runTests() {
        return runTests(null);
    }

    public List<String> runTests(String compileDir) {
        String cmd = "";
        cmd += this.jvmPath + "/java" + " -cp ";
        cmd += this.externalProjPath + File.pathSeparator;
        if (compileDir != null) {
            cmd += compileDir + File.pathSeparator;
        }
        if (!this.classpath.contains(this.binJavaDir)) {
            cmd += this.binJavaDir + File.pathSeparator;
        }
        if (!this.classpath.contains(this.binTestDir)) {
            cmd += this.binTestDir + File.pathSeparator;
        }
        cmd += this.classpath;
        if (flag.equals("str")) {
            cmd += " apr.junit.PatchTest -testStr ";
            for (String test : testCases) {
                cmd += test.trim() + File.pathSeparator;
            }
        } else if (flag.equals("file")) {
            cmd += " apr.junit.PatchTest -testFile " + testFilePath;
        } else {
            System.out.format("unknown flag of PatchTest: %s\n", flag);
        }
        if (runTestMethods) {
            cmd += " -runTestMethods true";
        }
        if (extraFailedMeththodPath != null) {
            cmd += " -extraFailedMeththodPath " + extraFailedMeththodPath;
        }
        cmd += " -savePath " + savePath + " > " + replicateLogPath + " 2>&1";
        System.out.println(cmd);
        logger.info("cmd: " + cmd);
        CmdUtil.runCmdNoOutput(cmd);
        FileUtil.writeToFile(replicateLogPath, "\n" + cmd, true);
        if (!new File(savePath).exists()) {
            return new ArrayList<>();
        }
        List<String> failedMethodsAfterTest = FileUtil.readFile(savePath);
        return failedMethodsAfterTest;
    }
}
package org.regminer.ct.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestCaseResult {

    private TestState state;
    private long usageTime;
    private String exceptionMessage;
    private String testCommands;
    private Logger logger = LogManager.getLogger(this);

    public String getTestCommands() {
        return testCommands;
    }

    public void setTestCommands(String testCommands) {
        this.testCommands = testCommands;
    }

    public TestState getState() {
        return state;
    }

    public void setState(TestState state) {
        this.state = state;
    }

    public long getUsageTime() {
        return usageTime;
    }

    public void setUsageTime(long usageTime) {
        this.usageTime = usageTime;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }



    public void setExceptionMessage(String exceptionMessage) {
        if (exceptionMessage == null) {
            return;
        }
        String ansiPattern = "\\x1B\\[[0-?]*[ -/]*[@-~]";
        this.exceptionMessage = exceptionMessage.replaceAll(ansiPattern, "");//remove ANSI escape code
    }

    public enum TestState {
        PASS,
        FAL,
        CE,
        TE,
        NOTEST,
        UNKNOWN,
        NOMARK
    }

    @Override
    public String toString() {
        return "TestCaseResult{" +
                "TestState='" + state.name() + '\'' +
                ", usageTime='" + usageTime + '\'' +
                '}';
    }

    public void execMsgToFile(String storeFilePath) {
        File storeFile = new File(storeFilePath);
        if (exceptionMessage == null || exceptionMessage.isEmpty()) {
            return;
        }

        if (!storeFile.exists()) {
            try {
                storeFile.createNewFile();
            } catch (IOException e) {
                if (logger != null) {
                    logger.error("Failed to create file: " + storeFilePath);
                } else {
                    System.out.println("Failed to create file: " + storeFilePath);
                }
            }

            try (FileWriter fileWriter = new FileWriter(storeFile)) {
                fileWriter.write(exceptionMessage);
            } catch (IOException e) {
                if (logger != null) {
                    logger.error("Failed to write to file: " + storeFilePath);
                } else {
                    System.out.println("Failed to write to file: " + storeFilePath);
                }
            }
        }
    }
}

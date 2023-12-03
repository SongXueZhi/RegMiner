package org.regminer.ct.model;

public class TestCaseResult {

    private TestState state;
    private long usageTime;
    private String exceptionMessage;
    private String testCommands;

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
        this.exceptionMessage = exceptionMessage;
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
}

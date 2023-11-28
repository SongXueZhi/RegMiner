package org.regminer.miner.ct.model;

public class CompileResult {

    private CompileState state;
    private EnvCommands envCommands;
    private CompileWay compileWay;
    private String exceptionMessage;

    public CompileResult() {
    }

    public CompileResult(CompileState state) {
        this.state = state;
    }
    public CompileResult(CompileState state, EnvCommands envCommands) {
        this.state = state;
        this.envCommands = envCommands;
    }
    
    public CompileResult(CompileState state, EnvCommands envCommands, CompileWay compileWay) {
        this.state = state;
        this.envCommands = envCommands;
        this.compileWay = compileWay;
    }

    public CompileState getState() {
        return state;
    }

    public void setState(CompileState state) {
        this.state = state;
    }

    public EnvCommands getEnvCommands() {
        return envCommands;
    }

    public void setEnvCommands(EnvCommands envCommands) {
        this.envCommands = envCommands;
    }

    public CompileWay getCompileWay() {
        return compileWay;
    }

    public void setCompileWay(CompileWay compileWay) {
        this.compileWay = compileWay;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public enum CompileState{
       SUCCESS,
       CE,
   }
}

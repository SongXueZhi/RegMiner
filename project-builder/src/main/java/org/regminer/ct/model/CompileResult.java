package org.regminer.ct.model;

public class CompileResult {

    private CompileState state;
    private CtCommands envCommands;
    private CompileEnv compileWay;
    private String exceptionMessage;

    public CompileResult() {
    }

    public CompileResult(CompileState state) {
        this.state = state;
    }
    public CompileResult(CompileState state, CtCommands envCommands) {
        this.state = state;
        this.envCommands = envCommands;
    }
    
    public CompileResult(CompileState state, CtCommands envCommands, CompileEnv compileWay) {
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

    public CtCommands getEnvCommands() {
        return envCommands;
    }

    public void setEnvCommands(CtCommands envCommands) {
        this.envCommands = envCommands;
    }

    public CompileEnv getCompileWay() {
        return compileWay;
    }

    public void setCompileWay(CompileEnv compileWay) {
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

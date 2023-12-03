package org.regminer.ct.model;

import org.regminer.ct.domain.Compiler;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.regminer.ct.model.CtCommands.CommandKey.*;

public class CtCommands implements Serializable {

    Map<CommandKey, String> commands = new HashMap<>();
    private Compiler compiler;
    private String osName;

    public Compiler getCompiler() {
        return compiler;
    }

    public void setCompiler(Compiler compiler) {
        this.compiler = compiler;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public CtCommands takeCommand(CommandKey key, String command) {
        if (command == null || command.trim() == "") {
            return this;
        }
        commands.put(key, command);
        return this;
    }

    public void remove(CommandKey key) {
        commands.remove(key);
    }

    public int sizes() {
        return commands.size();
    }

    public String compute() {
        String computeCommands = "";
        if (commands.containsKey(JDK)) {
            computeCommands = computeCommands + commands.get(JDK) + ";";
        }
        if (commands.containsKey(COMPILE)) {
            computeCommands = computeCommands + commands.get(COMPILE) + ";";
        }
        if (commands.containsKey(TEST)) {
            computeCommands = computeCommands + commands.get(TEST) + ";";
        }
        return computeCommands;
//        return String.join(";", commands.values());
    }

    public enum CommandKey {
        JDK,
        COMPILE,
        TEST,
    }

}

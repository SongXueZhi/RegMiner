package org.regminer.ct.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.regminer.ct.model.CtCommands.CommandKey.*;

public class CtCommands implements Serializable {

    Map<CommandKey, String> commands = new HashMap<>();

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
//        return String.join(";", commands.values()); //在组合的时候有顺序，使用了if
    }

    public enum CommandKey {
        JDK,
        COMPILE,
        TEST,
    }

}

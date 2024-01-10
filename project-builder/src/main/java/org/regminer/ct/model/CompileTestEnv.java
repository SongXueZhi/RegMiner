package org.regminer.ct.model;


import lombok.Data;
import org.regminer.common.model.ModuleNode;
import org.regminer.ct.domain.Compiler;
import org.regminer.ct.domain.JDK;

import java.io.File;
@Data
public class CompileTestEnv {
    private Compiler compiler;
    private JDK jdk;
    private boolean isMultipleModules;
    private File projectDir;
    private CtCommands ctCommand;
    private String osName;
    private ModuleNode moduleNode;
}

package org.regminer.ct.api;

import org.regminer.common.exec.Executor;
import org.regminer.ct.CtReferees;
import org.regminer.ct.domain.JDK;
import org.regminer.ct.domain.JDKs;
import org.regminer.ct.model.CompileResult;
import org.regminer.ct.model.CompileTestEnv;
import org.regminer.ct.model.CtCommands;

import java.util.logging.LogManager;
import java.util.logging.Logger;

public enum OriginCompileFixWay {
    JDK_SEARCH {
        @Override
        CompileResult fix(CompileTestEnv compileEnv) {
            CompileResult compileResult = new CompileResult(CompileResult.CompileState.CE);

            // Iterate left from the current index
            for (int i = JDKs.getCurIndex() - 1; i >= 0; i--) {
                compileResult = tryCompileWithJDK(compileEnv, JDKs.jdkSearchRange[i]);
                if (compileResult.getState() == CompileResult.CompileState.SUCCESS) {
                    JDKs.setCurIndex(i);
                    return compileResult;
                }
            }

            // Iterate right from the next index if the left iteration didn't succeed
            for (int i = JDKs.getCurIndex() + 1; i < JDKs.jdkSearchRange.length; i++) {
                compileResult = tryCompileWithJDK(compileEnv, JDKs.jdkSearchRange[i]);
                if (compileResult.getState() == CompileResult.CompileState.SUCCESS) {
                    JDKs.setCurIndex(i);
                    return compileResult;
                }
            }

            return compileResult;
        }

        private CompileResult tryCompileWithJDK(CompileTestEnv compileEnv, JDK jdk) {
            logger.info("Trying to compile with JDK " + jdk.name());
            compileEnv.getCtCommand().takeCommand(CtCommands.CommandKey.JDK, jdk.getCommand());
            CompileResult result = OriginCompileFixWay.recompileProject(compileEnv);
            if (result.getState() == CompileResult.CompileState.SUCCESS) {
                compileEnv.setJdk(jdk);
            } else {
                compileEnv.getCtCommand().remove(CtCommands.CommandKey.JDK);
            }
            return result;
        }

    },
    POM_FIX {
        @Override
        CompileResult fix(CompileTestEnv compileEnv) {
            // Implement logic for POM_FIX
            // Example: Modify the pom.xml file, handle exceptions, and recompile
            return recompileProject(compileEnv);
        }
    },
    DEPENDENCY_FIX {
        @Override
        CompileResult fix(CompileTestEnv compileEnv) {
            // Implement logic for DEPENDENCY_FIX
            // Example: Resolve dependency issues, handle exceptions, and recompile
            return recompileProject(compileEnv);
        }
    };

    protected Logger logger = LogManager.getLogManager().getLogger(this.name());

    private static CompileResult recompileProject(CompileTestEnv compileEnv) {
        String message = new Executor(compileEnv.getOsName())
                .setDirectory(compileEnv.getProjectDir())
                .exec(compileEnv.getCtCommand().compute())
                .getMessage();

        return new CompileResult(CtReferees.JudgeCompileState(message), compileEnv.getCtCommand(), compileEnv);
    }

    abstract CompileResult fix(CompileTestEnv compileEnv);
}


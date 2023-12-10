package org.regminer.ct.api;

import org.regminer.common.exec.Executor;
import org.regminer.ct.CtReferees;
import org.regminer.ct.model.CompileResult;
import org.regminer.ct.model.CompileTestEnv;

import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * @Author: sxz
 * @Date: 2023/12/10/16:44
 * @Description:
 */
public enum MigrationCompileFixWay {
    APPLY_REFACTORING {
        @Override
        CompileResult fix(CompileTestEnv compileEnv) {
           //TODO zhangjian apply refactor in bfc to bfc-1
            return recompileProject(compileEnv);
        }
    };

    protected Logger logger = LogManager.getLogManager().getLogger(this.name());

    private static CompileResult recompileProject(CompileTestEnv compileEnv) {
        String message = new Executor()
                .setDirectory(compileEnv.getProjectDir())
                .exec(compileEnv.getCtCommand().compute())
                .getMessage();

        return new CompileResult(CtReferees.JudgeCompileState(message), compileEnv.getCtCommand(), compileEnv);
    }

    abstract CompileResult fix(CompileTestEnv compileEnv);
}

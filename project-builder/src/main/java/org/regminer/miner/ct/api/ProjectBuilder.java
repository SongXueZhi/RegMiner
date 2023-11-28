package org.regminer.miner.ct.api;

import org.regminer.miner.ct.core.AbstractProjectBuilder;
import org.regminer.miner.ct.model.CompileResult;
import org.regminer.miner.ct.model.TestResult;

/**
 * @Author: sxz
 * @Date: 2022/06/09/00:11
 * @Description:
 */
public class ProjectBuilder extends AbstractProjectBuilder {

    /**
     * Get project structure graph to handle multi-modules project
     * @enhance feature
     */
    public void getProjectGraph(){

    }

    @Override
    public CompileResult compile(){
        return null;
    }

    @Override
    public TestResult test() {
        return null;
    }

}

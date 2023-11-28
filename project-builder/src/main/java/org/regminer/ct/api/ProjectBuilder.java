package org.regminer.ct.api;

import org.regminer.ct.core.AbstractProjectBuilder;
import org.regminer.ct.model.CompileResult;
import org.regminer.ct.model.TestResult;

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

package org.regminer.ct.core;

import org.regminer.ct.model.TestResult;
import org.regminer.ct.model.CompileResult;

/**
 * @Author: sxz
 * @Date: 2022/06/08/23:24
 * @Description:
 */
public abstract class AbstractProjectBuilder {
    public abstract CompileResult compile();
   public abstract TestResult test();
}

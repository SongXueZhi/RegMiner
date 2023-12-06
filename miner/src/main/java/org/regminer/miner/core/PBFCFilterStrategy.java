package org.regminer.miner.core;

import org.regminer.common.model.PotentialBFC;

import java.util.List;
import java.util.logging.Logger;

/**
 * @Author: sxz
 * @Date: 2023/12/03/16:08
 * @Description:
 */
public abstract class PBFCFilterStrategy {

    protected Logger logger = Logger.getLogger(PBFCFilterStrategy.class.getName());
    public abstract List<PotentialBFC> filter() throws Exception;
}


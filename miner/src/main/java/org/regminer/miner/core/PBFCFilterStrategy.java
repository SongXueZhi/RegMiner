package org.regminer.miner.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.regminer.commons.model.PotentialBFC;

import java.util.List;


/**
 * @Author: sxz
 * @Date: 2023/12/03/16:08
 * @Description:
 */
public abstract class PBFCFilterStrategy {

    protected Logger logger = LogManager.getLogger(this);
    public abstract List<PotentialBFC> filter() throws Exception;
}


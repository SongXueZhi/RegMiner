package org.regminer.miner.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.regminer.commons.model.PotentialBFC;

import java.util.List;

/**
 * @Author: sxz
 * @Date: 2023/12/03/16:04
 * @Description:
 */
public abstract class BFCSearchStrategy {
    protected Logger logger = LogManager.getLogger(this);
    public abstract void searchRealBFC(List<PotentialBFC> potentialBFCs);//evaluate list

    public abstract boolean confirmPBFCtoBFC(PotentialBFC potentialBFC);//evaluate single
}

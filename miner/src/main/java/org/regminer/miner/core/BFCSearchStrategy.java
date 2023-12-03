package org.regminer.miner.core;

import org.regminer.common.model.PotentialBFC;

import java.util.List;

/**
 * @Author: sxz
 * @Date: 2023/12/03/16:04
 * @Description:
 */
public abstract class BFCSearchStrategy {

    public abstract void searchRealBFC(List<PotentialBFC> potentialBFCs);
}

package org.regminer.miner.core;

import org.regminer.common.model.PotentialBFC;

import java.util.List;

/**
 * @Author: sxz
 * @Date: 2023/12/03/16:08
 * @Description:
 */
public abstract class PBFCFilterStrategy {

    public abstract List<PotentialBFC> filter() throws Exception;
}


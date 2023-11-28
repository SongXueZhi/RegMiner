package org.regminer.miner.bic.api.core;

/**
 * @Author: sxz
 * @Date: 2022/06/08/23:19
 * @Description:
 */
public abstract class AbstractBICSearcher {
    public abstract String[] getSearchSpace();

    public abstract String search();
}

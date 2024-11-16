package org.regminer.bic.api;

import org.apache.commons.lang3.tuple.Triple;
import org.regminer.bic.api.core.BICSearchStrategy;
import org.regminer.commons.model.PotentialBFC;

/**
 * @Author: sxz
 * @Date: 2023/12/01/15:01
 * @Description:
 */
public class SearchBICContext {
    private final BICSearchStrategy bicSearchStrategy;

    public SearchBICContext(BICSearchStrategy strategy) {
        this.bicSearchStrategy = strategy;
    }

    public Triple<String, String, Integer> search(PotentialBFC potentialBFC) {
        return bicSearchStrategy.search(potentialBFC);
    }
}

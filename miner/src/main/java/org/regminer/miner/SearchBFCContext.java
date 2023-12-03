package org.regminer.miner;

import org.regminer.common.model.PotentialBFC;
import org.regminer.miner.core.BFCSearchStrategy;
import org.regminer.miner.core.PBFCFilterStrategy;

import java.util.List;

/**
 * @Author: sxz
 * @Date: 2023/12/03/16:01
 * @Description:
 */
public class SearchBFCContext {
    private BFCSearchStrategy bfcSearchStrategy;
    private PBFCFilterStrategy pbfcFilterStrategy;

    public SearchBFCContext(BFCSearchStrategy bfcSearchStrategy, PBFCFilterStrategy pbfcFilterStrategy) {
        this.bfcSearchStrategy = bfcSearchStrategy;
        this.pbfcFilterStrategy = pbfcFilterStrategy;
    }

    public List<PotentialBFC> searchBFC() throws Exception {
        List<PotentialBFC> potentialBFCS = pbfcFilterStrategy.filter();
        bfcSearchStrategy.searchRealBFC(potentialBFCS);
        return potentialBFCS;
    }

}

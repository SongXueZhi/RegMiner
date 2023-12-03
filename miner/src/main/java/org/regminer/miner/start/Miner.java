package org.regminer.miner.start;

import org.apache.commons.lang3.tuple.Triple;
import org.regminer.bic.api.SearchBICContext;
import org.regminer.common.constant.Configurations;
import org.regminer.common.constant.Constant;
import org.regminer.common.model.PotentialBFC;
import org.regminer.miner.SearchBFCContext;
import org.slf4j.Logger;

import java.util.List;

/**
 * @Author: sxz
 * @Date: 2023/11/28/15:39
 * @Description:
 */
public class Miner {

    protected Logger logger = org.slf4j.LoggerFactory.getLogger(Miner.class);
    private SearchBFCContext bfcContext;
    private SearchBICContext bicContext;

    public Miner(SearchBFCContext bfcEvaluator, SearchBICContext bicFinder) {
        this.bfcContext = bfcEvaluator;
        this.bicContext = bicFinder;
    }

    public void start() {
        logger.info("Start mining...");
        try {
            List<PotentialBFC> pBFCs = bfcContext.searchBFC();
            logger.info("find {} potential BFCs", pBFCs.size());
            if (Configurations.TASK_NAME.equals(Constant.BFC_BIC_TASK)) {
                logger.info("start to search bic");
                for (PotentialBFC pBFC : pBFCs) {
                    Triple<String, String, Integer> bic = bicContext.search(pBFC);
                    logger.info("find bic: {} {} {}", bic.getLeft(), bic.getMiddle(), bic.getRight());
                }
            }
        } catch (Exception exception) {
            logger.error(exception.getMessage());
        }
    }
}

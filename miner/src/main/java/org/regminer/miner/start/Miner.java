package org.regminer.miner.start;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.regminer.bic.api.SearchBICContext;
import org.regminer.common.constant.Configurations;
import org.regminer.common.constant.Constant;
import org.regminer.common.model.PotentialBFC;
import org.regminer.common.tool.SycFileCleanup;
import org.regminer.miner.SearchBFCContext;

import java.io.File;
import java.util.List;

/**
 * @Author: sxz
 * @Date: 2023/11/28/15:39
 * @Description:
 */
public class Miner {

    protected Logger logger = LogManager.getLogger(Miner.class);
    private SearchBFCContext bfcContext;
    private SearchBICContext bicContext;

    public Miner(SearchBFCContext bfcEvaluator, SearchBICContext bicFinder) {
        this.bfcContext = bfcEvaluator;
        this.bicContext = bicFinder;
    }

    public void start() {
        logger.info("Start {} task on {}...", Configurations.taskName, Configurations.projectName);
        Thread.currentThread().setName(Configurations.projectName);
        try {
            List<PotentialBFC> pBFCs = bfcContext.searchBFC();
            logger.info("find {} BFCs", pBFCs.size());
            if (Configurations.taskName.equals(Constant.BFC_BIC_TASK)) {
                logger.info("start to search bic");
                for (PotentialBFC pBFC : pBFCs) {
                    Triple<String, String, Integer> bic = bicContext.search(pBFC);
                    logger.info("find bic: working {}, bic {} {}", bic.getLeft(), bic.getMiddle(), bic.getRight());
                }
            }
        } catch (Exception exception) {
            logger.error(exception.getMessage());
        } finally {
            File file = new File(Configurations.cachePath, Configurations.projectName);
            new SycFileCleanup().cleanDirectory(file);
        }
    }
}

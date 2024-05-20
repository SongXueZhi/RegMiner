package org.regminer.miner.start;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.regminer.bic.api.SearchBICContext;
import org.regminer.commons.constant.Configurations;
import org.regminer.commons.constant.Constant;
import org.regminer.commons.model.PotentialBFC;
import org.regminer.commons.model.Regression;
import org.regminer.commons.sql.BugStorage;
import org.regminer.commons.tool.SycFileCleanup;
import org.regminer.miner.SearchBFCContext;
import org.regminer.miner.monitor.ProgressMonitor;

import java.io.File;
import java.util.List;

/**
 * @Author: sxz
 * @Date: 2023/11/28/15:39
 * @Description:
 */
public class Miner {

    protected Logger logger = LogManager.getLogger(Miner.class);
    private final SearchBFCContext bfcContext;
    private final SearchBICContext bicContext;

    private final BugStorage bugStorage =  new BugStorage();

    public Miner(SearchBFCContext bfcEvaluator, SearchBICContext bicFinder) {
        this.bfcContext = bfcEvaluator;
        this.bicContext = bicFinder;
    }

    public void start() {
        logger.info("Start {} task on {}...", Configurations.taskName, Configurations.projectName);
        Thread.currentThread().setName(Configurations.projectName);
        try {
            List<PotentialBFC> pBFCs = bfcContext.searchPotentialBFC();
            boolean isBFC =false;
            int bfcNum = 0;
            for (PotentialBFC pBFC : pBFCs) {
                isBFC = bfcContext.confirmPBFCtoBFC(pBFC);
                if (isBFC){
                    bfcNum++;
                    if (Configurations.taskName.equals(Constant.BFC_BIC_TASK)) {
                        logger.info("start to search bic");
                        searchBIC(pBFC);
                    }
                }
            }
            logger.info("total bfc: {}, find bfc: {}", pBFCs.size(), bfcNum);
        } catch (Exception exception) {
            logger.error(exception.getMessage());
        } finally {
            File file = new File(Configurations.cachePath, Configurations.projectName);
            new SycFileCleanup().cleanDirectory(file);
        }
    }

    private void searchBIC(PotentialBFC pBFC) {
        Triple<String, String, Integer> bic = null;
        try {
            bic = bicContext.search(pBFC);
            logger.info("find bic: working {}, bic {} {}", bic.getLeft(), bic.getMiddle(), bic.getRight());
        } catch (Exception e) {
            logger.info("find bic failed, bfc: {}, msg: {}", pBFC.getCommit().getName(), e.getMessage());
        }
        try {
            if (bic != null){
                bugStorage.saveRegression(enCapRegression(pBFC, bic));
            }
            ProgressMonitor.updateState(pBFC.getCommit().getName());
        }catch (Exception e){
            logger.error("save regression failed, bfc: {}, msg: {}", pBFC.getCommit(), e.getMessage());
        }
    }

    private Regression enCapRegression(PotentialBFC pbfc, Triple<String, String, Integer> bic){
        return new Regression(pbfc.getCommit().getName(),pbfc.getBuggyCommitId(),bic.getMiddle(),
                bic.getLeft(), pbfc.joinTestcaseString(),bic.getRight());

    }
}

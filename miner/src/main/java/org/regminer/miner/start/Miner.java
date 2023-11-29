package org.regminer.miner.start;

import org.regminer.miner.PotentialBFCDetector;
import org.regminer.miner.migrate.BFCEvaluator;
import org.regminer.miner.migrate.BICFinder;
import org.regminer.common.model.PotentialBFC;
import org.slf4j.Logger;

import java.util.List;

/**
 * @Author: sxz
 * @Date: 2023/11/28/15:39
 * @Description:
 */
public class Miner {

    private BFCEvaluator bfcEvaluator;
    private BICFinder bicFinder;
    private PotentialBFCDetector potentialBFCDetector;
    protected Logger logger = org.slf4j.LoggerFactory.getLogger(Miner.class);
    public Miner(BFCEvaluator bfcEvaluator, BICFinder bicFinder){
        this.bfcEvaluator = bfcEvaluator;
        this.bicFinder = bicFinder;
        potentialBFCDetector = new PotentialBFCDetector();
    }
    public void start(){
        logger.info("Start mining...");
        try {
            List<PotentialBFC> pBFCs  = potentialBFCDetector.detectPotentialBFC();
            bfcEvaluator.evoluteBFCList(pBFCs);
            logger.info("Mining finished.");
            for (PotentialBFC pBFC : pBFCs) {
//                bicFinder.searchBIC(pBFC);
            }
        }catch (Exception exception){

        }
    }
}

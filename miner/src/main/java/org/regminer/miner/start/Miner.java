package org.regminer.miner.start;

import org.apache.commons.lang3.tuple.Triple;
import org.regminer.bic.api.SearchBICContext;
import org.regminer.miner.PotentialBFCDetector;
import org.regminer.miner.BFCEvaluator;
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
    private SearchBICContext bicFinder;
    private PotentialBFCDetector potentialBFCDetector;
    protected Logger logger = org.slf4j.LoggerFactory.getLogger(Miner.class);
    public Miner(BFCEvaluator bfcEvaluator, SearchBICContext bicFinder){
        this.bfcEvaluator = bfcEvaluator;
        this.bicFinder = bicFinder;
        potentialBFCDetector = new PotentialBFCDetector();
    }
    public void start(){
        logger.info("Start mining...");
        try {
            List<PotentialBFC> pBFCs  = potentialBFCDetector.detectPotentialBFC();
            bfcEvaluator.evoluteBFCList(pBFCs);
            logger.info("find {} potential BFCs",pBFCs.size());
            for (PotentialBFC pBFC : pBFCs) {
                Triple<String,String,Integer> bic = bicFinder.search(pBFC);
                logger.info("find bic: {} {} {}",bic.getLeft(),bic.getMiddle(),bic.getRight());
            }
        }catch (Exception exception){

        }
    }
}

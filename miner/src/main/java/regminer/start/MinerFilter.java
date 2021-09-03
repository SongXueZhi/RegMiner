package regminer.start;

import org.eclipse.jgit.api.Git;
import regminer.constant.Conf;
import regminer.git.provider.Provider;
import regminer.miner.PotentialBFCDetector;
import regminer.model.PotentialRFC;
import regminer.utils.FileUtilx;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MinerFilter {
    public void prepare() throws Exception {
        ConfigLoader.refresh();
        Miner.repo = new Provider().create(Provider.EXISITING).get(Conf.LOCAL_PROJECT_GIT);
        Miner.git = new Git(Miner.repo);
    }

    public void handleTask() throws Exception {
        List<String> filter = new ArrayList<>();
        filter.add("f627193ce0950f0d55ed1b4f6a2fe9973447853a");
        PotentialBFCDetector pBFCDetector = new PotentialBFCDetector(Miner.repo, Miner.git);
        Miner.pRFCs = null;
        Miner.pRFCs = (LinkedList<PotentialRFC>) pBFCDetector.detectPotentialBFC(filter);
        Miner.singleThreadHandle();
    }
    public static void main(String[] args) throws Exception {
        MinerFilter minerFilter = new  MinerFilter();
        minerFilter.prepare();
        minerFilter.handleTask();
    }
}


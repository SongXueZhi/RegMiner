package regminer.start;

import org.eclipse.jgit.api.Git;
import regminer.constant.Conf;
import regminer.git.provider.Provider;
import regminer.miner.PotentialBFCDetector;
import regminer.model.PotentialRFC;

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
        filter.add("4bc0c553e40ce1e47d8d2a6a43df5c5923898f8c");
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


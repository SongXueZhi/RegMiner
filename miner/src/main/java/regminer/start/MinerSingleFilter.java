package regminer.start;

import org.eclipse.jgit.api.Git;
import regminer.constant.Conf;
import regminer.git.provider.Provider;
import regminer.miner.PotentialBFCDetector;
import regminer.model.PotentialRFC;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MinerSingleFilter {
    public static void main(String[] args) throws Exception {
        long s1 = System.currentTimeMillis();
        MinerSingleFilter minerFilter = new MinerSingleFilter();
        minerFilter.prepare();
        minerFilter.handleSingleTask();
        long s2 = System.currentTimeMillis();
        System.out.println(s2 - s1);
    }

    public void prepare() throws Exception {
        ConfigLoader.refresh();
        Miner.repo = new Provider().create(Provider.EXISITING).get(Conf.LOCAL_PROJECT_GIT);
        Miner.git = new Git(Miner.repo);
    }

    public void handleSingleTask() throws Exception {
        List<String> filter = new ArrayList<>();
        filter.add("e5210d1f9ef4f1d41ff0a8c4a2ab8e9192d5e087");
        PotentialBFCDetector pBFCDetector = new PotentialBFCDetector(Miner.repo, Miner.git);
        Miner.pRFCs = null;
        Miner.pRFCs = (LinkedList<PotentialRFC>) pBFCDetector.detectPotentialBFC(filter);
        Miner.singleThreadHandle();
    }
}


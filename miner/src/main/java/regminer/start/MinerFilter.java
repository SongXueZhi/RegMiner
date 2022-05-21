package regminer.start;

import org.eclipse.jgit.api.Git;
import regminer.constant.Conf;
import regminer.git.provider.Provider;
import regminer.miner.PotentialBFCDetector;
import regminer.model.PotentialRFC;
import regminer.utils.FileUtilx;

import java.io.File;
import java.util.*;

public class MinerFilter {
    public void prepare() throws Exception {
        ConfigLoader.refresh();
        Miner.repo = new Provider().create(Provider.EXISITING).get(Conf.LOCAL_PROJECT_GIT);
        Miner.git = new Git(Miner.repo);
    }

    public void handleTask() throws Exception {
        List<String> filter = new ArrayList<>(FileUtilx.readSetFromFile(Conf.PROJECT_PATH+ File.separator+"bfc.txt"));
        PotentialBFCDetector pBFCDetector = new PotentialBFCDetector(Miner.repo, Miner.git);
        List<String> meta =  FileUtilx.readListFromFile(Conf.ROOT_DIR+File.separator+"regression_bfc.csv");
        Map<String,String> map = new HashMap<>();
        for (String s : meta){
            String[] ss = s.split(",");
            map.put(ss[0],ss[1]);
        }
        Conf.metaMap = map;
        Miner.pRFCs = null;
        Miner.pRFCs = (LinkedList<PotentialRFC>) pBFCDetector.detectPotentialBFC(filter);
        Miner.singleThreadHandle();
    }

    public static void main(String[] args) throws Exception {
        long s1 =System.currentTimeMillis();
        MinerFilter minerFilter = new  MinerFilter();
        minerFilter.prepare();
        minerFilter.handleTask();
        long s2 = System.currentTimeMillis();
        System.out.println(s2-s1);
    }
}


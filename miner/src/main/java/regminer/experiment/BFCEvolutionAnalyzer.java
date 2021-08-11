package regminer.experiment;


import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import regminer.constant.Conf;
import regminer.git.provider.Provider;
import regminer.miner.PotentialBFCDetector;
import regminer.miner.migrate.BFCEvaluator;
import regminer.model.PotentialRFC;
import regminer.start.ConfigLoader;
import regminer.utils.FileUtilx;

import java.util.ArrayList;
import java.util.List;

public class BFCEvolutionAnalyzer {

    String dataFilePath = "resource/fastjson_rfc";
    Repository repo;
    Git git;

    public void prepare() throws Exception {
        ConfigLoader.refresh();
        repo = new Provider().create(Provider.EXISITING).get(Conf.LOCAL_PROJECT_GIT);
        git = new Git(repo);
    }

    public void handleTask(List<String> filter) throws Exception {
        PotentialBFCDetector pBFCDetector = new PotentialBFCDetector(repo, git);
        List<PotentialRFC> potentialRFCList = pBFCDetector.detectPotentialBFC(filter);
        BFCEvaluator bfcEvaluator = new BFCEvaluator(repo);
        bfcEvaluator.evoluteBFCList(potentialRFCList);
    }

    public List<String> readProjectCommitTOHandle() {
        return new ArrayList<>(FileUtilx.readSetFromFile(dataFilePath));
    }

    public static void main(String[] args) throws Exception {
        BFCEvolutionAnalyzer analyzer = new BFCEvolutionAnalyzer();
        analyzer.prepare();
        analyzer.handleTask(analyzer.readProjectCommitTOHandle());
    }

}

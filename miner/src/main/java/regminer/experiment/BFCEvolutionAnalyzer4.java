package regminer.experiment;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.lib.Repository;
import regminer.constant.Conf;
import regminer.finalize.SycFileCleanup;
import regminer.git.GitTracker;
import regminer.git.provider.Provider;
import regminer.miner.PotentialBFCDetector;
import regminer.miner.migrate.Migrator;
import regminer.model.NormalFile;
import regminer.model.PotentialRFC;
import regminer.start.ConfigLoader;
import regminer.utils.FileUtilx;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BFCEvolutionAnalyzer4 extends Migrator {
    private final static double PROB_UNIT = 0.05;
    private double notRegProb = 1 - PROB_UNIT;
    private String index = "fastjson_bfc1";
    String dataFilePath = "resource"+File.separator+index;
    Repository repo;
    Git git;
    GitTracker gitTracker = new GitTracker();
    File log = new File(Conf.PROJECT_PATH + File.separator + index+".csv");

    public void prepare() throws Exception {
        ConfigLoader.refresh();
        repo = new Provider().create(Provider.EXISITING).get(Conf.LOCAL_PROJECT_GIT);
        git = new Git(repo);
    }

    public void handleTask(List<String> filter) throws Exception {
        PotentialBFCDetector pBFCDetector = new PotentialBFCDetector(repo, git);
        List<PotentialRFC> potentialRFCList = pBFCDetector.detectPotentialBFC(filter);
        for (PotentialRFC pRFC : potentialRFCList) {
            String commitID = pRFC.getCommit().getName();
            File bfcDir = checkout(commitID, commitID, "bfc");
            pRFC.fileMap.put(commitID, bfcDir);
            double score = regressionProbCalculate(trackCodeBlocks(pRFC.getNormalJavaFiles(),bfcDir));
            emptyCache(commitID);
            FileUtilx.apendResultToFile(pRFC.getCommit().getName() + "," + score, log);
        }
    }

    public double regressionProbCalculate(int sum) {
        double methodNotRegressionProb = Math.pow(notRegProb, sum);
        return 1 - methodNotRegressionProb;
    }

    public int trackCodeBlocks(List<NormalFile> normalFiles, File bfcDir) {
        int sum = 0;
        for (NormalFile normalFile : normalFiles) {
            for (Edit edit:normalFile.getEditList()) {
                sum += gitTracker.trackhunkByLogl(edit.getBeginB()+1, edit.getEndB(), normalFile.getNewPath(), bfcDir);
            }
        }
        return sum;
    }

    public void emptyCache(String bfcID) {
        File bfcFile = new File(Conf.CACHE_PATH + File.separator + bfcID);
        new SycFileCleanup().cleanDirectory(bfcFile);
    }

    public List<String> readProjectCommitTOHandle() {
        return new ArrayList<>(FileUtilx.readSetFromFile(dataFilePath));
    }

    public static void main(String[] args) throws Exception {
        BFCEvolutionAnalyzer4 analyzer2 = new BFCEvolutionAnalyzer4();
        analyzer2.prepare();
        analyzer2.handleTask(analyzer2.readProjectCommitTOHandle());
    }
}

package regminer.regressiontest;

import constant.Conf;
import git.provider.Provider;
import miner.PotentialBFCDetector;
import miner.migrate.Migrater;
import model.PotentialRFC;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.evosuite.EvoSuite;
import org.evosuite.Properties;
import org.evosuite.result.TestGenerationResult;
import org.junit.Before;
import org.junit.Test;
import start.ConfigLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BFCTestGeneration extends Migrater {

    @Before
    public void setUp() {
        ConfigLoader.refresh();
    }

    /**
     * input: commit ID, output: the generated test cases for the potential method
     */
    @Test
    public void testBFCTestGeneration() {
        /**
         * input
         */
        String commitID = "341fc39f1b341ff1574e83c6cf51536cce758fcf";

        try {
            // The  pRFC is the protagonist of this approach
            // pRFC is must be bfc, but bfc may not be RFC
            PotentialRFC pRFC = parseCommit(commitID);
            //TODO 1. checkout project version 1,
            RevCommit bfcCommit = pRFC.getCommit();
            String bfcName = bfcCommit.getName();
            // When we checkout, we need to specify the uniqueness of the file
            // The general structure of the directory is as follows:
            //- the project name
            //  -cache
            //      -bfcid
            //          -target commit
            //              -version_uuid
            // So you can see that intuitively,
            // we copy the original code(in meta directory) into the cache directory
            // and uniquely identify it with bfcid, then git checkout to the specified commimt.
            // So for a specific project we need this structure
            // -project name
            //      - meta （source code here）
            // After checkout:
            // -project name
            //      - cache
            //      - meta （source code here）
            File bfcDir = checkout(bfcName, bfcName, "bfc");
            //preBFC ---> bfcP,i.e, bfc parent
            File bfcpDir = checkout(bfcName, bfcCommit.getParent(0).getName(), "bfcp");

            //TODO 2. target method
            String method = retrieveTargetMethod(pRFC, bfcDir);

            //TODO 3. generate test cases
            String testcase = generateTests(method, bfcDir);

            //TODO 4. compare
            move(testcase, bfcpDir);
            run(testcase, bfcpDir);

            System.currentTimeMillis();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testTestGeneration() {
        String targetClass = "com.alibaba.fastjson.JSON";
        String targetMethod = "parseArray(Ljava/lang/String;Lcom/alibaba/fastjson/parser/ParserConfig;)Lcom/alibaba/fastjson/JSONArray;";
        String projectRoot = "D:\\linyun\\git_space\\reg\\subject-repo\\fastjason\\project\\";

        //TODO we may still need to parse maven class path
        String cp = projectRoot + File.separator + "target/classes" + File.pathSeparator +
                projectRoot + File.separator + "target/test-classes";

        evoTestSmartSeedMethod(targetClass,
                targetMethod, cp, 10,
                "generateMOSuite", "MOSUITE", "MOSA");
    }

    public static void evoTestSmartSeedMethod(String targetClass, String targetMethod, String cp,
                                              long seconds,
                                              String option,
                                              String strategy,
                                              String algorithm) {
        /* configure */
        EvoSuite evo = new EvoSuite();
        Properties.TARGET_CLASS = targetClass;
        Properties.TRACK_COVERED_GRADIENT_BRANCHES = true;
        String[] args = new String[]{
                "-" + option,
                "-Dstrategy", strategy,
                "-Dalgorithm", algorithm,
//				"-Dcriterion", "branch",
                "-class", targetClass,
                "-projectCP", cp,
                "-Dtarget_method", targetMethod,
                "-Dsearch_budget", String.valueOf(seconds),
                "-Dmax_attempts", "100",
                "-Dassertions", "true",
        };

        List<List<TestGenerationResult>> list = (List<List<TestGenerationResult>>) evo.parseCommandLine(args);
    }

    private String generateTests(String method, File f1) {
        // TODO Auto-generated method stub
        return null;
    }

    private void run(String testcase, File f2) {
        // TODO Auto-generated method stub

    }

    private void move(String testcase, File f2) {
        // TODO Auto-generated method stub

    }

    private String retrieveTargetMethod(PotentialRFC rfc, File f1) {
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("resource")
    private PotentialRFC parseCommit(String commitID) throws Exception {
        ConfigLoader.refresh();// 加载配置
        Repository repo = new Provider().create(Provider.EXISITING).get(Conf.LOCAL_PROJECT_GIT);
        Git git = new Git(repo);

        Iterable<RevCommit> commits = git.log().all().call();
        // 开始迭代每一个commit
        boolean a = true;
        for (RevCommit commit : commits) {
            ObjectId id = commit.getId();

            if (id.toString().contains(commitID)) {

                PotentialBFCDetector detector = new PotentialBFCDetector(repo, git);

                List<PotentialRFC> potentialRFCs = new ArrayList<>();
                detector.detect(commit, potentialRFCs);

                return potentialRFCs.get(0);
            }
        }

        return null;
    }

}

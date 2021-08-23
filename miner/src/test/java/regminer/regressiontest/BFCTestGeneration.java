//package regminer.regressiontest;
//
//import org.eclipse.jgit.api.Git;
//import org.eclipse.jgit.lib.Repository;
//import org.eclipse.jgit.revwalk.RevCommit;
//import org.evosuite.EvoSuite;
//import org.evosuite.Properties;
//import org.evosuite.result.TestGenerationResult;
//import org.junit.Before;
//import org.junit.Test;
//import regminer.constant.Conf;
//import regminer.git.provider.Provider;
//import regminer.maven.MavenManager;
//import regminer.miner.PotentialBFCDetector;
//import regminer.miner.migrate.Migrator;
//import regminer.model.PotentialRFC;
//import regminer.start.ConfigLoader;
//import regminer.start.Miner;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//public class BFCTestGeneration extends Migrator {
//
//    @Before
//    public void setUp() {
//        ConfigLoader.refresh();
//    }
//
//    /**
//     * input: commit ID, output: the generated test cases for the potential method
//     */
//    @Test
//    public void testBFCTestGeneration() {
//        /**
//         * input
//         */
//        String commitID = "341fc39f1b341ff1574e83c6cf51536cce758fcf";
//
//        try {
//            // The  pRFC is the protagonist of this approach
//            // pRFC is must be bfc, but bfc may not be RFC
//            PotentialRFC pRFC = parseCommit(commitID);
//            //TODO 1. checkout project version 1,
//            RevCommit bfcCommit = pRFC.getCommit();
//            String bfcName = bfcCommit.getName();
//            // When we checkout, we need to specify the uniqueness of the file
//            // The general structure of the directory is as follows:
//            //- the project name
//            //  -cache
//            //      -bfcid
//            //          -target commit
//            //              -version_uuid
//            // So you can see that intuitively,
//            // we copy the original code(in meta directory) into the cache directory
//            // and uniquely identify it with bfcid, then regminer.git checkout to the specified commimt.
//            // So for a specific project we need this structure
//            // -project name
//            //      - meta （source code here）
//            // After checkout:
//            // -project name
//            //      - cache
//            //      - meta （source code here）
//            File bfcDir = checkout(bfcName, bfcName, "bfc");
//            //preBFC ---> bfcP,i.e, bfc parent
//            File bfcpDir = checkout(bfcName, bfcCommit.getParent(0).getName(), "bfcp");
//
//            //TODO 2. target method
//
//            String method = retrieveTargetMethod(pRFC, bfcDir);
//
//            //TODO 3. generate test cases
//            String testcase = generateTests(method, bfcDir);
//
//            //TODO 4. compare
//            move(testcase, bfcpDir);
//            run(testcase, bfcpDir);
//
//            System.currentTimeMillis();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @Test
//    public void testTestGeneration() throws Exception {
//        String targetClass = "org.jsoup.Jsoup";
//        String targetMethod = "(Ljava/net/URL;I)Lorg/jsoup/nodes/Document;";
//        String projectRoot = "/home/sxz/Desktop/jsoup";
//
//        //TODO we may still need to parse maven class path
//        //FIXME sxz
//        MavenManager mvnManager = new MavenManager();
//        List<String> mvnClassPathList = mvnManager.readAllDependency(new File(Conf.META_PATH + File.separator + "pom.xml"));
//
//        String cp = projectRoot + File.separator + "target/classes" + File.pathSeparator +
//                projectRoot + File.separator + "target/test-classes";
//
//        evoTestSmartSeedMethod(targetClass,
//                targetMethod, cp, 10,
//                "generateMOSuite", "MOSUITE", "MOSA");
//    }
//
//    public static void evoTestSmartSeedMethod(String targetClass, String targetMethod, String cp,
//                                              long seconds,
//                                              String option,
//                                              String strategy,
//                                              String algorithm) {
//        /* configure */
//        EvoSuite evo = new EvoSuite();
//        Properties.TARGET_CLASS = targetClass;
//        Properties.TRACK_COVERED_GRADIENT_BRANCHES = true;
//        String[] args = new String[]{
//                "-" + option,
//                "-Dstrategy", strategy,
//                "-Dalgorithm", algorithm,
////				"-Dcriterion", "branch",
//                "-class", targetClass,
//                "-projectCP", cp,
//                "-Dtarget_method", targetMethod,
//                "-Dsearch_budget", String.valueOf(seconds),
//                "-Dmax_attempts", "100",
//                "-Dassertions", "true",
//        };
//
//        List<List<TestGenerationResult>> list = (List<List<TestGenerationResult>>) evo.parseCommandLine(args);
//    }
//
//    private String generateTests(String method, File f1) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    private void run(String testcase, File f2) {
//        // TODO Auto-generated method stub
//
//    }
//
//    private void move(String testcase, File f2) {
//        // TODO Auto-generated method stub
//
//    }
//
//    private String retrieveTargetMethod(PotentialRFC rfc, File f1) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @SuppressWarnings("resource")
//    private PotentialRFC parseCommit(String commitID) throws Exception {
//        List<String> filter = new ArrayList<>();
//        filter.add(commitID);
//        Repository repo = new Provider().create(Provider.EXISITING).get(Conf.LOCAL_PROJECT_GIT);
//        Git git = new Git(Miner.repo);
//        PotentialBFCDetector pBFCDetector = new PotentialBFCDetector(repo, git);
//        List<PotentialRFC> potentialRFCS = pBFCDetector.detectPotentialBFC(filter);
//        return potentialRFCS == null ? null : potentialRFCS.get(0);
//    }
//
//}

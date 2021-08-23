package regminer.experiment;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.zhixiangli.code.similarity.CodeSimilarity;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import regminer.callgraph.CalleeResolver;
import regminer.callgraph.MethodRetriever;
import regminer.callgraph.model.CallRoot;
import regminer.constant.Conf;
import regminer.exec.TestExecutor;
import regminer.finalize.SycFileCleanup;
import regminer.git.GitTracker;
import regminer.git.provider.Provider;
import regminer.miner.PotentialBFCDetector;
import regminer.miner.migrate.Migrator;
import regminer.model.PotentialRFC;
import regminer.start.ConfigLoader;
import regminer.utils.FileUtilx;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class BFCEvolutionAnalyzer6 extends Migrator {
    private final static double PROB_UNIT = 0.05;
    Repository repo;
    Git git;
    GitTracker gitTracker = new GitTracker();
    TestExecutor exec = new TestExecutor();
    CalleeResolver calleeResolver = new CalleeResolver(1);
    CommitParser commitParser;
    MethodRetriever methodRetriever = new MethodRetriever();
    private double notRegProb = 1 - PROB_UNIT;
    private String index = "fastjson_bfc";
    String dataFilePath = "resource" + File.separator + index;
    File log = new File(Conf.PROJECT_PATH + File.separator + index + ".csv");

    public static void main(String[] args) throws Exception {
        BFCEvolutionAnalyzer6 analyzer2 = new BFCEvolutionAnalyzer6();
        analyzer2.prepare();
        analyzer2.handleTask(analyzer2.readProjectCommitTOHandle());
    }

    public void prepare() throws Exception {
        ConfigLoader.refresh();
        repo = new Provider().create(Provider.EXISITING).get(Conf.LOCAL_PROJECT_GIT);
        git = new Git(repo);
        commitParser = new CommitParser(repo, git);
    }

    public void handleTask(List<String> filter) throws Exception {
        PotentialBFCDetector pBFCDetector = new PotentialBFCDetector(repo, git);
        List<PotentialRFC> potentialRFCList = pBFCDetector.detectPotentialBFC(filter);
        for (PotentialRFC pRFC : potentialRFCList) {
                String bfcId = pRFC.getCommit().getName();
                File bfcdir = checkout(bfcId,bfcId,"bfc");
                pRFC.fileMap.put(bfcId,bfcdir);
              int n = trackRelatedCommits(pRFC);
              double score = regressionProbCalculate(n);
            System.out.println(pRFC.getCommit().getName() + "," + score);
            FileUtilx.apendResultToFile(pRFC.getCommit().getName() + "," + score, log);
        }
    }
    public double regressionProbCalculate(int sum) {
        double methodNotRegressionProb = Math.pow(notRegProb, sum);
        return 1 - methodNotRegressionProb;
    }
    public boolean match(Map<String, MethodDeclaration> fDeclarationMap, File dir,Map<String, MethodDeclaration> tMethodDeclarationMap) throws FileNotFoundException {
        Map<String, CompilationUnit> fileCompilationMap = new HashMap<>();

        List<MethodDeclaration> ftMethodList = new ArrayList<>();
        for (Map.Entry<String, MethodDeclaration> entry : fDeclarationMap.entrySet()) {
            String path = entry.getKey().split("_")[0];
            MethodDeclaration fMethod = entry.getValue();

            File file = new File(dir, path);
            if(!file.exists()){
                continue;
            }

            CompilationUnit unit = null;
            //Cache Compilation Or Get
            if (fileCompilationMap.containsKey(path)) {
                unit = fileCompilationMap.get(path);
            } else {
                unit =StaticJavaParser.parse(file);
                fileCompilationMap.put(path, unit);
            }

            List<MethodDeclaration> methodDeclarationList = methodRetriever.getMethodList(unit);
            MethodDeclaration fTMethod = match(fMethod, methodDeclarationList);
            if (fTMethod != null) {
                ftMethodList.add(fTMethod);
            }
        }
        //match nothing return
        if (ftMethodList.size() <= 0) {
            return false;
        }
        //else continue match call graph join set
        List<CallRoot> ftMethodCallRoots = calleeResolver.getAllMethodCallRoot(ftMethodList,dir);
        List<MethodDeclaration>  ftCAllMethods = new ArrayList<>();
        for (CallRoot callRoot:ftMethodCallRoots){
            ftCAllMethods.addAll(callRoot.computeAllMethodDeclaration());
        }

        List<CallRoot> tMethodCallRoots =calleeResolver.getAllMethodCallRoot(tMethodDeclarationMap,dir);
        List<MethodDeclaration>  tCAllMethods = new ArrayList<>();
        for(CallRoot callRoot1:tMethodCallRoots){
            tCAllMethods.addAll(callRoot1.computeAllMethodDeclaration());
        }

        for(MethodDeclaration ftme :ftCAllMethods){
            for (MethodDeclaration tme:tCAllMethods){
                if (ftme.getDeclarationAsString().equals(tme.getDeclarationAsString())){
                    return true;
                }
            }
        }
        return false;
    }

    public MethodDeclaration match(MethodDeclaration fMethod, List<MethodDeclaration> methodDeclarationList) {

        CodeSimilarity codeSimilarity = new CodeSimilarity();
        String fMethodContent = fMethod.toString();
        int startline = fMethod.getBegin().get().line;
        for (MethodDeclaration tMethod : methodDeclarationList) {
            if (fMethod.getDeclarationAsString(true, true, true).equals(
                    tMethod.getDeclarationAsString(true, true, true))) {
                return tMethod;
            }
        }
        HashMap<MethodDeclaration, Double> methodSimilarityMap = new HashMap<>();
        //not match in same declaration string
        //compare similarity rate
        for (MethodDeclaration tMethod : methodDeclarationList) {
            if (tMethod.getName().equals(fMethod.getName())) {
                double score = codeSimilarity.get(tMethod.toString(), fMethodContent);
                methodSimilarityMap.put(tMethod, score);
            }
        }
        // nothing found return null
        if (methodSimilarityMap.size() <= 0) {
            return null;
        }
        // find any method with same simple name, return max
        List<Map.Entry<MethodDeclaration, Double>> methodScores = new ArrayList<Map.Entry<MethodDeclaration, Double>>(methodSimilarityMap.entrySet());
        Collections.sort(methodScores, new Comparator<Map.Entry<MethodDeclaration, Double>>() {
            public int compare(Map.Entry<MethodDeclaration, Double> o1, Map.Entry<MethodDeclaration, Double> o2) {
                return (int) (o2.getValue() - o1.getValue());
            }
        });
        List<MethodDeclaration> resultList = new ArrayList<>();
        double max = methodScores.get(0).getValue();
        for (int i = 1; i < methodScores.size(); i++) {
            if (methodScores.get(i).getValue() == max) {
                resultList.add(methodScores.get(i).getKey());
            }
        }
        if (resultList.size() <= 0) {
            return methodScores.get(0).getKey();
        }
        // if more than one max ,compare location distance
        resultList.add(methodScores.get(0).getKey());
        int minx = 100000000;
        MethodDeclaration minMed = methodScores.get(0).getKey();
        for (MethodDeclaration m : resultList) {
            int s = startline - m.getBegin().get().line;
            s = Math.abs(s);
            if (s < minx) {
                minx = s;
                minMed = m;
            }
        }
        return minMed;
    }

    public List<String> revListCommand(String commitId, File dir) {
        exec.setDirectory(dir);
        return exec.runCommand("git log --pretty=format:%H");
    }

    public int  trackRelatedCommits(PotentialRFC pRFC) throws Exception {
        String bfcID = pRFC.getCommit().getName();
        File bfcDir = pRFC.fileMap.get(bfcID);
        List<String> commitIDs = revListCommand(bfcID, pRFC.fileMap.get(bfcID));
        commitIDs.remove(bfcID);
        Map<String, MethodDeclaration> bfcEditMethodMap = commitParser.handle(pRFC);
        System.out.println("bfc: "+bfcID);
        int sum = 0;
        for (String commitID : commitIDs) {
            System.out.println("c: "+commitID);
            TrackedCommit trackedCommit = commitParser.parseCommit(commitID);
            Map<String, MethodDeclaration> tMethodDeclarationMap = commitParser.handleTrackedCommit(bfcDir, trackedCommit);
            if (tMethodDeclarationMap.size()<=0){
                continue;
            }
            if(match(bfcEditMethodMap,bfcDir,tMethodDeclarationMap)){
                sum++;
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
}

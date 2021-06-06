package regminer.regressiontest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.evosuite.EvoSuite;
import org.evosuite.Properties;
import org.evosuite.result.TestGenerationResult;
import org.junit.Test;

import constant.Conf;
import git.provider.Provider;
import miner.PotentialBFCDetector;
import model.PotentialRFC;
import start.ConfigLoader;

public class BFCTestGeneration {

	@Test
	/**
	 * input: commit ID, output: the generated test cases for the potential method
	 */
	public void testBFCTestGeneration() {
		/**
		 * input
		 */
		String commitID = "341fc39f1b341ff1574e83c6cf51536cce758fcf";

		try {
			PotentialRFC rfc = parseCommit(commitID);
			//FIXME
			PotentialRFC prevRFC = parseCommit(commitID);
			
			//TODO 1. checkout project version 1, 
			File f1 = checkout(rfc);
			
			// git --work-tree=../project checkout 341fc39f1b34 -- .
			File f2 = checkout(prevRFC);
			
			//TODO 2. target method
			String method = retrieveTargetMethod(rfc, f1);
			
			//TODO 3. generate test cases
			String testcase = generateTests(method, f1);
			
			//TODO 4. compare 
			move(testcase, f2);
			run(testcase, f2);
			
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
		String[] args = new String[] {
				"-"+option,
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

	private File checkout(PotentialRFC rfc) {
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
			
			if(id.toString().contains(commitID)) {
				
				PotentialBFCDetector detector = new PotentialBFCDetector(repo, git);
				
				List<PotentialRFC> potentialRFCs = new ArrayList<>();
				detector.detect(commit, potentialRFCs);
				
				return potentialRFCs.get(0);
			}
		}
		
		return null;
	}

}

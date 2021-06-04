package regminer.regressiontest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
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

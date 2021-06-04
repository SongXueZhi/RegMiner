package regminer.regressiontest;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import constant.Conf;
import git.provider.Provider;
import model.PotentialRFC;
import start.ConfigLoader;

public class BFCTestGeneration {

	@Test
	/**
	 * input: commit ID, 
	 * output: the generated test cases for the potential method
	 */
	public void testBFCTestGeneration() {
		/**
		 * input
		 */
		String commitID = "341fc39f1b341ff1574e83c6cf51536cce758fcf";
		
		try {
			PotentialRFC commit = parseCommit(commitID);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@SuppressWarnings("resource")
	private PotentialRFC parseCommit(String commitID) throws Exception {
		ConfigLoader.refresh();//加载配置
		Repository repo = new Provider().create(Provider.EXISITING).get(Conf.LOCAL_PROJECT_GIT);
		Git git = new Git(repo);
		
		// 获取所有的commit，我们需要对所有的commit进行分析
		Iterable<RevCommit> commits = git.log().all().call();
		// 开始迭代每一个commit
		boolean a = true;
		for (RevCommit commit : commits) {
			ObjectId id = commit.getId();
			
			PotentialRFC rfc = new PotentialRFC(commit);
			return rfc;
		}
		
		return null;
		
	}
	
}

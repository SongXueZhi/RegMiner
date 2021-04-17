package pre_compile;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

public class Compiler {

	public static void main(String[] args) throws Exception {
//		RepositoryProviderCloneImpl rProvider = new RepositoryProviderCloneImpl(Conf.CLONE_URL);
//		rProvider.get(Conf.metaPath + File.separator + "fastjson");
		String projectPath = args[0] + File.separator + "/.git";
		String loaclPath = args[1];
		String compileCommand = args[2];
		Repository repo = new Provider().create(Provider.EXISITING).get(projectPath);
		Git git = new Git(repo);
		Iterable<RevCommit> commits = git.log().all().call();
		TestExecutor exec = new TestExecutor();
		int sucess = 0;
		int total = 0;
		File meta = new File(args[0]);
		for (RevCommit commit : commits) {
			total++;
			File target = new File(loaclPath + commit.getName());
			if (target.exists()) {
				continue;
			}
			FileUtils.copyDirectory(meta, target);
			exec.setDirectory(target);
			String name = commit.getName();
			exec.execPrintln("git checkout -f " + name);
			System.out.println(name);
			String res = exec.exec("git rev-parse HEAD");
			if (!res.equals(name)) {
				System.err.println("checkout error " + res);
				return;
			}
			boolean a = exec.execBuildWithResult(compileCommand, false);
			System.out.println(a);
			if (a) {
				sucess++;
			}
		}
		System.out.println("total " + total + " sucess " + sucess);
	}
}

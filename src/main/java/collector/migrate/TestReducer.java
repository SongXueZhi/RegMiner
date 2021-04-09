package collector.migrate;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;

import org.eclipse.jgit.lib.Repository;

import constant.Conf;
import exec.TestExecutor;
import model.ChangedFile.Type;
import model.ExperResult;
import model.MigrateItem.MigrateFailureType;
import model.PotentialRFC;
import model.RelatedTestCase;
import model.TestFile;
import utils.FileUtil;

public class TestReducer {
	int i = 0;
	int j = 0;
	Repository repo;
	TestExecutor exec = new TestExecutor();
	String projectName = Conf.PROJRCT_NAME;

	public TestReducer(Repository repo) {
		this.repo = repo;
	}

	public Set<String> reducer(PotentialRFC pRFC) throws Exception {

		// 1.准备BFC
		String commitId = pRFC.getCommit().getName();
		System.out.println(commitId + "开始执行测试约减");
		File bfcDirectory = checkout(commitId, commitId, "bfc");
		if (pRFC.getCommit().getParentCount() <= 0) {
			emptyCache();
			return null;
		}
		// 2.编译BFC
		if (!comiple(bfcDirectory, false)) {
			System.out.println("BFC构建失败");
			emptyCache();
			return null;
		}
		// 3 测试BFC中的每一个待测试方法
		Set<String> realTestCase = testBFC(bfcDirectory, pRFC);
		if (realTestCase.size() <= 0) {
			System.out.println("BFC 没有测试成功的方法");
			emptyCache();
			return null;
		}
		File bfcpDirectory = checkout(commitId, pRFC.getCommit().getParent(0).getName(), "bfcp");

		if (!comiple(bfcpDirectory, false)) {
			System.out.println("BFCp本身编译失败");
			emptyCache();
			return null;
		}
		// 4.将BFC中所有被更改的测试文件迁移到BFC-1
		copyToTarget(pRFC, bfcpDirectory);
		// 5.编译BFCP
		if (!comiple(bfcpDirectory, true)) {
			System.out.println("BFCp迁移后编译失败");
			emptyCache();
			return null;
		}
		// 6.测试BFCP
		String result = testBFCP(bfcpDirectory, realTestCase);

		if (realTestCase.size() > 0) {
			ExperResult.numSuc++;
			pRFC.setTestCaseSet(realTestCase);
			System.out.println("迁移成功" + result.toString());
		} else {
			System.out.println("迁移失败" + result.toString());
			emptyCache();
			return null;
		}
		emptyCache();
		return realTestCase;
//		ExperResult.numSuc++;
	}

	public boolean comiple(File file, boolean record) throws Exception {
		exec.setDirectory(file);
		return exec.execBuildWithResult("mvn compile test-compile", record);
	}

	public Set<String> testBFC(File file, PotentialRFC pRFC) throws Exception {
		// 一定要先设置当前文件路径
		exec.setDirectory(file);
		// 开始测试
		Set<String> result = new HashSet<>();
		for (TestFile testFile : pRFC.getTestCaseFiles()) {
			if (testFile.getType() == Type.TEST_SUITE) {
				testSuite(testFile, result);
			}
		}
		return result;
	}

	public void testSuite(TestFile testFile, Set<String> result) throws Exception {
		Map<String, RelatedTestCase> methodMap = testFile.getTestMethodMap();
		if (methodMap != null && methodMap.size() > 0) {
			testMethod(methodMap.keySet(), testFile.getQualityClassName(), result);
		}
	}

	public void testMethod(Set<String> methodSet, String qualityClassName, Set<String> result) throws Exception {
		for (String method : methodSet) {
			String testCase = qualityClassName + "#" + method;
			MigrateFailureType type = exec.execTestWithResult("mvn test -Dtest=" + testCase);
			if (type == MigrateFailureType.TESTSUCCESS) {
				result.add(testCase);
			}
		}
	}

	public String testBFCP(File file, Set<String> realTestCase) throws Exception {
		exec.setDirectory(file);
		StringJoiner sj = new StringJoiner(";", "[", "]");
		Iterator<String> iterator = realTestCase.iterator();
		while (iterator.hasNext()) {
			String testCase = iterator.next();
			MigrateFailureType type = exec.execTestWithResult("mvn test -Dtest=" + testCase);
			sj.add(testCase + ":" + type.getName());
			if (type != MigrateFailureType.NONE) {
				iterator.remove();
			}
		}
		return sj.toString();
	}

	public File checkout(String bfc, String commitId, String version) {
		String cacheFile = Conf.cachePath + bfc + File.separator + commitId + File.separator + version + File.separator
				+ UUID.randomUUID().toString();
		File file = new File(cacheFile);
		if (!file.exists()) {
			file.mkdirs();
		}
		exec.setDirectory(file);
		exec.execPrintln("cp -rf " + Conf.metaPath + " " + cacheFile);
		File result = new File(cacheFile + File.separator + "meta");
		exec.setDirectory(result);
		exec.execPrintln("git checkout -f " + commitId);
		return result;
	}

	public void copyToTarget(PotentialRFC pRFC, File targerProjectDirectory) {
		// copy
		String targetPath = null;
		for (TestFile testFile : pRFC.getTestCaseFiles()) {
			File file = new File(
					"tmp" + File.separator + pRFC.getCommit().getName() + File.separator + testFile.getNewPath());
			// 测试文件是被删除则什么也不作。
			if (testFile.getNewPath().contains("/dev/null")) {
				continue;
			}

			targetPath = testFile.getNewPath();
			// 测试文件不是删除，则copy
			targetPath = FileUtil.getDirectoryFromPath(targetPath);
			File file1 = new File(targerProjectDirectory.getAbsoluteFile() + File.separator + targetPath);
			if (!file1.exists()) {
				file1.mkdirs();
			}
			exec.exec("cp " + file.getAbsolutePath() + " " + file1.getAbsolutePath());

		}
	}

	public void emptyCache() {
//		exec.setDirectory(new File(Conf.cachePath));
//		exec.exec("rm -rf * ");
	}

}

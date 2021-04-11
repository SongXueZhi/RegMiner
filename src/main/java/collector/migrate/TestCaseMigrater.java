package collector.migrate;

import java.io.File;
import java.util.Iterator;
import java.util.Set;
import java.util.StringJoiner;

import model.MigrateItem.MigrateFailureType;
import model.PotentialRFC;
import model.TestFile;
import utils.FileUtil;

public class TestCaseMigrater extends Migrater {
	public final static int PASS = 0;
	public final static int FAL = 1;
	public final static int CE = -1;
	public final static int UNRESOLVE = -2;
	String projectName;

	public TestCaseMigrater(String projectName) {
		this.projectName = projectName;
	}

	public void migrate(PotentialRFC pRFC, Set<String> bicSet) {
		System.out.println(pRFC.getCommit().getName() + " 开始迁移bic");
		for (String bic : bicSet) {
			try {
				migrate(pRFC, bic);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public int migrate(PotentialRFC pRFC, String bic) throws Exception {
		System.out.println("bic:" + bic);
		File bicDirectory = checkout(pRFC.getCommit().getName(), bic, "bic");
		pRFC.fileMap.put(bic, bicDirectory);
		// 第一次编译未copy时候编译尝试
		if (!comiple(bicDirectory, false)) {
			emptyCache();
			System.out.println("本身编译失败");
			return CE;
		}
		// 第一次编译成功,则开始
		copyToTarget(pRFC, bicDirectory);
		// 编译
		if (comiple(bicDirectory, true)) {
			Set<String> rt = pRFC.getTestCaseSet();
			int a = test(bicDirectory, rt);
			emptyCache();
			return a;
		} else {
			emptyCache();
			System.out.println("迁移后编译失败");
			return CE;
		}
	}

	public boolean comiple(File file, boolean record) throws Exception {
		exec.setDirectory(file);
		return exec.execBuildWithResult("mvn compile test-compile", record);
	}

	public void emptyCache() {
//		exec.setDirectory(new File(Conf.cachePath));
//		exec.exec("rm -rf * ");
	}

	public int test(File file, Set<String> realTestCase) throws Exception {
		boolean result = false;
		boolean result1 = false;
		exec.setDirectory(file);
		StringJoiner sj = new StringJoiner(";", "[", "]");
		Iterator<String> iterator = realTestCase.iterator();

		while (iterator.hasNext()) {
			String testCase = iterator.next();
			MigrateFailureType type = exec.execTestWithResult("mvn test -Dtest=" + testCase);
			sj.add(testCase + ":" + type.getName());

			if (type == MigrateFailureType.NONE) {
				result = true;
			}
			if (type == MigrateFailureType.TESTSUCCESS) {
				result1 = true;
			}
		}
		System.out.println("测试bic " + sj);
		if (result) {
			System.out.println("FAL");
			return FAL;
		}
		if (result1) {
			System.out.println("PASS");
			return PASS;
		}
		System.out.println("UNRESOLVE");
		return UNRESOLVE;
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
			// System.out.println("迁移的文件有：" + targetPath);
			// 测试文件不是删除，则copy
			targetPath = FileUtil.getDirectoryFromPath(targetPath);
			File file1 = new File(targerProjectDirectory.getAbsoluteFile() + File.separator + targetPath);
			if (!file1.exists()) {
				file1.mkdirs();
			}
			exec.exec("cp -rf " + file.getAbsolutePath() + " " + file1.getAbsolutePath());

		}
	}
}

package miner.migrate;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import constant.Conf;
import constant.Constant;
import exec.TestExecutor;
import miner.migrate.model.MergeTask;
import model.ChangedFile;
import model.PotentialRFC;
import model.TestFile;
import utils.FileUtilx;

public class Migrater {
	TestExecutor exec = new TestExecutor();

	public File checkout(String bfcId, String commitId, String version) throws IOException {
		String cacheFile = Conf.CACHE_PATH + File.separator + bfcId + File.separator + commitId + File.separator
				+ version + "_" + UUID.randomUUID();
		File file = new File(cacheFile);
		if (!file.exists()) {
			file.mkdirs();
		}
		exec.setDirectory(file);
		FileUtils.copyDirectoryToDirectory(new File(Conf.META_PATH), new File(cacheFile));
		File result = new File(cacheFile + File.separator + "meta");
		exec.setDirectory(result);
		exec.execPrintln("git checkout -f " + commitId);
		return result;
	}

	public String findJavaFile(@NotNull String className, @NotNull String[] projectJavaFiles) {
		String path = className.replace(".", File.separator) + ".java";
		for (String file : projectJavaFiles) {
			if (file.contains(path)) {
				return file;
			}
		}
		return null;
	}

	public String findClassFile(@NotNull String className, @NotNull String[] projectJavaFiles) {
		String path = className.replace(".", File.separator) + ".class";
		for (String file : projectJavaFiles) {
			if (file.contains(path)) {
				return file;
			}
		}
		return null;
	}

	/**
	 * @param pRFC
	 * @param tDir
	 */
	public void mergeTwoVersion_BaseLine(PotentialRFC pRFC, File tDir) {
		/**
		 *
		 * 注意！bfc的patch中可能存在 普通java文件，测试文件（相关测试用例，非测试用例但测试目录下的java文件），配置文件(测试目录下的，其他)
		 * 在base_line中我们只迁移 测试文件，和之前不存在的配置文件（暂时不做文本的merge）
		 */
		// 相关测试用例
		List<TestFile> testSuite = pRFC.getTestCaseFiles();
		// 非测试用例的在测试目录下的其他文件
		List<TestFile> underTestDirJavaFiles = pRFC.getTestRelates();
		// merge测试文件
		// 整合任务
		MergeTask mergeJavaFileTask = new MergeTask();
		mergeJavaFileTask.addAll(testSuite).compute();
		File bfcDir = pRFC.fileMap.get(pRFC.getCommit().getName());
		for (Map.Entry<String, ChangedFile> entry : mergeJavaFileTask.getMap().entrySet()) {
			String newPathInBfc = entry.getKey();
			File bfcFile = new File(bfcDir, newPathInBfc);
			File tFile = new File(tDir, newPathInBfc);
			if (tFile.exists()) {
				tFile.deleteOnExit();
			}
			// 直接copy过去
			try {
				FileUtils.forceMkdirParent(tFile);
				FileUtils.copyFileToDirectory(bfcFile, tFile.getParentFile());
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}

		}

	}

	public void copyToTarget(@NotNull PotentialRFC pRFC, File targerProjectDirectory) throws IOException {
		// copy
		String targetPath = null;
		for (TestFile testFile : pRFC.getTestCaseFiles()) {
			File file = new File(Conf.TMP_FILE + File.separator + pRFC.getCommit().getName() + File.separator
					+ testFile.getNewPath());
			// 测试文件是被删除则什么也不作。
			if (testFile.getNewPath().contains(Constant.NONE_PATH)) {
				continue;
			}

			targetPath = testFile.getNewPath();
			// 测试文件不是删除，则copy
			targetPath = FileUtilx.getDirectoryFromPath(targetPath);
			File file1 = new File(targerProjectDirectory.getAbsoluteFile() + File.separator + targetPath);
			if (!file1.exists()) {
				file1.mkdirs();
			}
			FileUtils.copyFileToDirectory(file,file1);
		}
	}
}

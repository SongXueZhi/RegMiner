package miner.migrate;

import java.io.File;
import java.util.UUID;

import constant.Conf;
import exec.TestExecutor;

public class Migrater {
	TestExecutor exec = new TestExecutor();

	public File checkout(String bfcId, String commitId, String version) {
		String cacheFile = Conf.CACHE_PATH + File.separator + bfcId + File.separator + commitId + File.separator
				+ version + "_" + UUID.randomUUID();
		File file = new File(cacheFile);
		if (!file.exists()) {
			file.mkdirs();
		}
		exec.setDirectory(file);
		exec.execPrintln("cp -rf " + Conf.META_PATH + " " + cacheFile);
		File result = new File(cacheFile + File.separator + "meta");
		exec.setDirectory(result);
		exec.execPrintln("git checkout -f " + commitId);
		return result;
	}

}

package migration;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.eclipse.jgit.lib.Repository;

import model.PotentialRFC;
import model.TestFile;
import utils.GitUtil;

public class TestcaseMigartion {
	Repository repo = null;
	public static String tmpFile = "D:/tmp/";

	public TestcaseMigartion(Repository repo) {
		this.repo = repo;
	}

	public void testReduce(PotentialRFC pRFC) throws Exception {
		List<TestFile> testFile = pRFC.getTestCaseFiles();
		for (TestFile file : testFile) {
			String code = GitUtil.getContextWithFile(repo, pRFC.getCommit(), file.getNewPath());
			writeToCache(file.getNewPath(), code);
		}
	}

	public void writeToCache(String path, String code) throws Exception {
		File file = new File(tmpFile);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(tmpFile + path.substring(path.lastIndexOf("/"), path.length()));
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		FileWriter fwriter = null;
		try {
			fwriter = new FileWriter(file.getAbsoluteFile());
			fwriter.write(code);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				fwriter.flush();
				fwriter.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}

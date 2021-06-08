package regminer.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class FileUtilsTest {
	@Test
	public void testCopyDirToDir() throws IOException {
		String meta = "C:/Users/sxzdh/Documents/pcode/meta";
		String cache = "C:/Users/sxzdh/Documents/pcode/cache";

		FileUtils.copyDirectoryToDirectory(new File(meta), new File(cache));

	}
	@Test
	public void testCopyToDir() throws IOException {
		String meta = "C:/Users/sxzdh/Documents/pcode/meta/pom.xml";
		String cache = "C:/Users/sxzdh/Documents/pcode/cache";
		FileUtils.copyFileToDirectory(new File(meta),new File(cache));
	}

}

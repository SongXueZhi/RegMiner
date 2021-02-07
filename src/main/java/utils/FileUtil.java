package utils;

import java.io.File;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

public class FileUtil {

	public void createdNewDirectory(String path) {

	}

	// /home/sxz/document/mmmmm-ddahkdak989/123.java
	public static String getDirectoryFromPath(String path) {
		return path.substring(0, path.lastIndexOf("/"));
	}
	
	@SuppressWarnings("deprecation")
	public static void log(String line, String path, String header) throws Exception {
		File file = new File(path);
		if (!file.isDirectory()) {
			file.delete();
			file.mkdirs();
		}
		File file1 = new File(file.getAbsoluteFile() + File.separator + header + "_" + ".txt");
		FileUtils.writeStringToFile(file1, line, true);
	}

	public static String getUUID() {
		return UUID.randomUUID().toString();

	}
}

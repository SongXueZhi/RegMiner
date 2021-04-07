package utils;

import java.io.File;

public class Test {
	static int num = 0;

	public static void showDirectory(File file) {

		File[] files = file.listFiles();
		for (File a : files) {

			if (a.isDirectory()) {
				showDirectory(a);
			} else {
				String path = a.getAbsolutePath();
				if (path.contains("test") && path.contains(".java")) {
					num++;
				}
			}
		}
	}

	public static void main(String[] args) {
		File file = new File("/home/sxz/Documents/fastjson");
		showDirectory(file);
		System.out.println(num);
	}
}
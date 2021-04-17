package gitwalk;

import java.io.FileInputStream;

import org.junit.Before;
import org.junit.Test;

import collector.migrate.TestCaseMigrater;

public class TestCompilationUtil {
	String fileContent = "";

	@Before
	public void init() {
		String filePath = "/home/sxz/Desktop/Solution.java";
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(filePath); // 内容是：abc
			StringBuilder sb = new StringBuilder();
			int temp = 0;
			// 当temp等于-1时，表示已经到了文件结尾，停止读取
			while ((temp = fis.read()) != -1) {
				sb.append((char) temp);
			}
			fileContent = sb.toString();
		} catch (Exception exc) {

		}
	}

//
//	// @Test
//	public void testGetMethodList() {
//
//		List<Methodx> methodList = CompilationUtil.getAllMethod(fileContent);
//		for (Methodx method : methodList) {
//			String name = method.getSignature();
//			int p = method.getStartLine();
//			int q = method.getStopLine();
//			System.out.println(name + " " + p + " --> " + q);
//		}
//	}
//
//	// @Test
//	public void testGetClassName() {
//		String ss = CompilationUtil.getQualityClassName(fileContent);
//		System.out.println(ss);
//	}
	@Test
	public void testPruneMethod() throws Exception {
		TestCaseMigrater tm = new TestCaseMigrater();
	}
}

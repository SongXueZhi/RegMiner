package gitwalk.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import utils.FileUtilx;

public class TestUtils {

	@Test
	public void testGetDirectory() {
		String filePath = "/home/sxz/document/mmmmm-ddahkdak989/123.java";
		String result = FileUtilx.getDirectoryFromPath(filePath);
		assertEquals("/home/sxz/document/mmmmm-ddahkdak989", result);

	}
}

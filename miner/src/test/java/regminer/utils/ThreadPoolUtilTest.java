package regminer.utils;

import org.junit.Test;

public class ThreadPoolUtilTest {

	@Test
	public void testIOIntesivePoolSize() {
		int a = ThreadPoolUtil.ioIntesivePoolSize();
		System.out.println(a);
	}
}

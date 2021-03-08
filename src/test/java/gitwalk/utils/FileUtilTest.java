package gitwalk.utils;

import java.util.Set;

import org.junit.Test;

import collector.migrate.BICFinder;

public class FileUtilTest {

	@Test
	public void testGetBICSet() {
		BICFinder bic = new BICFinder(null);
		Set<String> bicSet = bic.readBICSetFromFile();
		for (String b : bicSet) {
			System.out.println(b);
		}
	}
}

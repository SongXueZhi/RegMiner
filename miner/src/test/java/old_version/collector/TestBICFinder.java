package old_version.collector;

import java.util.List;

import org.junit.Test;

import regminer.miner.migrate.BICFinder;

public class TestBICFinder {
	@Test
	public void test() {
		BICFinder bicFinder = new BICFinder();
		List<String> result = bicFinder.revListCommand("975302bdb60e16188163161dad44e82221dcc546");
		for (String iterable : result) {
			System.out.println(iterable);
		}
	}

}

package gitwalk.collector;

import org.junit.Test;

import miner.migrate.TestCaseDeterminer;

public class TestCheckout {
	@Test
	public void testCheckout() {
		TestCaseDeterminer tm = new TestCaseDeterminer(null);
		tm.checkout("", "545a06beec0b755ec1057147260b56fe23ad3a66", "s");

	}
}

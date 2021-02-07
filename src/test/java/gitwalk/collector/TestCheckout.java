package gitwalk.collector;

import org.junit.Test;

import collector.migrate.TestMigrator;

public class TestCheckout {
	@Test
	public void testCheckout() {
		TestMigrator tm = new TestMigrator(null);
		tm.checkout("545a06beec0b755ec1057147260b56fe23ad3a66", "s");

	}
}

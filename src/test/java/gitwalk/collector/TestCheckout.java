package gitwalk.collector;

import org.junit.Test;

import collector.migrate.TestReducer;

public class TestCheckout {
	@Test
	public void testCheckout() {
		TestReducer tm = new TestReducer(null);
		tm.checkout("545a06beec0b755ec1057147260b56fe23ad3a66", "s");

	}
}

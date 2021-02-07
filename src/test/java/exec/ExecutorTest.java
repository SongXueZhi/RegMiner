package exec;

import java.io.File;

import org.junit.Test;

public class ExecutorTest {

	TestExecutor exec = new TestExecutor();

	@Test
	public void testExec() throws Exception {
		exec.setDirectory(new File("/home/sxz/Documents/code/codecache/fastjson"));
		boolean cc = exec.execBuildWithResult("mvn compile");
		System.out.println(cc);
		exec.execPrintln("git show;mvn compile");
	}

}

package regminer.exec;

import org.junit.Test;

import java.io.File;

public class ExecutorTest {

	TestExecutor exec = new TestExecutor();

	@Test
	public void testExec() throws Exception {
		exec.setDirectory(new File("C:\\Users\\sxzdh\\Documents\\pcode\\fastjson\\fastjson"));
		boolean cc = exec.execBuildWithResult("mvn compile", false);
		System.out.println(cc);
		exec.execPrintln("git show;mvn compile");
	}

    @Test
    public void setDirectory() {
    }

    @Test
    public void exec() {
    }

    @Test
    public void execPrintln() {
        exec.setDirectory(new File("C:\\Users\\sxzdh\\Documents\\pcode\\fastjson\\fastjson"));
        exec.execPrintln("git show");
    }
}

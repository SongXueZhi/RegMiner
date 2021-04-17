
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 *
 * @author knightsong
 */
public class Executor {

	public final static String OS_WINDOWS = "windows";
	public final static String OS_MAC = "mac";
	public final static String OS_UNIX = "unix";

	static ProcessBuilder pb = new ProcessBuilder();

//	static {
//		Configs.refresh();
//	}

	public static void setEnviroment(String args[]) {

		Map<String, String> map = pb.environment();
		StringBuilder PATH = new StringBuilder(map.get("PATH"));
		for (int i = 1; i < args.length; i++) {
			PATH.append(File.pathSeparator).append(args[i]);
		}
		map.put("PATH", PATH.toString());
	}

	public void setDirectory(File file) {
		pb.directory(file);
	}

	public String exec(String cmd) {
		StringBuilder builder = new StringBuilder();
		try {
			String OS = System.getProperty("os.name").toLowerCase();
			if (OS.equals(OS_WINDOWS)) {
				pb.command("cmd.exe", "/c", cmd);
			} else {
				pb.command("bash", "-c", cmd);
			}
			Process process = pb.start();
			InputStreamReader inputStr = new InputStreamReader(process.getInputStream());
			BufferedReader bufferReader = new BufferedReader(inputStr);
			String line;
			while ((line = bufferReader.readLine()) != null) {
				builder.append("\n").append(line);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return builder.toString();
	}

	public int execPrintln(String cmd) {
		int a = 1;
		try {
			String OS = System.getProperty("os.name").toLowerCase();
			if (OS.equals(OS_WINDOWS)) {
				pb.command("cmd.exe", "/c", cmd);
			} else {
				pb.command("bash", "-c", cmd);
			}
			Process process = pb.start();
			InputStreamReader inputStr = new InputStreamReader(process.getInputStream());
			BufferedReader bufferReader = new BufferedReader(inputStr);
			String line;
			while ((line = bufferReader.readLine()) != null) {
				System.out.println(line);
			}
			try {
				a = process.waitFor();
			} catch (InterruptedException ex) {
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return a;
	}
}

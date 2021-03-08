package exec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GitExecutor extends Executor {

	public List<String> runCommand(String cmd) {
		List<String> result = new ArrayList<>();
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
				result.add(line);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return result;
	}
}

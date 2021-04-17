
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TestExecutor extends Executor {

	// 请注意以最小的单元运行任务
	public boolean execBuildWithResult(String cmd, boolean record) throws Exception {
		boolean result = false;
		try {
			String OS = System.getProperty("os.name").toLowerCase();
			if (OS.equals(OS_WINDOWS)) {
				pb.command("cmd.exe", "/c", cmd);
			} else {
				pb.command("bash", "-c", cmd);
			}
			Process process = pb.start();
			InputStreamReader inputStr = new InputStreamReader(process.getInputStream(), "gbk");
			BufferedReader bufferReader = new BufferedReader(inputStr);
			String line;
			StringBuilder sb = new StringBuilder();
			while ((line = bufferReader.readLine()) != null) {
				line = line.toLowerCase();
				sb.append(line + "\n");
				// FileUtils.writeStringToFile(new File("build_log.txt"), line, true);
				if (line.contains("success")) {
					result = true;
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public List<String> runCommand(String cmd) {
		List<String> result = new ArrayList<String>();
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
			int a = process.waitFor();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}
}

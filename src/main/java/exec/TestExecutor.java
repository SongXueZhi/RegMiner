package exec;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.io.FileUtils;

import model.MigrateItem.MigrateFailureType;

public class TestExecutor extends Executor {

	// 请注意以最小的单元运行任务
	public boolean execBuildWithResult(String cmd) throws Exception {
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
			while ((line = bufferReader.readLine()) != null) {
				line = line.toLowerCase();
				FileUtils.writeStringToFile(new File("build_log.txt"), line, true);
				if (line.contains("success")) {
					result = true;
				}

				//
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return result;
	}

	// 请注意以最小的单元运行任务
	public MigrateFailureType execTestWithResult(String cmd)
			throws Exception {
		try {
			String OS = System.getProperty("os.name").toLowerCase();
			if (OS.equals(OS_WINDOWS)) {
				pb.command("cmd.exe", "/c", cmd);
			} else {
				pb.command("bash", "-c", cmd);
			}
			Process process = pb.start();
			Timer t = new Timer();
			t.schedule(new TimerTask() {
				@Override
				public void run() {
					process.destroy();
				}
			}, 60000);
			InputStreamReader inputStr = new InputStreamReader(process.getInputStream());
			BufferedReader bufferReader = new BufferedReader(inputStr);
			String line;
			while ((line = bufferReader.readLine()) != null) {
				line = line.toLowerCase();
				if (line.contains("build success")) {
					t.cancel();
					return MigrateFailureType.TESTSUCCESS;
				} else if (line.contains("compilation error")) {
					t.cancel();
					return MigrateFailureType.CompilationFailed;
				} else if (line.contains("no test")) {
					t.cancel();
					return MigrateFailureType.NoTests;
				}
			}
			t.cancel();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return MigrateFailureType.NONE;
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
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return result;
	}
}

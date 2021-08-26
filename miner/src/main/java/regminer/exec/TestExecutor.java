package regminer.exec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import regminer.model.MigrateItem.MigrateFailureType;
import org.apache.commons.io.IOUtils;

public class TestExecutor extends Executor {

	// 请注意以最小的单元运行任务
	public boolean execBuildWithResult(String cmd, boolean record) throws Exception {
		cmd = "export JAVA_HOME=/home/sxz/java/jdk1.7.0_80/;export PATH=${JAVA_HOME}/bin:${PATH};"+cmd;
		boolean result = false;
		try {
			if (OS.contains(OS_WINDOWS)) {
				pb.command("cmd.exe", "/c", cmd);
			} else {
				pb.command("bash", "-c", cmd);
			}
			Process process = pb.start();
			InputStreamReader inputStr = new InputStreamReader(process.getInputStream(), "gbk");
			BufferedReader bufferReader = new BufferedReader(inputStr);
			String line;
			StringBuilder sb = new StringBuilder();
//			pb.redirectErrorStream(true);
//			pb.redirectOutput(Redirect.PIPE);
			while ((line = bufferReader.readLine()) != null) {
				line = line.toLowerCase();
				sb.append(line + "\n");
				// FileUtils.writeStringToFile(new File("build_log.txt"), line, true);
				if (line.contains("success")) {
					process.destroy();
					IOUtils.close(inputStr,bufferReader);
					result = true;
					return true;
				}
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return result;
	}


	// 请注意以最小的单元运行任务
	public MigrateFailureType execTestWithResult(String cmd) throws Exception {
		cmd = "export JAVA_HOME=/home/sxz/java/jdk1.7.0_80/;export PATH=${JAVA_HOME}/bin:${PATH};"+cmd;
		try {
			if (OS.contains(OS_WINDOWS)) {
				pb.command("cmd.exe", "/c", cmd);
			} else {
				pb.command("bash", "-c", cmd);
			}
			final Process process = pb.start();
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
				} else if (line.contains("compilation error") || line.contains("compilation failure")) {
					t.cancel();
					return MigrateFailureType.CompilationFailed;
				} else if (line.contains("no test")) {
					t.cancel();
					return MigrateFailureType.NoTests;
				}
			}
			t.cancel();
			IOUtils.close(inputStr,bufferReader);
			process.destroy();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return MigrateFailureType.NONE;
	}

	public List<String> runCommand(String cmd) {
		List<String> result = new ArrayList<String>();
		try {
			if (OS.contains(OS_WINDOWS)) {
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
			IOUtils.close(inputStr,bufferReader);
			process.destroy();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}

	public void record(String content) {
//		String[] a1 = content.split("compilation error");
////		if (a1.length < 2) {
////			a1 = content.split("compilation error");
////		}
//		if (a1.length < 2) {
//			ExperResult.unknow++;
//			System.out.print("_unknow_length<2");
//			return;
//		}
//		String[] a2 = a1[1].split("\n");
//
//		boolean cannotFindMethod = false;
//		boolean cannotFindClass = false;
//		boolean cannotFindPackage = false;
//		boolean cannotFindVariable = false;
//		boolean packageNotExist = false;
//
//		for (int i = 0; i < a2.length; i++) {
//			if (a2[i].contains("cannot") && a2[i].contains("find") && a2[i + 1].contains("symbol:")
//					&& a2[i + 1].contains("method") && !cannotFindMethod) {
//				System.out.print("_cannotFindMethod");
//				ExperResult.methodNotFind++;
//				cannotFindMethod = true;
//				continue;
//			}
//			if (a2[i].contains("cannot") && a2[i].contains("find") && a2[i + 1].contains("symbol:")
//					&& a2[i + 1].contains("class") && !cannotFindClass) {
//				System.out.print("_cannotFindClass");
//				ExperResult.classNotFind++;
//				cannotFindClass = true;
//				continue;
//			}
//
//			if (a2[i].contains("cannot") && a2[i].contains("find") && a2[i + 1].contains("symbol:")
//					&& a2[i + 1].contains("package") && !cannotFindPackage) {
//				System.out.print("_cannotFindPackage");
//				ExperResult.packageNotFind++;
//				cannotFindPackage = true;
//				continue;
//			}
//			if (a2[i].contains("cannot") && a2[i].contains("find") && a2[i + 1].contains("symbol:")
//					&& a2[i + 1].contains("variable") && !cannotFindVariable) {
//				System.out.print("_cannotFindVariable");
//				ExperResult.variableNotFind++;
//				cannotFindVariable = true;
//				continue;
//			}
//			if (a2[i].contains("package") && a2[i].contains("not exist") && !packageNotExist) {
//				System.out.print("_packageNotExist");
//				ExperResult.packageNotExits++;
//				packageNotExist = true;
//				continue;
//			}
//		}
//		if (!cannotFindMethod && !cannotFindClass && !cannotFindPackage && !packageNotExist && !cannotFindVariable) {
//			if (content.contains("cannnot find symbol")) {
//				System.out.print("_cannnotfindsymbol");
//				ExperResult.symbolNotFind++;
//			} else {
//				System.out.print("_unknow");
//				ExperResult.unknow++;
//			}
//		}
	}
}

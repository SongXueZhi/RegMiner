package regminer.exec;

import org.apache.commons.io.IOUtils;
import regminer.model.MigrateItem.MigrateFailureType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TestExecutor extends Executor {

    // 请注意以最小的单元运行任务
    public boolean execBuildWithResult(String cmd, boolean record) {
        Process process = null;
        InputStreamReader inputStr = null;
        BufferedReader bufferReader = null;
        try {
            if (OS.contains(OS_WINDOWS)) {
                pb.command("cmd.exe", "/c", cmd);
            } else {
                pb.command("bash", "-c", cmd);
            }
            process = pb.start();
            inputStr = new InputStreamReader(process.getInputStream(), "gbk");
            bufferReader = new BufferedReader(inputStr);
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = bufferReader.readLine()) != null) {
                line = line.toLowerCase();
                sb.append(line + "\n");
                // FileUtils.writeStringToFile(new File("build_log.txt"), line, true);
                if (line.contains("success")) {
                    return true;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (process != null) {
                    process.destroy();
                }
                if (inputStr != null) {
                    IOUtils.close(inputStr);
                }
                if (bufferReader != null) {
                    IOUtils.close(bufferReader);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return false;
    }


    // 请注意以最小的单元运行任务
    public MigrateFailureType execTestWithResult(String cmd) {
        Process process = null;
        Timer t = null;
        InputStreamReader inputStr = null;
        BufferedReader bufferReader = null;
        try {
            if (OS.contains(OS_WINDOWS)) {
                pb.command("cmd.exe", "/c", cmd);
            } else {
                pb.command("bash", "-c", cmd);
            }
            process = pb.start();
            t = new Timer();
            Process finalProcess = process;
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    finalProcess.destroy();
                }
            }, 60000);
            inputStr = new InputStreamReader(process.getInputStream());
            bufferReader = new BufferedReader(inputStr);
            String line;
            boolean testCE = false;
            while ((line = bufferReader.readLine()) != null) {
                line = line.toLowerCase();
                if (line.contains("build success")) {
                    return MigrateFailureType.TESTSUCCESS;
                } else if (line.contains("compilation error") || line.contains("compilation failure")) {
                    testCE = true;
                } else if (line.contains("no test")) {
                    return MigrateFailureType.NoTests;
                }
            }
            if (testCE) {
                return MigrateFailureType.CompilationFailed;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {

                if (t != null) {
                    t.cancel();
                }
                if (process != null) {
                    process.destroy();
                }
                if (inputStr != null) {
                    IOUtils.close(inputStr);
                }
                if (bufferReader != null) {
                    IOUtils.close(bufferReader);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return MigrateFailureType.NONE;
    }

    public List<String> runCommand(String cmd) {
        Process process = null;
        InputStreamReader inputStr = null;
        BufferedReader bufferReader = null;
        List<String> result = new ArrayList<String>();
        try {
            if (OS.contains(OS_WINDOWS)) {
                pb.command("cmd.exe", "/c", cmd);
            } else {
                pb.command("bash", "-c", cmd);
            }
            process = pb.start();
            inputStr = new InputStreamReader(process.getInputStream());
            bufferReader = new BufferedReader(inputStr);
            String line;
            while ((line = bufferReader.readLine()) != null) {
                result.add(line);
            }
            int a = process.waitFor();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            try{
                if (process != null) {
                    process.destroy();
                }
                if (inputStr != null) {
                    IOUtils.close(inputStr);
                }
                if (bufferReader != null) {
                    IOUtils.close(bufferReader);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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

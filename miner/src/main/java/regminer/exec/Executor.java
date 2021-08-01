package regminer.exec;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import regminer.utils.FileUtilx;

/**
 * @author knightsong
 */
public class Executor {

    public final static String OS_WINDOWS = "windows";
    public final static String OS_MAC = "mac";
    public final static String OS_UNIX = "unix";

    static ProcessBuilder pb = new ProcessBuilder();
    private  static String PATH_AFFIX ="PATH";
    protected static String OS;

    static {
       OS = System.getProperty("os.name").toLowerCase();
        // ubuntu use “PATH” windows use “Path”
        if (OS.contains(OS_WINDOWS)) {
            PATH_AFFIX = "Path";
        }
//        ConfigLoader.refresh();  //保证不经过main入口的程序也能加载配置
//		暂时关闭
//		String[] toolPaths = ConfigLoader.envPath.split(";");
//		setEnviroment(toolPaths);
    }

    public static void setEnviroment(String[] args) {

        Map<String, String> map = pb.environment();

        StringBuilder PATH = new StringBuilder(map.get(PATH_AFFIX));
        for (int i = 1; i < args.length; i++) {
            PATH.append(File.pathSeparator).append(args[i]);
        }
        map.put(PATH_AFFIX, PATH.toString());
    }

    public void setDirectory(File file) {
        pb.directory(file);
    }

    public Set<String> execWithSetResult(String cmd){
       Set<String> result = new HashSet<>();
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
                line = line.trim().replace("\n","");
                if (line.equals("") || line.equals(" ")){
                    continue;
                }else{
                    result.add(line);
                }
            }
            IOUtils.close(inputStr,bufferReader);
            process.destroy();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }
    public String exec(String cmd) {
        StringBuilder builder = new StringBuilder();
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
                builder.append("\n").append(line);
            }
            IOUtils.close(inputStr,bufferReader);
            process.destroy();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return builder.toString();
    }

    public int execPrintln(String cmd) {
        int a = 1;
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
                FileUtilx.log(line);
            }
            try {
                a = process.waitFor();
            } catch (InterruptedException ex) {
            }
            IOUtils.close(inputStr,bufferReader);
            process.destroy();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return a;
    }
}

package org.regminer.common.exec;

import org.apache.commons.io.IOUtils;
import org.regminer.common.constant.Configurations;
import org.regminer.common.model.OS;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * @author knightsong
 */
public class Executor {

    ProcessBuilder pb = new ProcessBuilder();

    /**
     * Set working directory to process
     *
     * @param file working directory
     */
    public Executor setDirectory(File file) {
        pb.directory(file);
        return this;
    }

    public ExecResult exec(String cmd, int timeout) {
        return this.exeCmd(cmd, timeout);
    }
    public ExecResult exec(String cmd) {
        return this.exec(cmd, 0);
    }

    /**
     * Run command line and get results,you can combine the multi-command by ";"
     * for example: mvn test -Dtest="testcase",or git reset;mvn compile
     *
     * @param cmd command line
     * @return return result by exec command
     */
    public ExecResult exeCmd(String cmd, int timeout) {

        ExecResult execResult = new ExecResult();

        StringBuilder builder = new StringBuilder();
        Process process = null;
        InputStreamReader inputStr = null;
        BufferedReader bufferReader = null;
        pb.redirectErrorStream(true); //redirect error stream to standard stream

        long startTime = System.currentTimeMillis();
        try {
            if (Configurations.osName.equals(OS.WINDOWS)) {
                pb.command("cmd.exe", "/c", cmd);
            } else {
                pb.command("bash", "-c", cmd);
            }
            process = pb.start();
            if (timeout > 0) {
                boolean completed = process.waitFor(timeout, TimeUnit.MINUTES);
                if (!completed) {
                    execResult.setTimeOut(true);
                    return execResult;
                }
            }

            // Read result from out stream, if execute commands has ">>"
            //,nothing will be read.
            inputStr = new InputStreamReader(process.getInputStream());
            bufferReader = new BufferedReader(inputStr);
            String line;
            while ((line = bufferReader.readLine()) != null) {
                builder.append("\n").append(line);
            }

        } catch (IOException | InterruptedException ex) {
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
            execResult.setUsageTime(System.currentTimeMillis() - startTime);
            execResult.setMessage(builder.toString());
        }
        return execResult;
    }
}


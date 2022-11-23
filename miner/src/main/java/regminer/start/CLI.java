package regminer.start;

import org.apache.commons.cli.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

/**
 * @Author: sxz
 * @Date: 2022/11/23/18:47
 * @Description:
 */
public class CLI {
    private static Options OPTIONS = new Options();
    private static CommandLine commandLine;
    private static String HELP_STRING = null;
    private final static String CONFIG_LONG_OPT="configPath";
    private final static String TASK_OPT="task";
    public static void main(String[] args) throws Exception {
        CommandLineParser commandLineParser = new DefaultParser();
        // help
        OPTIONS.addOption("help","usage help");
        // host
        OPTIONS.addOption(Option.builder("t").argName("regression or bug").required().hasArg(true).longOpt(TASK_OPT).type(String.class)
                .desc("target task for regminer").build());
        // port
        OPTIONS.addOption(Option.builder("p").hasArg(true).longOpt(CONFIG_LONG_OPT).type(String.class)
                .desc("Configuration file path").build());
        try {
            commandLine = commandLineParser.parse(OPTIONS, args);
            ConfigLoader.CONFIGPATH = commandLine.getOptionValue(CONFIG_LONG_OPT);
            Miner.taskType = commandLine.getOptionValue(TASK_OPT);
            Miner.start();
        } catch (ParseException e) {
            System.out.println(e.getMessage() + "\n" + getHelpString());
            System.exit(0);
        }
    }
    private static String getHelpString() {
        if (HELP_STRING == null) {
            HelpFormatter helpFormatter = new HelpFormatter();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
            helpFormatter.printHelp(printWriter, HelpFormatter.DEFAULT_WIDTH, "scp -help", null,
                    OPTIONS, HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null);
            printWriter.flush();
            HELP_STRING = new String(byteArrayOutputStream.toByteArray());
            printWriter.close();
        }
        return HELP_STRING;
    }
}

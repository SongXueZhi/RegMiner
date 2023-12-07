package org.regminer.miner.start;

import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.regminer.bic.api.EnhancedBinarySearch;
import org.regminer.bic.api.SearchBICContext;
import org.regminer.common.constant.Configurations;
import org.regminer.common.constant.Constant;
import org.regminer.ct.api.TestCaseParser;
import org.regminer.migrate.api.TestCaseMigrator;
import org.regminer.miner.BFCEvaluator;
import org.regminer.miner.PotentialBFCDetector;
import org.regminer.miner.SearchBFCContext;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: sxz
 * @Date: 2023/11/28/14:25
 * @Description: Command Line Interface for Miner application.
 */
public class MinerCli {
    private static final Options OPTIONS = new Options();
    static Logger logger = LogManager.getLogger(MinerCli.class);
    private static CommandLine commandLine;
    private static String HELP_STRING = null;

    private static Miner miner;

    public static void main(String[] args) {
        CommandLineParser commandLineParser = new DefaultParser();
        setupOptions();
        try {
            commandLine = commandLineParser.parse(OPTIONS, args);
            if (isValidCommandLine(commandLine)) {
                processCommands();
            }
        } catch (ParseException e) {
            logger.info("Error parsing command: {}", e.getMessage());
            printHelp();
        }
    }

    private static void setupOptions() {
        OPTIONS.addOption("h", "help", false, "Show usage help.");
        OPTIONS.addOption("ws", "workspace", true, "Specify the workspace directory.");
        OPTIONS.addOption("pj", "project", true, "Specify the project to mine.");
        OPTIONS.addOption("cfg", "config", true, "Specify the configuration path.");
        OPTIONS.addOption("t", "task", true, "Specify the task name.");
        OPTIONS.addOption("f", "filter", true, "Specify the filter tasks file path.");
    }

    private static boolean isValidCommandLine(CommandLine commandLine) {
        if (commandLine.hasOption("h")) {
            printHelp();
            return false;
        }
        return checkRequiredOptions(commandLine, "ws", "workspace") && checkRequiredOptions(commandLine, "pj",
                "project");
    }

    private static boolean checkRequiredOptions(CommandLine commandLine, String opt, String description) {
        if (!commandLine.hasOption(opt)) {
            logger.error("Missing required option: {}", description);
            return false;
        }
        return true;
    }

    private static void processCommands() {
        Configurations.rootDir = commandLine.getOptionValue("ws");
        Configurations.projectName = commandLine.getOptionValue("pj");
        Configurations.configPath = commandLine.getOptionValue("cfg", Configurations.configPath);
        Configurations.updateDependentFields();
        processTaskOption();
        processFilterOption();
    }

    private static void processFilterOption() {
        List<String> filterList =new ArrayList<>();
        if (commandLine.hasOption("f")) {
            String filterPath = commandLine.getOptionValue("f");
            try {
                filterList = FileUtils.readLines(new File(filterPath), "UTF-8");
            } catch (IOException e) {
                filterList = new ArrayList<>();
                logger.error("Error reading filter file: {}", e.getMessage());
            }
        }
        startMining(filterList);
    }

    private static void startMining(List<String> filterList) {
        miner = new Miner(new SearchBFCContext(new BFCEvaluator(new TestCaseParser(), new TestCaseMigrator()),
                new PotentialBFCDetector(filterList)),
                new SearchBICContext(new EnhancedBinarySearch()));
        miner.start();
    }
    private static void processTaskOption() {
        if (commandLine.hasOption("t")) {
            String taskName = commandLine.getOptionValue("t");
            if (Constant.TASK_LIST.contains(taskName)) {
                Configurations.taskName = taskName;
            } else {
                logger.error("Task name '{}' is not valid. Defaulting to BFC collection.", taskName);
            }
        }
    }

    private static void printHelp() {
        if (HELP_STRING == null) {
            initializeHelpString();
        }
        System.out.println(HELP_STRING);
    }

    private static void initializeHelpString() {
        HelpFormatter helpFormatter = new HelpFormatter();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
        helpFormatter.printHelp(printWriter, HelpFormatter.DEFAULT_WIDTH, "regs4j", null,
                OPTIONS, HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null, false);
        printWriter.flush();
        HELP_STRING = new String(byteArrayOutputStream.toByteArray());
        printWriter.close();
    }
}

package org.regs4j;

import org.apache.commons.cli.*;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.regminer.commons.constant.Configurations;
import org.regminer.commons.constant.Constant;
import org.regminer.commons.model.PotentialBFC;
import org.regminer.commons.model.Regression;
import org.regminer.commons.sql.MysqlManager;
import org.regminer.commons.model.Revision;
import org.regminer.ct.api.TestCaseParser;
import org.regminer.ct.model.TestResult;
import org.regminer.migrate.api.TestCaseMigrator;
import org.regminer.miner.BFCEvaluator;
import org.regminer.miner.PotentialBFCDetector;
import org.regminer.miner.SearchBFCContext;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class NewCLI {
    private static final Options OPTIONS = new Options();
    private static CommandLine commandLine;
    private static String HELP_STRING = null;
    private  static final TestCaseMigrator testCaseMigrator = new TestCaseMigrator();
    private final static  String TABLE_NAME = "regression";

    public static void main(String[] args) {
        Configurations.rootDir = System.getProperty("user.home")+ File.separator+"reg4j";
        Configurations.taskName= Constant.BFC_BIC_TASK;
        Configurations.updateDependentFields();

        CommandLineParser commandLineParser = new DefaultParser();
        setupOptions();
        try {
            commandLine = commandLineParser.parse(OPTIONS, args);
            processCommands();
        } catch (InvalidVersionException | ParseException | MissingVersionException | InvalidBugIDException e) {
            System.out.println(e.getMessage());
            printHelp();
        }
    }

    private static void setupOptions() {
        OPTIONS.addOption("h", "help", false, "usage help");
        OPTIONS.addOption("checkout", "checkout", true, "checkout bugs by ID");
        OPTIONS.addOption("p", true, "path to work");
        OPTIONS.addOption("v", true, "version for bug");
        OPTIONS.addOption("compile", "compile", true, "compile bugID");
        OPTIONS.addOption("test", "test", true, "test bugID");
    }

    private static void processCommands() throws InvalidVersionException, MissingVersionException,
            InvalidBugIDException {
        if (commandLine.hasOption("h")) {
            printHelp();
        }
        if (commandLine.hasOption("checkout")) {
            processCheckoutCommand();
        }
    }

    private static void processCheckoutCommand() throws InvalidVersionException, MissingVersionException,
            InvalidBugIDException {
        String bugIDParam = commandLine.getOptionValue("checkout");
        int bugID = Integer.parseInt(bugIDParam);
        if (commandLine.hasOption("v")) {
            String version = commandLine.getOptionValue("v", "");
            validateVersion(version);
            retrieveBugs(bugID, version);
        } else {
            throw new MissingVersionException("No version info");
        }
    }

    private static void validateVersion(String version) throws InvalidVersionException {
        if (!Arrays.asList("bic", "work", "bfc", "buggy").contains(version)) {
            throw new InvalidVersionException("Invalid version: " + version);
        }
    }

    private static void retrieveBugs(int bugID, String version) throws InvalidBugIDException, InvalidVersionException {
        List<Regression> regressionList = MysqlManager.getRegressions("select id,project_full_name,bfc,buggy,bic," +
                "work,testcase from " +
                "regression where " +
                "id='" + bugID + "'");

        if (regressionList == null || regressionList.size() == 0) {
            throw new InvalidBugIDException("Invalid bug id" + bugID);
        }
        Regression regression = regressionList.get(0);
        String projectName = regression.getProjectFullName().replace("/", "_");
        Configurations.projectName = projectName;
        Configurations.updateDependentFields();

        File projectDir = new File(Configurations.projectPath);
        PotentialBFC bfc = null;
        try {
            SearchBFCContext bfcContext = new SearchBFCContext(new BFCEvaluator(new TestCaseParser(), new TestCaseMigrator()),
                    new PotentialBFCDetector(List.of(regression.getBfcId())));
            bfc = bfcContext.searchBFC().get(0);
        } catch (Exception e) {
            System.out.println("Potential BFC not found");
            return;
        }

        if (version.equals("bfc")) {
            System.out.println("checkout successful:" + regression.getBfcId());
            return;
        }
        Revision targetVersion = switch (version) {
            case "bic" -> new Revision(regression.getBicId(),"bic");
            case "work" -> new Revision(regression.getWorkId(),"work");
            case "buggy" -> new Revision(regression.getBugId(),"buggy");
            default -> throw new InvalidVersionException("Invalid version: " + version);
        };
        TestResult testResult = null;
        try {
            testResult = testCaseMigrator.migrateAndTest(bfc, targetVersion.getCommitID());
        } catch (Exception e) {
            System.out.println("Test failed");
        }
        System.out.println(testResult);
    }


    private static void printHelp() {
        if (HELP_STRING == null) {
            HelpFormatter helpFormatter = new HelpFormatter();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
            helpFormatter.printHelp(printWriter, HelpFormatter.DEFAULT_WIDTH, "regs4j -help", null,
                    OPTIONS, HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null);
            printWriter.flush();
            HELP_STRING = new String(byteArrayOutputStream.toByteArray());
            printWriter.close();
        }
        System.out.println(HELP_STRING);
    }

    static class InvalidVersionException extends Exception {
        public InvalidVersionException(String message) {
            super(message);
        }
    }

    static class MissingVersionException extends Exception {
        public MissingVersionException(String message) {
            super(message);
        }
    }

    static class InvalidBugIDException extends Exception {
        public InvalidBugIDException(String message) {
            super(message);
        }
    }
}

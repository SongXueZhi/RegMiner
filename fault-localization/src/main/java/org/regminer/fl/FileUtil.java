package org.regminer.fl;

/**
 * @Author: sxz
 * @Date: 2023/12/27/18:53
 * @Description:
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class FileUtil {

    final static Logger logger = LogManager.getLogger(FileUtil.class);

    public static int totalPassedTests = 0;

    public static int totalFailedTests = 0;

    public static void printTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("Current time: " + df.format(new Date()));
    }

    public static long getTime() {
        return System.currentTimeMillis();
    }

    public static String countTime(long startTime) {
        DecimalFormat dF = new DecimalFormat("0.0000");
        return dF.format((float) (System.currentTimeMillis() - startTime) / 1000);
    }

    public static void writeLinesToFile(String path, List<String> lines) {
        writeLinesToFile(path, lines, false);
    }

    public static void writeLinesToFile(String path, Set<String> lines) {
        writeLinesToFile(path, lines, false);
    }

    public static void writeLinesToFile(String path, Set<String> lines, boolean append) {
        List<String> linesList = new ArrayList<>();
        linesList.addAll(lines);
        writeLinesToFile(path, linesList, append);
    }

    public static void writeLinesToFile(String path, List<String> lines, boolean append) {
        String dirPath = path.substring(0, path.lastIndexOf("/"));
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println(String.format("%s does not exists, and are created now via mkdirs()", dirPath));
        }
        BufferedWriter output = null;
        try {
            output = new BufferedWriter(new FileWriter(path, append));
            for (String line : lines) {
                output.write(line + "\n");
            }
            output.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToFile(String content) {
        writeToFile(Globals.flLogPath, content, true);
    }

    public static void writeToFile(String path, String content) {
        writeToFile(path, content, true);
    }

    public static void writeToFile(String path, String content, boolean append) {
        String dirPath = path.substring(0, path.lastIndexOf("/"));
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println(String.format("%s does not exists, and are created now via mkdirs()", dirPath));
        }
        BufferedWriter output = null;
        try {
            output = new BufferedWriter(new FileWriter(path, append));
            output.write(content);
            output.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> readFile(String path) {
        List<String> list = new ArrayList<>();
        try {
            final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.length() == 0)
                    System.err.println(String.format("Empty line in %s", path));
                list.add(line);
            }
            in.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<SuspiciousLocation> readBuggylocFile(String path) {
        List<SuspiciousLocation> buggyLocs = new ArrayList<>();
        try {
            final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                SuspiciousLocation sl = new SuspiciousLocation(line.split(":")[0], Integer.parseInt(line.split(":")[1]));
                buggyLocs.add(sl);
            }
            in.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return buggyLocs;
    }

    public static List<SuspiciousLocation> readStmtFile(String path) {
        List<SuspiciousLocation> stmtList = new ArrayList<>();
        try {
            final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
            String line;
            in.readLine();
            while ((line = in.readLine()) != null) {
                if (line.length() == 0)
                    logger.error("Empty line in %s", path);
                String[] tmps = line.split(":")[0].split("#")[0].split("\\$");
                String className = tmps[0] + "." + tmps[1];
                SuspiciousLocation sl = new SuspiciousLocation(className, Integer.parseInt(line.split(":")[1]));
                stmtList.add(sl);
            }
            logger.info(String.format("The total suspicious statements: %d", stmtList.size()));
            in.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return stmtList;
    }

    public static List<String> readTestFile(String path) {
        return readTestFile(path, true);
    }

    public static List<String> readTestFile(String path, boolean isCsv) {
        List<String> testsList = new ArrayList<>();
        int position = 1;
        if (isCsv) {
            position = 0;
        }
        try {
            final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
            String line;
            in.readLine();
            while ((line = in.readLine()) != null) {
                if (line.length() == 0)
                    logger.error("Empty line in %s", path);
                testsList.add(line.split(",")[position]);
            }
            in.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return testsList;
    }

    public static List<String> readTestMethodFile(String path) {
        List<String> testsList = new ArrayList<>();
        try {
            final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
            String line;
            in.readLine();
            while ((line = in.readLine()) != null) {
                if (line.length() == 0)
                    logger.error("Empty line in %s", path);
                testsList.add(line.split(",")[1]);
            }
            in.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return testsList;
    }

    public static void writeMatrixFile(List<String> bitSets, List<String> testList, List<String> stmtList) {
        FileUtil.writeLinesToFile(Globals.testListPath, testList);
        FileUtil.writeLinesToFile(Globals.stmtListPath, stmtList);
        FileUtil.writeLinesToFile(Globals.coveragePath, bitSets);
    }

    public static List<Pair<List<Integer>, String>> readMatrixFile(String path, int specSize, List<String> testsList) {
        List<Pair<List<Integer>, String>> matrixList = new ArrayList<>();
        try {
            final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
            String line;
            int cnt = 0;
            int unrelatedTestCnt = 0;
            while ((line = in.readLine()) != null) {
                if (cnt == 0) {
                    if (line.replace(" ", "").length() != (specSize + 1)) {
                        String str = String.format("line length(): {}, total stmts size + 1: {} + 1. They are not consistent. EXIT now.\n", line.replace(" ", "").length(), specSize);
                        FileUtil.writeToFile(str);
                        logger.error(str);
                        System.exit(0);
                    }
                }
                if (line.length() == 0)
                    logger.error(String.format("Empty line in %s", path));
                String testResult = line.substring(line.length() - 1);
                List<Integer> coveredStmtIndexList = new ArrayList<>();
                String coverage = line.replace(" ", "");
                int index = -1;
                while ((index = coverage.indexOf("1", index + 1)) >= 0) {
                    coveredStmtIndexList.add(index);
                }
                if (coveredStmtIndexList.size() == 0) {
                    unrelatedTestCnt++;
                }
                if (testResult.equals("+")) {
                    totalPassedTests += 1;
                } else if (testResult.equals("-")) {
                    totalFailedTests += 1;
                    FileUtil.writeToFile(String.format("[readMatrixFile] [Matrix Simplification] failed method (index: %d): %s\n", cnt, testsList.get(cnt)));
                    FileUtil.writeToFile(String.format("[readMatrixFile] [Matrix Simplification] Indexes of stmts covered by failed method (total number: %d): %s\n", coveredStmtIndexList.size(), coveredStmtIndexList.toString()));
                } else {
                    logger.error(String.format("Unknown testResult: %s", testResult));
                }
                cnt++;
                matrixList.add(new Pair<>(coveredStmtIndexList, testResult));
            }
            FileUtil.writeToFile(String.format("[readMatrixFile] [Matrix Simplification] the unrelated test cases: %d\n", unrelatedTestCnt));
            FileUtil.writeToFile(String.format("[readMatrixFile] [Matrix Simplification] the total test cases: %d\n", cnt));
            FileUtil.writeToFile(String.format("[readMatrixFile] [Matrix Simplification] the total stmts: %d\n", specSize));
            in.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return matrixList;
    }

    public static List<SuspiciousLocation> parseMatrixFile(String path, List<SuspiciousLocation> slSpecList, List<String> testsList, List<String> failedMethods) {
        int specSize = slSpecList.size();
        int testSize = testsList.size();
        int[][] matrix = new int[testSize][specSize + 1];
        String[] lineSplit = null;
        int totalPassedCnt = 0;
        int totalFailedCnt = 0;
        try (BufferedReader in = new BufferedReader(new FileReader(path))) {
            String line = in.readLine();
            lineSplit = line.split(" ");
            if (lineSplit[lineSplit.length - 1].equals("+")) {
                lineSplit[lineSplit.length - 1] = "2";
                totalPassedCnt++;
            } else {
                lineSplit[lineSplit.length - 1] = "-2";
                totalFailedCnt++;
                failedMethods.add(testsList.get(0));
            }
            if (lineSplit.length != (specSize + 1)) {
                String str = String.format("line length(): {}, total stmts size + 1: {} + 1. They are not consistent. EXIT now.\n", line.replace(" ", "").length(), specSize);
                FileUtil.writeToFile(str);
                logger.error("matrix-spectra size inconsistency error: {}", str);
                System.exit(0);
            }
            for (int i = 0; i < lineSplit.length; i++) {
                matrix[0][i] = Integer.valueOf(lineSplit[i]);
            }
            int cnt = 1;
            while ((line = in.readLine()) != null) {
                lineSplit = line.split(" ");
                if (lineSplit[lineSplit.length - 1].equals("+")) {
                    lineSplit[lineSplit.length - 1] = "2";
                    totalPassedCnt++;
                } else {
                    lineSplit[lineSplit.length - 1] = "-2";
                    totalFailedCnt++;
                    failedMethods.add(testsList.get(cnt));
                }
                for (int i = 0; i < lineSplit.length; i++) {
                    matrix[cnt][i] = Integer.valueOf(lineSplit[i]);
                }
                cnt++;
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        if (testSize != (totalPassedCnt + totalFailedCnt)) {
            String str = String.format("testsList.size(): %d, totalPassedCnt: %d, totalFailedCnt: %d. They are not consistent. EXIT now.\n", testSize, totalPassedCnt, totalFailedCnt);
            FileUtil.writeToFile(str);
            logger.error("matrix-test inconsistency size error: {}", str);
            System.exit(0);
        }
        List<SuspiciousLocation> slList = new ArrayList<>();
        for (int i = 0; i < specSize; i++) {
            SuspiciousLocation sl = slSpecList.get(i);
            int executedPassedCount = 0;
            int executedFailedCount = 0;
            List<Integer> coveredTestIndexList = new ArrayList<>();
            for (int j = 0; j < testSize; j++) {
                if (matrix[j][i] == 1) {
                    coveredTestIndexList.add(j);
                    if (matrix[j][specSize] == 2) {
                        executedPassedCount++;
                    } else {
                        executedFailedCount++;
                    }
                }
            }
            slList.add(new SuspiciousLocation(sl.getClassName(), sl.getLineNo(), executedPassedCount, executedFailedCount, totalPassedCnt, totalFailedCnt, coveredTestIndexList));
        }
        return slList;
    }

    public static Pair<Integer, Integer> getTieRange(int index, double bugSuspValue, List<SuspiciousLocation> suspList) {
        int begin = -1;
        int end = -1;
        for (int i = index; i >= 0; i--) {
            if (suspList.get(i).getSuspValue() == bugSuspValue) {
                begin = i;
            } else {
                break;
            }
        }
        for (int i = index; i < suspList.size(); i++) {
            if (suspList.get(i).getSuspValue() == bugSuspValue) {
                end = i;
            } else {
                break;
            }
        }
        return new Pair<Integer, Integer>(begin, end);
    }
}
package org.regminer.fl;

/**
 * @Author: sxz
 * @Date: 2023/12/27/19:34
 * @Description:
 */
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Globals {

    public static String srcJavaDir;

    public static String binJavaDir;

    public static String binTestDir;

    public static String dependencies;

    public static String jvmPath;

    public static String failedTests;

    public static String workingDir;

    public static String outputDir;

    public static ArrayList<String> depList = new ArrayList<>();

    public static List<String> oriFailedTestList;

    public static List<String> fakedPosTests = new ArrayList<>();

    public static List<String> expectedFailedTests = new ArrayList<>();

    public static String externalProjPath;

    public static String rankListPath;

    public static String coveragePath;

    public static String testListPath;

    public static String stmtListPath;

    public static String matrixPathAgain;

    public static String testListPathAgain;

    public static String rankListPathAgain;

    public static String flLogPath;

    public static Map<String, Object> outputData = new LinkedHashMap<>();

    public static String outputDataPath;
}
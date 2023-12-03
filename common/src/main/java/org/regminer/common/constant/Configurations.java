package org.regminer.common.constant;


import org.regminer.common.sql.MysqlManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configurations {


    private static final String SQL_ENABLE_KEY = "sql_enable";
    private static final String SQL_URL_KEY = "sql_url";
    private static final String USER_NAME_KEY = "username";
    private static final String PASSWD_KEY = "passwd";
    private final static String JDK_DIR = "jdk_dir";
    private final static String JDK_HOME = "jdk_home";
    private final static String JDK6 = "j6_file";
    private final static String JDK7 = "j7_file";
    private final static String JDK8 = "j8_file";
    private final static String JDK9 = "j9_file";
    private final static String JDK10 = "j10_file";
    private final static String JDK11 = "j11_file";
    private final static String JDK12 = "j12_file";
    private final static String JDK13 = "j13_file";
    private final static String JDK14 = "j14_file";
    private final static String JDK15 = "j15_file";
    private final static String JDK16 = "j16_file";
    private final static String JDK17 = "j17_file";

    public static String projectName;
    public static String rootDir;
    public static String configPath = "env.properties";
    public static String taskName = "bfc";
    public static String projectPath = rootDir + File.separator + "meta_projects" + File.separator + projectName;
    public static String metaPath = projectPath;
    public static String localProjectGit = metaPath + File.separator + ".git";
    public static String tmpFile = projectPath + File.separator + "tmp";
    public static String resultPath = projectPath + File.separator + "regression.csv";
    public static String cachePath = rootDir + File.separator + "cache";
    // extra with clone
    public static String cloneUrl = "";
    public static boolean sqlEnable = false;



    public static String jdkDir = "";
    public static String jdkHome = "";
    public static String j6File = "";
    public static String j7File = "";
    public static String j8File = "";
    public static String j9File = "";
    public static String j10File = "";
    public static String j11File = "";
    public static String j12File = "";
    public static String j13File = "";
    public static String j14File = "";
    public static String j15File = "";
    public static String j16File = "";
    public static String j17File = "";

    static {
        loadConfigurations();
    }

    private static void loadConfigurations() {
        Properties prop = new Properties();
        try (InputStream inStream = new FileInputStream(configPath)) {
            prop.load(inStream);

            // Load configuration properties
            sqlEnable = "1".equals(prop.getProperty(SQL_ENABLE_KEY));
            if (sqlEnable) {
                MysqlManager.url = prop.getProperty(SQL_URL_KEY);
                MysqlManager.name = prop.getProperty(USER_NAME_KEY);
                MysqlManager.pwd = prop.getProperty(PASSWD_KEY);
            }
            // ... load other properties ...
            jdkDir = prop.getProperty(JDK_DIR);
            jdkHome = prop.getProperty(JDK_HOME);
            j6File = jdkDir + prop.getProperty(JDK6) + jdkHome;
            j7File = jdkDir + prop.getProperty(JDK7) + jdkHome;
            j8File = jdkDir + prop.getProperty(JDK8) + jdkHome;
            j9File = jdkDir + prop.getProperty(JDK9) + jdkHome;
            j10File = jdkDir + prop.getProperty(JDK10) + jdkHome;
            j11File = jdkDir + prop.getProperty(JDK11) + jdkHome;
            j12File = jdkDir + prop.getProperty(JDK12) + jdkHome;
            j13File = jdkDir + prop.getProperty(JDK13) + jdkHome;
            j14File = jdkDir + prop.getProperty(JDK14) + jdkHome;
            j15File = jdkDir + prop.getProperty(JDK15) + jdkHome;
            j16File = jdkDir + prop.getProperty(JDK16) + jdkHome;
            j17File = jdkDir + prop.getProperty(JDK17) + jdkHome;
        } catch (IOException ex) {
            System.out.println("Error loading configuration: " + ex.getMessage());
        }
    }

    public static void updateDependentFields() {
        projectPath = rootDir + File.separator + "meta_projects" + File.separator + projectName;
        metaPath = projectPath;
        localProjectGit = metaPath + File.separator + ".git";
        tmpFile = projectPath + File.separator + "tmp";
        resultPath = projectPath + File.separator + "regression.csv";
        cachePath = rootDir + File.separator + "cache";
    }
}

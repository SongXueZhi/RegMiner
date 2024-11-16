package org.regminer.commons.constant;


import org.regminer.commons.model.OS;
import org.regminer.commons.sql.MysqlManager;
import org.regminer.commons.utils.OSUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configurations {

    private static final String SQL_ENABLE_KEY = "sql_enable";
    private static final String JENV_ENABLE_KEY = "jenv_enable";
    private static final String MONITOR_ENABLE_KEY = "open_monitor";
    private static final String SQL_URL_KEY = "sql_url";
    private static final String USER_NAME_KEY = "username";
    private static final String PASSWD_KEY = "passwd";
    private final static String JDK_DIR = "jdk_dir";
    private final static String JDK7 = "j7_file";
    private final static String JDK8 = "j8_file";
    private final static String JDK11 = "j11_file";
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
    public static String exceptionUrl;// will be updated after setting rootPath (-ws)

    // extra with clone
    public static String cloneUrl = "";
    public static boolean sqlEnable = false;
    public static String jdkDir = "";
    public static String j7File = "";
    public static String j8File = "";
    public static String j11File = "";
    public static String j17File = "";
    public static boolean jenv_enable = false;
    public static boolean open_monitor = false;
    static Properties prop = new Properties();

    public static String osName= OS.UNIX;
    static {
        loadConfigurations();
        osName = OSUtils.getOSType();
    }
    private static void loadConfigurations() {

        try (InputStream inStream = new FileInputStream(configPath)) {
            prop.load(inStream);

            // Load configuration properties
            sqlEnable = "1".equals(prop.getProperty(SQL_ENABLE_KEY));
            jenv_enable = "1".equals(prop.getProperty(JENV_ENABLE_KEY));
            open_monitor = "1".equals(prop.getProperty(MONITOR_ENABLE_KEY));

            if (sqlEnable) {
                MysqlManager.url = prop.getProperty(SQL_URL_KEY);
                MysqlManager.name = prop.getProperty(USER_NAME_KEY);
                MysqlManager.pwd = prop.getProperty(PASSWD_KEY);
            }
            // ... load other properties ...
            jdkDir = prop.getProperty(JDK_DIR);
            j7File = prop.getProperty(JDK7);
            j8File = prop.getProperty(JDK8);
            j11File = prop.getProperty(JDK11);
            j17File = prop.getProperty(JDK17);
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
        exceptionUrl = rootDir + File.separator + "exception";
    }
}

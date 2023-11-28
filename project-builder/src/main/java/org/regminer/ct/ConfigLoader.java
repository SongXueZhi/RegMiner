package org.regminer.ct;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties prop = new Properties();
    private final static String CONFIGPATH = "env.properties";

    private final static String PROJRCT_NAME = "project_name";
    private final static String PROJECTS_Dir = "projects_dir";
    private final static String PROJECT_IN = "project_in";
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


    public static String projectName = "";
    public static String projectPath = "";
    public static String projectsDir = "";
    public static String projectIn = "";
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


    public static void refresh() {
        try (InputStream inStream = new FileInputStream(CONFIGPATH)) {
            prop.load(inStream);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        projectName = prop.getProperty(PROJRCT_NAME);
        projectPath = "datas/" + projectName;
        projectsDir = prop.getProperty(PROJECTS_Dir);
        projectIn = prop.getProperty(PROJECT_IN);
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

    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.regminer.miner.start;

import org.regminer.common.constant.Configurations;
import org.regminer.miner.sql.MysqlManager;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author knightsong
 */
public class ConfigLoader {
    private final static String PROJRCT_NAME = "project_name";
    private final static String ROOT_DIR = "root_dir";
    private static  Properties prop ;
    private final static String KEY_PATH = "path";
    private final static String KEY_JAVA_HOME = "JAVA_HOME";
    private final static String CONFIGPATH = "env.properties";
    private final static String CODE_COVER = "code_cover";
    private final static String AUTO_COMPILE = "auto_compile";
    private final static String COMMAND_LINE = "command_line";
    private final static String TEST_LINE = "test_line";
    private final static String TEST_SYMBOL = "test_symbol";
    private final static String SQL_URL = "sql_url";
    private final static String USER_NAME = "username";
    private final static String PASSWD = "passwd";
    private final static String SQL_ENABLE = "sql_enable";
    private final static String SLASH = "/";
    public static String envPath = "";
    public static String projectName = "";
    public static String projectFullName = "";
    public static String organizeName = "";
    public static String rootDir = "";
    public static String compileLine = "";
    public static String testLine = "";
    public static String testSymbol = "";
    public static boolean code_cover = false;
    public static boolean auto_compile = false;
    private static String JAVA_HONE = "";

    protected static Logger logger = org.slf4j.LoggerFactory.getLogger(ConfigLoader.class);
    static {
        prop = new Properties();
        refresh();
    }
    public static void refresh() {
        try (InputStream inStream = new FileInputStream(Configurations.ROOT_DIR + File.separator + Configurations.CONFIG_PATH)) {
            prop.load(inStream);
        } catch (IOException ex) {
            logger.error("Error loading config file: {}", ex.getMessage());
        }
        envPath = prop.getProperty(KEY_PATH);
        JAVA_HONE = prop.getProperty(KEY_JAVA_HOME);
        projectFullName = prop.getProperty(PROJRCT_NAME);

        if (projectFullName.contains(SLASH)){
            String[] items = projectFullName.split("/");
            projectName = items[1];
            organizeName =items[0];
        }
        rootDir = prop.getProperty(ROOT_DIR);
        compileLine = prop.getProperty(COMMAND_LINE);
        testLine = prop.getProperty(TEST_LINE);
        testSymbol = prop.getProperty(TEST_SYMBOL);
        envPath = JAVA_HONE + ";" + envPath;

        if (prop.getProperty(SQL_ENABLE).equals("0")) {
            Configurations.sql_enable = false;
        } else {
            MysqlManager.URL = prop.getProperty(SQL_URL);
            MysqlManager.NAME = prop.getProperty(USER_NAME);
            MysqlManager.PWD = prop.getProperty(PASSWD);
        }

        if (prop.getProperty(CODE_COVER).equals("1")) {
            code_cover = true;
        }
        if (prop.getProperty(AUTO_COMPILE).equals("1")) {
            auto_compile = true;
        }
    }
}

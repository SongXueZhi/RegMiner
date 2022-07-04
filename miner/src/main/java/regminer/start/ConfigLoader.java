/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package regminer.start;

import regminer.constant.Conf;
import regminer.sql.MysqlManager;
import regminer.utils.FileUtilx;

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
    private static final Properties prop = new Properties();
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

    public static void refresh() {
        String pathx = CONFIGPATH;
        //###########################
        // 使用Eclipse发布release版本时候请解除注释
        //###########################
		String path = System.getProperty("java.class.path");
		int firstIndex = path.lastIndexOf(System.getProperty("path.separator")) + 1;
		int lastIndex = path.lastIndexOf(File.separator) + 1;
		path = path.substring(firstIndex, lastIndex);
		FileUtilx.log("env.pro " + path);
		pathx = path + File.separator + CONFIGPATH;
		FileUtilx.log("env.pro " + pathx);
        //#########################
        // release code end
        //#########################
        try (InputStream inStream = new FileInputStream(new File(pathx))) {
            prop.load(inStream);
        } catch (IOException ex) {
            FileUtilx.log(ex.getMessage());
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
            Conf.sql_enable = false;
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pre_compile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author knightsong
 */
public class ConfigLoader {
	final static Class<ConfigLoader> here = ConfigLoader.class;
	private final static String PROJRCT_NAME = "project_name";
	private final static String ROOT_DIR = "root_dir";
	private static Properties prop = new Properties();
	private final static String KEY_PATH = "path";
	private final static String KEY_JAVA_HOME = "JAVA_HOME";
	private static String JAVA_HONE = "";
	public static String envPath = "";
	public static String projectName = "";
	public static String rootDir = "";
	private final static String CONFIGPATH = "env.properties";
	private final static String COMMAND_LINE = "command_line";
	private final static String TEST_LINE = "test_line";
	private final static String TEST_SYMBOL = "test_symbol";
	public static String compileLine = "";
	public static String testLine = "";
	public static String testSymbol = "";

	public static void refresh() {
//		发布release版本时候请解除注释
//		String path = System.getProperty("java.class.path");
//		int firstIndex = path.lastIndexOf(System.getProperty("path.separator")) + 1;
//		int lastIndex = path.lastIndexOf(File.separator) + 1;
//		path = path.substring(firstIndex, lastIndex);
//		System.out.println("env.pro " + path);
		String pathx = CONFIGPATH;
//		pathx = path + File.separator + CONFIGPATH;
		try (InputStream inStream = new FileInputStream(new File(pathx));) {
			prop.load(inStream);
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
		envPath = prop.getProperty(KEY_PATH);
		JAVA_HONE = prop.getProperty(KEY_JAVA_HOME);
		projectName = prop.getProperty(PROJRCT_NAME);
		rootDir = prop.getProperty(ROOT_DIR);
		compileLine = prop.getProperty(COMMAND_LINE);
		testLine = prop.getProperty(TEST_LINE);
		testSymbol = prop.getProperty(TEST_SYMBOL);
		envPath = JAVA_HONE + ";" + envPath;
	}

//    public  static void setConfig(String javaPath,String toolPath,String gitPath){
//        FileOutputStream outputStream  =null;
//        try {
//            outputStream = new FileOutputStream(new File(CONFIGPATH));
//            prop.setProperty(KEY_JDK, javaPath);
//            prop.setProperty(KEY_BUILD_TOOL, toolPath);
//            prop.setProperty(KEY_GIT, gitPath);
//            prop.store(outputStream,"");
//        } catch (Exception ex) {
//            Logger.getLogger(ConfigLoader.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            try {
//                outputStream.close();
//            } catch (IOException ex) {
//                Logger.getLogger(ConfigLoader.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//    }
}

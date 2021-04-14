/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exec;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author knightsong
 */
public class Configs {

	public final static String PROJRCT_NAME = "project_name";
	public final static String ROOT_DIR = "root_dir";
	private static Properties prop = new Properties();
	private final static String KEY_PATH = "path";
	private final static String KEY_JAVA_HOME = "JAVA_HOME";
	private static String JAVA_HONE = "";
	public static String envPath = "";
	public static String projectName = "";
	public static String rootDir = "";
	private final static String CONFIGPATH = "env.properties";

	public static void refresh() {
		try (InputStream inStream = new FileInputStream(new File(CONFIGPATH));) {
			prop.load(inStream);
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
		envPath = prop.getProperty(KEY_PATH);
		JAVA_HONE = prop.getProperty(KEY_JAVA_HOME);
		projectName = prop.getProperty(PROJRCT_NAME);
		rootDir = prop.getProperty(ROOT_DIR);
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
//            Logger.getLogger(Configs.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            try {
//                outputStream.close();
//            } catch (IOException ex) {
//                Logger.getLogger(Configs.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//    }
}

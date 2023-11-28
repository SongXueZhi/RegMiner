package org.regminer.miner.constant;

import org.regminer.miner.model.ProjectEntity;
import org.regminer.miner.start.ConfigLoader;

import java.io.File;

public class Configurations {

	public static String PROJECT_NAME = ConfigLoader.projectName;
	public static String ROOT_DIR = ConfigLoader.rootDir;
	public static String CONFIG_PATH = "env.properties";
	public static String TASK_NAME = "bfc";

	public static String PROJECT_PATH = ROOT_DIR +File.separator+"meta_projects"+ File.separator + PROJECT_NAME;
	public static String META_PATH = PROJECT_PATH;
	public static String TMP_FILE = PROJECT_PATH + File.separator + "tmp";

	public static String LOCAL_PROJECT_GIT = META_PATH + File.separator + ".git";

	public static String CACHE_PATH = ROOT_DIR + File.separator + "cache";

	public static String RESULT_Path = PROJECT_PATH + File.separator + "regression.csv";

	// extra with clone
	public static String CLONE_URL = "";

}

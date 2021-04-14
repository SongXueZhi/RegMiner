package constant;

import java.io.File;

import exec.Configs;

public class Conf {

	// basic config
	static {
		Configs.refresh();
	}

	public final static String PROJRCT_NAME = "fastjson";
	public final static String ROOT_DIR = "/home/sxz/Documents/pcode/";

	public static String PROJECT_PATH = ROOT_DIR + File.separator + PROJRCT_NAME;
	public static String META_PATH = PROJECT_PATH + File.separator + "meta/";

	public static String LOCAL_PROJECT_GIT = META_PATH + File.separator + ".git";

	public static String CACHE_PATH = PROJECT_PATH + File.separator + "cache/";

	public static String LOG_Path = PROJECT_PATH + File.separator + "log.txt";

	// extra with clone
	public static String CLONE_URL = "";

	// config for szz
	public static String issueList = "";
	// test migrate File
	public static String BIC_SET_PATH = "bic_set_file";

}

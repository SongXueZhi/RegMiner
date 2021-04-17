package constant;

import java.io.File;

import exec.Configs;

public class Conf {

	// basic config
	static {
		Configs.refresh();
	}

	public static String PROJRCT_NAME = Configs.projectName;
	public static String ROOT_DIR = Configs.rootDir;

	public static String compileLine = Configs.compileLine;
	public static String methodClassLinkSymbolForTest = Configs.testSymbol;
	public static String testLine = Configs.testLine;

	public static String PROJECT_PATH = ROOT_DIR + File.separator + PROJRCT_NAME;
	public static String META_PATH = PROJECT_PATH + File.separator + "meta/";
	public static String TMP_FILE = PROJECT_PATH + File.separator + "tmp/";

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

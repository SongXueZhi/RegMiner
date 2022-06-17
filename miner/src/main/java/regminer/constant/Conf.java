package regminer.constant;

import regminer.model.ProjectEntity;
import regminer.start.ConfigLoader;

import java.io.File;

public class Conf {

	// basic config
	static {
		ConfigLoader.refresh();
	}

	public static String PROJRCT_NAME = ConfigLoader.projectName;
	public static String ROOT_DIR = ConfigLoader.rootDir;

	public static String compileLine = ConfigLoader.compileLine;
	public static String methodClassLinkSymbolForTest = ConfigLoader.testSymbol;
	public static String testLine = ConfigLoader.testLine;

	public static String PROJECT_PATH = ROOT_DIR + File.separator + PROJRCT_NAME;
	public static String META_PATH = PROJECT_PATH + File.separator + "meta";
	public static String TMP_FILE = PROJECT_PATH + File.separator + "tmp";

	public static String LOCAL_PROJECT_GIT = META_PATH + File.separator + ".git";

	public static String CACHE_PATH = PROJECT_PATH + File.separator + "cache";

	public static String RESULT_Path = PROJECT_PATH + File.separator + "regression.csv";

	// extra with clone
	public static String CLONE_URL = "";

	// config for szz
	public static String issueList = "";
	// test migrate File
	public static String BIC_SET_PATH = "bic_set_file";

	public static boolean code_cover =ConfigLoader.code_cover;

	public static boolean auto_compile =ConfigLoader.auto_compile;

	public static boolean sql_enable =true;

	public static ProjectEntity projectEntity;
}

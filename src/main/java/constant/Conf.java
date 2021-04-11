package constant;

import java.io.File;

public class Conf {

	// basic config

	public final static String PROJRCT_NAME = "jodatime";
	public final static String ROOT_DIR = "/home/sxz/Documents/pcode/";

	public final static String PROJECT_PATH = ROOT_DIR + File.separator + PROJRCT_NAME;
	public final static String META_PATH = PROJECT_PATH + File.separator + "meta/";

	public final static String LOCAL_PROJECT_GIT = META_PATH + File.separator + ".git";

	public final static String CACHE_PATH = PROJECT_PATH + File.separator + "cache/";

	public final static String LOG_Path = PROJECT_PATH + File.separator + "log.txt";

	// extra with clone
	public final static String CLONE_URL = "";

	// config for szz
	public final static String issueList = "";
	// test migrate File
	public final static String BIC_SET_PATH = "bic_set_file";

}

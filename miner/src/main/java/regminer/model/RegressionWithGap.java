package regminer.model;

public class RegressionWithGap extends Regression{
    /**
     * @param bugId
     * @param bfcId
     * @param buggyId
     * @param bicId
     * @param workId
     * @param bfcDirPath
     * @param buggyDirPath
     * @param bicDirPath
     * @param workDirPath
     * @param testCase
     */
    public RegressionWithGap(String bugId, String bfcId, String buggyId, String bicId, String workId, String bfcDirPath, String buggyDirPath, String bicDirPath, String workDirPath, String testCase) {
        super(bugId, bfcId, buggyId, bicId, workId, bfcDirPath, buggyDirPath, bicDirPath, workDirPath, testCase);
    }
}

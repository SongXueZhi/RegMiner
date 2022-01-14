package regminer.miner.migrate;

import org.jetbrains.annotations.NotNull;
import regminer.constant.Conf;
import regminer.exec.TestExecutor;
import regminer.finalize.SycFileCleanup;
import regminer.model.PotentialRFC;
import regminer.model.Regression;
import regminer.model.RelatedTestCase;
import regminer.model.TestFile;
import regminer.utils.FileUtilx;

import java.io.File;
import java.util.*;

public class BICFinder {
    private final static String dataPath = "results/fix_and_introducers_pairs.json";
    final int level = 0;
    TestExecutor exec = new TestExecutor();
    TestCaseMigrator testMigrater = new TestCaseMigrator();
    PotentialRFC pRFC;
    int[] status; // 切勿直接访问该数组

    public BICFinder() {
    }

    // ================================
    // SZZ BLOCK CODE 方法目前只在linux上使用
    // ================================
    public Set<String> getBICSet() {
        createBICLog();
        return readBICSetFromFile();
    }

    public void createBICLog() {
        exec.exec("rm -rf issues");
        exec.exec("rm -rf results");
        exec.exec(
                "java -jar szz_find_bug_introducers-0.1.jar -i issue_list.json -r /home/sxz/Documents/meta/fastjson -d 3 -c 1");
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {

        }
    }

    public Set<String> readBICSetFromFile() {
        String originData = FileUtilx.readContentFromFile(dataPath);
        originData = originData.replace("[", "").replace("]", "").replace("\"", "").replace("\n", "");
        String[] originDataArray = originData.split(",");
        Set<String> bICSet = new HashSet<>();
        for (int i = 0; i < originDataArray.length; i++) {
            bICSet.add(originDataArray[i]);
            FileUtilx.log(originDataArray[i]);
        }
        bICSet.remove(originDataArray[0]);
        return bICSet;
    }

    // ==========================
    // SZZ BLOCK CODE END
    // ==============================

    /**
     * @param pRFC
     * @return
     */
    public Regression searchBIC(@NotNull PotentialRFC pRFC) {
        FileUtilx.log(pRFC.getCommit().getName() + " Start search");

        // 预防方法被错误的调用
        // 情况1 search时候bfc没有确定的测试用例
        List<TestFile> testSuites = pRFC.getTestCaseFiles();
        if (testSuites == null || testSuites.size() == 0) {
            FileUtilx.log("意外的错误：NOSET");
            return null;
        }
        // 情况2 BFC没有parent，没必要search
        if (pRFC.getCommit().getParentCount() <= 0) {
            FileUtilx.log("searchBIC no Parent");
            return null;
        }
        // 方法主要逻辑
        this.pRFC = pRFC;
        // 获取BFC到Origin的所有CommitID
        List<String> candidateList = revListCommand(pRFC.getCommit().getParent(0).getName());
        // 得到反转数组,即从Origin到Commit
        Collections.reverse(candidateList);
        String[] arr = candidateList.toArray(new String[candidateList.size()]);
        // 针对每一个BFC使用一个status数组记录状态，测试过的不再测试
        status = new int[arr.length];
        for (int i = 0; i < status.length; i++) {
            status[i] = -2000;
        }
        // recursionBinarySearch(arr, 1, arr.length - 1);//乐观二分查找，只要不能编译，就往最新的时间走
        int a = optimisticBinarySearch(arr, 1, arr.length - 1);
        // 处理search结果
        String bfcId = pRFC.getCommit().getName();
        File bfcFile = new File(Conf.CACHE_PATH + File.separator + bfcId);

        //handle hit result
        if (a >= 0) {
            String working = "";
            String bic = "";
            working = arr[a];
            bic = arr[a + 1];
            FileUtilx.log("regression+1");
            // 如果是regression组合一下需要记录的相关数据
            // 顺便恢复一下exec的目录，防止exec正在的目录已被删除
            exec.setDirectory(new File(Conf.PROJECT_PATH));
            String testcaseString = combinedRegressionTestResult();
            String bfcpId = pRFC.getBuggyCommitId();
            new SycFileCleanup().cleanDirectory(bfcFile);
            return new Regression(Conf.PROJRCT_NAME + "_" + bfcId, bfcId, bfcpId, bic, working, testcaseString);
        }
        exec.setDirectory(new File(Conf.PROJECT_PATH));
        new SycFileCleanup().cleanDirectory(bfcFile);
        return null;
    }


    /**
     * 此方法组合regression的最终测试用力文本
     *
     * @return
     */
    public String combinedRegressionTestResult() {
        StringJoiner sj = new StringJoiner(";", "", "");
        for (TestFile tc : pRFC.getTestCaseFiles()) {
            Map<String, RelatedTestCase> methodMap = tc.getTestMethodMap();
            if (methodMap == null) {
                continue;
            }
            for (Iterator<Map.Entry<String, RelatedTestCase>> it = methodMap.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, RelatedTestCase> entry = it.next();
                String testCase = tc.getQualityClassName() + Conf.methodClassLinkSymbolForTest
                        + entry.getKey().split("[(]")[0];
                sj.add(testCase);
            }
        }
        return sj.toString();
    }

    public int getTestResult(String bic, int index) {
        int statu = -2000;
        if (status[index] != -2000) {
            statu = status[index];
        } else {
            statu = test(bic, index);
        }
        return statu;
    }

    public int test(String bic, int index) {
        try {
            int result = testMigrater.migrate(pRFC, bic);
            status[index] = result;
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1000;
    }

    // XXX:optimistic binary search
    public int optimisticBinarySearch(String[] arr, int low, int high) {

        if (low > high) {
            FileUtilx.log("search fal");
            return -1;
        }

        int middle = (low + high) / 2; // 初始中间位置

        int a = getTestResult(arr[middle], middle);
        boolean result = a == TestCaseMigrator.FAL;
        int b = getTestResult(arr[middle - 1], middle);
        boolean result1 = b == TestCaseMigrator.PASS;
        if (result && result1) {
            FileUtilx.log("regression+1");
            return middle - 1;
        }
        if (result) {
            // 测试用例不通过往左走
            return optimisticBinarySearch(arr, low, middle - 1);

        } else {
            return optimisticBinarySearch(arr, middle + 1, high);
        }
    }


    public List<String> revListCommand(String commitId) {
        exec.setDirectory(new File(Conf.META_PATH));
        exec.runCommand("git checkout -f master");
        return exec.runCommand("git rev-list " + commitId);
    }
}

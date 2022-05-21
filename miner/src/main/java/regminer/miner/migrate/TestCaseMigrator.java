package regminer.miner.migrate;

import regminer.constant.Conf;
import regminer.model.MigrateItem.MigrateFailureType;
import regminer.model.PotentialRFC;
import regminer.model.RelatedTestCase;
import regminer.model.TestFile;
import regminer.utils.FileUtilx;

import java.io.File;
import java.util.*;

/**
 * @author sxz
 */
public class TestCaseMigrator extends Migrator {
    public final static int PASS = 0;
    public final static int FAL = 1;
    public final static int CE = -1;
    public final static int UNRESOLVE = -2;


    public void migrate( PotentialRFC pRFC,  Set<String> bicSet) {
        FileUtilx.log("Time index: "+pRFC.getCommit().getName());
        for (String bic : bicSet) {
            try {
                migrate(pRFC, bic);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int migrate( PotentialRFC pRFC, String bic) throws Exception {
        FileUtilx.log("bic:" + bic);
        File bicDirectory = checkout(pRFC.getCommit().getName(), bic, "bic");
        pRFC.fileMap.put(bic, bicDirectory);
        mergeTwoVersion_BaseLine(pRFC,bicDirectory);
        // 编译
        if (compile(bicDirectory, true)) {
            int a = testSuite(bicDirectory, pRFC.getTestCaseFiles());
            return a;
        } else {
            FileUtilx.log(" CE ");
            return CE;
        }
    }
    public int testClass(TestFile testFile) throws Exception {
        MigrateFailureType type = exec.execTestWithResult(Conf.testLine + testFile.getQualityClassName());
        FileUtilx.log("try test class");
        if (type == MigrateFailureType.NONE) {
            FileUtilx.log("FAL");
            return FAL;
        }
        if (type == MigrateFailureType.TESTSUCCESS) {
            FileUtilx.log("PASS");
            return PASS;
        }
        FileUtilx.log("UNRESOLVE");
        return UNRESOLVE;
    }
    public boolean compile(File file, boolean record) throws Exception {
        exec.setDirectory(file);
        return exec.execBuildWithResult(Conf.compileLine, record);
    }

    public int testSuite(File file,  List<TestFile> testSuites) throws Exception {
        exec.setDirectory(file);
        StringJoiner sj = new StringJoiner(";", "[", "]");
        Iterator<TestFile> iterator = testSuites.iterator();
        boolean result1 = false;
        boolean result = false;
        while (iterator.hasNext()) {
            TestFile testSuite = iterator.next();
            int res = testBFCPMethod(testSuite, sj);
            if (res == PASS) {
                result1 = true;
            }
            if (res == FAL) {
                result = true;
            }

        }

        FileUtilx.log("Test bic " + sj);
        if (result1) {
            return PASS;
        } else if (result) {
            return FAL;
        } else {
            return UNRESOLVE;
        }
    }

    public int testBFCPMethod( TestFile testSuite, StringJoiner sj) throws Exception {
        boolean result = false;
        boolean result1 = false;
        boolean result2 = false;
        Map<String, RelatedTestCase> methodMap = testSuite.getTestMethodMap();
        for (Iterator<Map.Entry<String, RelatedTestCase>> it = methodMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, RelatedTestCase> entry = it.next();
            String testCase = testSuite.getQualityClassName() + Conf.methodClassLinkSymbolForTest
                    + entry.getKey().split("[(]")[0];
            MigrateFailureType type = exec.execTestWithResult(Conf.testLine + testCase);
            sj.add(testCase + ":" + type.getName());

            if (type == MigrateFailureType.NONE) {
                result = true;
            }
            if (type == MigrateFailureType.TESTSUCCESS) {
                result1 = true;
            }
        }

        if (result1) {
            FileUtilx.log("PASS");
            return PASS;
        }
        if (result) {
            FileUtilx.log("FAL");
            return FAL;
        }
        FileUtilx.log("UNRESOLVE");
        return UNRESOLVE;
    }


}

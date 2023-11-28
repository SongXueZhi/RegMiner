package org.regminer.miner.migrate;

import org.jetbrains.annotations.NotNull;
import org.regminer.miner.constant.Configurations;
import org.regminer.miner.model.MigrateItem;
import org.regminer.miner.model.PotentialBFC;
import org.regminer.miner.model.RelatedTestCase;
import org.regminer.miner.model.TestFile;
import org.regminer.miner.utils.FileUtilx;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @author sxz
 */
public class TestCaseMigrator extends Migrator {
    public final static int PASS = 0;
    public final static int FAL = 1;
    public final static int CE = -1;
    public final static int UNRESOLVE = -2;

    public int migrate(@NotNull PotentialBFC pRFC, String bic) throws Exception {
        File bicDirectory = checkout(pRFC.getCommit().getName(), bic, "bic");
        pRFC.fileMap.put(bic, bicDirectory);
        mergeTwoVersion_BaseLine(pRFC,bicDirectory);
        // 编译
        if (compile(bicDirectory, true)) {
            return testSuite(bicDirectory, pRFC.getTestCaseFiles());
        } else {
            return CE;
        }
    }
    public int testClass(TestFile testFile) throws Exception {
        MigrateItem.MigrateFailureType type = exec.execTestWithResult(Configurations.testLine + testFile.getQualityClassName());
        FileUtilx.log("try test class");
        if (type == MigrateItem.MigrateFailureType.NONE) {
            return FAL;
        }
        if (type == MigrateItem.MigrateFailureType.TESTSUCCESS) {
            return PASS;
        }
        return UNRESOLVE;
    }
    public boolean compile(File file, boolean record) throws Exception {
        exec.setDirectory(file);
        return exec.execBuildWithResult(Configurations.compileLine, record);
    }

    public int testSuite(File file, @NotNull List<TestFile> testSuites) throws Exception {
        exec.setDirectory(file);
        StringJoiner sj = new StringJoiner(";", "[", "]");
        Iterator<TestFile> iterator = testSuites.iterator();
        boolean result1 = false;
        boolean result = false;
        while (iterator.hasNext()) {
            TestFile testSuite = iterator.next();
            int res = testBFCPMethod(testSuite, sj);

            // XXX:TestDenpendency block
            if (res == UNRESOLVE){
                res = testClass(testSuite);
            }
            // block end

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

    public int testBFCPMethod(@NotNull TestFile testSuite, StringJoiner sj) throws Exception {
        boolean result = false;
        boolean result1 = false;
        boolean result2 = false;
        Map<String, RelatedTestCase> methodMap = testSuite.getTestMethodMap();
        for (Iterator<Map.Entry<String, RelatedTestCase>> it = methodMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, RelatedTestCase> entry = it.next();
            String testCase = testSuite.getQualityClassName() + Configurations.methodClassLinkSymbolForTest
                    + entry.getKey().split("[(]")[0];
            MigrateItem.MigrateFailureType type = exec.execTestWithResult(Configurations.testLine + testCase);
            sj.add(testCase + ":" + type.getName());

            if (type == MigrateItem.MigrateFailureType.NONE) {
                result = true;
            }
            if (type == MigrateItem.MigrateFailureType.TESTSUCCESS) {
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

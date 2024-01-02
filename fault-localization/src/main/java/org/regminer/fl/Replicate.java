package org.regminer.fl;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Replicate {

    final static Logger logger = LogManager.getLogger(Replicate.class);

    public static void replicateTests(String testPath) {
        List<String> testResults = FileUtil.readFile(testPath);
        List<String> testMethods = new ArrayList<>();
        for (String tr : testResults) {
            testMethods.add(tr.split(",")[0]);
        }
        String realTestPath = Globals.outputDir + "/test_methods.txt";
        FileUtil.writeLinesToFile(realTestPath, testMethods, false);
        Set<String> failedMethodsAfterTest = new HashSet<>();
        for (int i = 0; i < 3; i++) {
            long startTime = System.currentTimeMillis();
            String savePath = Globals.outputDir + "/all_failed_methods_replicate.txt";
            PatchTest pt = new PatchTest(savePath, Globals.jvmPath, Globals.externalProjPath, Globals.outputDir, Globals.binJavaDir, Globals.binTestDir, Globals.dependencies, null);
            pt.configure(realTestPath, true);
            List<String> failedMethods = pt.runTests();
            System.out.format("[replication %s] failedMethods size: %s, failedMethods: %s\n", i, failedMethods.size(), failedMethods);
            failedMethodsAfterTest.addAll(failedMethods);
            Globals.outputData.put("time_cost_in_replication" + i, FileUtil.countTime(startTime));
        }
        int fakeCnt = 0;
        for (String failedMethod : failedMethodsAfterTest) {
            if (!Globals.oriFailedTestList.contains(failedMethod.split("#")[0])) {
                Globals.fakedPosTests.add(failedMethod);
                fakeCnt++;
            } else {
                Globals.expectedFailedTests.add(failedMethod);
            }
        }
        FileUtil.writeToFile(String.format("[replicateTests] fakeCnt: %s\n", fakeCnt));
        FileUtil.writeLinesToFile(Globals.outputDir + "/expected_failed_test_replicate.txt", Globals.expectedFailedTests, false);
        FileUtil.writeLinesToFile(Globals.outputDir + "/extra_failed_test_replicate.txt", Globals.fakedPosTests, false);
        testMethods.removeAll(failedMethodsAfterTest);
        FileUtil.writeLinesToFile(Globals.outputDir + "/positive_test_replicate.txt", testMethods, false);
        if (fakeCnt == failedMethodsAfterTest.size()) {
            System.err.println("[replicateTests] expected failed tests are not found. Exit now.\n");
            FileUtil.writeToFile("[replicateTests] expected failed tests are not found. Exit now.\n");
            System.exit(0);
        }
    }
}
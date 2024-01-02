package org.regminer.fl;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.gzoltar.core.GZoltar;
import com.gzoltar.core.components.Component;
import com.gzoltar.core.components.Statement;
import com.gzoltar.core.instr.testing.TestResult;
import com.gzoltar.core.spectra.Spectra;

public class FaultLocalizer {

    private String workDir = System.getProperty("user.dir");

    final static Logger logger = LogManager.getLogger(FaultLocalizer.class);

    private int totalPassed = 0;

    private int totalFailed = 0;

    private List<String> failedMethods = new ArrayList<>();

    private List<String> extraFailedMethods = new ArrayList<>();

    private List<Integer> extraFailedMethodsIndices = new ArrayList<>();

    private List<String> expectedFailedMethod = new ArrayList<>();

    private List<String> testList = new ArrayList<>();

    private List<String> testResultList = new ArrayList<>();

    private List<String> stmtList = new ArrayList<>();

    private List<SuspiciousLocation> suspList = new ArrayList<>();

    private Set<String> testClasses = new HashSet<>();

    private Set<String> srcClasses = new HashSet<>();

    private String savePath;

    public FaultLocalizer(String savePath, Set<String> testClasses, Set<String> srcClasses) {
        this.savePath = savePath;
        this.testClasses.addAll(testClasses);
        this.srcClasses.addAll(srcClasses);
    }

    public List<SuspiciousLocation> readFLResults(String flPath) {
        List<String> lines = FileUtil.readFile(flPath);
        List<SuspiciousLocation> suspList = new ArrayList<>();
        for (String line : lines) {
            String className = line.split(":")[0];
            int lineNo = Integer.parseInt(line.split(":")[1].split(",")[0]);
            double suspValue = Double.parseDouble(line.split(":")[1].split(",")[1]);
            suspList.add(new SuspiciousLocation(className, lineNo, suspValue));
        }
        return suspList;
    }

    public GZoltar runGzoltar() {
        return runGzoltar(null);
    }

    public GZoltar runGzoltar(HashSet<String> extraFailedMethods) {
        logger.info("FL starts.");
        GZoltar gz = null;
        try {
            gz = new GZoltar(Globals.workingDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        gz.setClassPaths(Globals.depList);
        gz.addTestPackageNotToExecute("junit.framework");
        gz.addTestPackageNotToExecute("org.junit");
        gz.addTestPackageNotToExecute("org.easymock");
        gz.addTestPackageNotToExecute("junit.framework.TestSuite$1#warning");
        gz.addTestPackageNotToExecute("junit.framework.TestSuite$1");
        gz.addTestPackageNotToExecute("junit.framework.TestSuite");
        for (String testClass : testClasses) {
            if (testClass.contains("junit.framework")) {
                continue;
            }
            gz.addTestToExecute(testClass);
        }
        if (extraFailedMethods != null) {
            for (String extraFailedMethod : extraFailedMethods) {
                gz.addTestNotToExecute(extraFailedMethod);
            }
        }
        gz.addPackageNotToInstrument("org.junit");
        gz.addPackageNotToInstrument("junit.framework");
        gz.addPackageNotToInstrument("org.easymock");
        for (String srcClass : srcClasses) {
            gz.addClassToInstrument(srcClass);
        }
        logger.debug("FL starts gz.run()");
        gz.run();
        logger.debug("FL ends gz.run()");
        return gz;
    }

    public void calculateSusp(GZoltar gz) {
        Spectra spectra = gz.getSpectra();
        List<TestResult> testResults = spectra.getTestResults();
        logger.info("Total tests executed: {}, total componenets (stmts) obtained: {}", testResults.size(), spectra.getNumberOfComponents());
        collectTestInfo(testResults);
        List<String> bitSets = new ArrayList<>();
        for (int index = 0; index < spectra.getComponents().size(); index++) {
            Component component = spectra.getComponents().get(index);
            Statement stmt = (Statement) component;
            String className = stmt.getClazz().getLabel();
            int lineNo = stmt.getLineNumber();
            BitSet coverage = stmt.getCoverage();
            bitSets.add(coverage.toString());
            int execPassed = 0;
            int execFailed = 0;
            List<String> execPassedMethods = new ArrayList<>();
            List<String> execFailedMethods = new ArrayList<>();
            for (int i = coverage.nextSetBit(0); i >= 0; i = coverage.nextSetBit(i + 1)) {
                if (i == Integer.MAX_VALUE) {
                    logger.error("i == Integer.MAX_VALUE now.");
                    break;
                }
                TestResult tr = testResults.get(i);
                if (tr.wasSuccessful()) {
                    execPassed++;
                    execPassedMethods.add(tr.getName());
                } else {
                    execFailed++;
                    execFailedMethods.add(tr.getName());
                }
            }
            SuspiciousLocation sl = new SuspiciousLocation(className, lineNo, execPassed, execFailed, totalPassed, totalFailed, execPassedMethods, execFailedMethods);
            suspList.add(sl);
            stmtList.add(String.format("%s:%s", className, lineNo));
        }
        Collections.sort(suspList, new Comparator<SuspiciousLocation>() {

            @Override
            public int compare(SuspiciousLocation o1, SuspiciousLocation o2) {
                return Double.compare(o2.getSuspValue(), o1.getSuspValue());
            }
        });
        FileUtil.writeToFile(savePath, "", false);
        for (SuspiciousLocation sl : suspList) {
            FileUtil.writeToFile(savePath, sl.toString() + "\n");
        }
        FileUtil.writeMatrixFile(bitSets, testResultList, stmtList);
        logger.info("FL ends.");
        Globals.outputData.put("extra_failed_methods", extraFailedMethods);
        Globals.outputData.put("expected_failed_classes", Globals.oriFailedTestList);
        Globals.outputData.put("expected_failed_methods", expectedFailedMethod);
    }

    private void collectTestInfo(List<TestResult> testResults) {
        for (int index = 0; index < testResults.size(); index++) {
            TestResult tr = testResults.get(index);
            String methodName = tr.getName();
            testList.add(methodName);
            testResultList.add(String.format("%s,%s", methodName, tr.wasSuccessful()));
            if (tr.wasSuccessful()) {
                totalPassed++;
            } else {
                totalFailed++;
                failedMethods.add(methodName);
                if (!Globals.oriFailedTestList.contains(methodName.split("#")[0])) {
                    extraFailedMethods.add(methodName);
                    extraFailedMethodsIndices.add(index);
                } else {
                    expectedFailedMethod.add(methodName);
                }
            }
        }
    }

    public List<String> getFailedMethods() {
        return failedMethods;
    }

    public void setFailedMethods(List<String> failedMethods) {
        this.failedMethods = failedMethods;
    }
}
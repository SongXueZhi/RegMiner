package org.regminer.ct.utils;

import org.regminer.common.model.PotentialBFC;
import org.regminer.ct.model.TestCaseResult;
import org.regminer.ct.model.TestResult;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Utility class for test-related operations.
 *
 * @Author: sxz
 * @Date: 2023/11/29/22:54
 */
public class TestUtils {

    /**
     * Collects test cases based on a given state filter.
     *
     * @param testResult  The test result containing the cases.
     * @param stateFilter A filter to apply on test case states.
     * @return A map of test case IDs and their results.
     */
    public static Map<String, TestCaseResult> collectTestCases(TestResult testResult,
                                                               Predicate<TestCaseResult.TestState> stateFilter) {
        return testResult.getCaseResultMap().entrySet().stream()
                .filter(entry -> stateFilter.test(entry.getValue().getState()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Removes test files from a PotentialBFC based on a specific test state.
     *
     * @param potentialBFC The potential BFC to modify.
     * @param testResult   The test results to use for filtering.
     * @param testState    The state of the test cases to remove.
     */
    public static void removeTestFilesInBFCMeet(PotentialBFC potentialBFC, TestResult testResult,
                                            List<TestCaseResult.TestState> testStateList) {
        Set<String> testCasesToRemove = collectTestCases(testResult, state -> testStateList.contains(state)).keySet();

        potentialBFC.getTestCaseFiles().removeIf(testFile -> {
            testFile.getTestMethodMap().entrySet().removeIf(
                    entry -> testCasesToRemove.contains(entry.getValue().toString())
            );
            // Return true if the test file is now empty, indicating it should be removed.
            return testFile.getTestMethodMap().isEmpty();
        });

    }

    public static void removeTestFilesInBFCNotMeet(PotentialBFC potentialBFC, TestResult testResult,
                                            TestCaseResult.TestState testState) {
        Set<String> testCasesToRemove = collectTestCases(testResult, state -> state!=testState).keySet();

        potentialBFC.getTestCaseFiles().removeIf(testFile -> {
            testFile.getTestMethodMap().entrySet().removeIf(
                    entry -> testCasesToRemove.contains(entry.getValue().toString())
            );
            // Return true if the test file is now empty, indicating it should be removed.
            return testFile.getTestMethodMap().isEmpty();
        });

    }

}

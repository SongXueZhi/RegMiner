package org.regminer.ct.utils;

import org.regminer.commons.model.PotentialBFC;
import org.regminer.ct.model.TestCaseResult;
import org.regminer.ct.model.TestResult;

import java.util.Collections;
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
        if (testResult == null || stateFilter == null) {
            return Collections.emptyMap();
        }

        return testResult.getCaseResultMap().entrySet().stream()
                .filter(entry -> stateFilter.test(entry.getValue().getState()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Removes test files in PotentialBFC based on a set of test case IDs to remove.
     *
     * @param potentialBFC The potential BFC to modify.
     * @param testCasesToRemove The test case IDs to remove.
     */
    public static void removeTestFilesBasedOnFilter(PotentialBFC potentialBFC, Set<String> testCasesToRemove) {
        if (potentialBFC == null || testCasesToRemove == null) {
            return;
        }

        potentialBFC.getTestSuiteFiles().removeIf(testFile -> {
            testFile.getTestMethodMap().entrySet().removeIf(
                    entry -> testCasesToRemove.contains(entry.getValue().toString())
            );
            return testFile.getTestMethodMap().isEmpty();
        });
    }

    /**
     * Retains test files in PotentialBFC based on a set of test case IDs to maintain.
     *
     * @param potentialBFC The potential BFC to modify.
     * @param testCasesToRetain The test case IDs to retain.
     */
    public static void retainTestFilesMatchingFilter(PotentialBFC potentialBFC, Set<String> testCasesToRetain) {
        if (potentialBFC == null || testCasesToRetain == null) {
            return;
        }

        potentialBFC.getTestSuiteFiles().removeIf(testFile -> {
            testFile.getTestMethodMap().entrySet().removeIf(
                    entry -> !testCasesToRetain.contains(entry.getValue().toString())
            );
            return testFile.getTestMethodMap().isEmpty();
        });
    }

    /**
     * Removes test files in PotentialBFC based on test states.
     *
     * @param potentialBFC The potential BFC to modify.
     * @param testResult   The test results to use for filtering.
     * @param testStateList The states of the test cases to remove or retain.
     * @param retainStates Flag to determine whether to retain (true) or remove (false) matching states.
     */
    private static void modifyTestFilesByState(PotentialBFC potentialBFC, TestResult testResult,
                                               List<TestCaseResult.TestState> testStateList, boolean retainStates) {
        if (potentialBFC == null || testResult == null || testStateList == null) {
            return;
        }

        Set<String> testCasesToModify = collectTestCases(testResult, state -> retainStates == testStateList.contains(state)).keySet();
        if (retainStates) {
            retainTestFilesMatchingFilter(potentialBFC, testCasesToModify);
        } else {
            removeTestFilesBasedOnFilter(potentialBFC, testCasesToModify);
        }
    }

    public static void removeTestFilesMatchingStates(PotentialBFC potentialBFC, TestResult testResult,
                                                     List<TestCaseResult.TestState> testStateList) {
        modifyTestFilesByState(potentialBFC, testResult, testStateList, false);
    }

    public static void retainTestFilesMatchingStates(PotentialBFC potentialBFC, TestResult testResult,
                                                        List<TestCaseResult.TestState> testStateList) {
        modifyTestFilesByState(potentialBFC, testResult, testStateList, true);
    }

}

/*
 * Copyright 2019-2024   XueZhi Song, Yun Lin and RegMiner contributors
 *
 * This file is part of RegMiner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package org.regminer.bic.api;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.regminer.bic.api.core.BICSearchStrategy;
import org.regminer.commons.constant.Configurations;
import org.regminer.commons.model.PotentialBFC;
import org.regminer.commons.tool.SycFileCleanup;
import org.regminer.commons.utils.GitUtils;
import org.regminer.ct.api.CtContext;
import org.regminer.ct.model.TestCaseResult;
import org.regminer.ct.model.TestResult;
import org.regminer.migrate.api.TestCaseMigrator;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

/**
 * @Author: sxz
 * @Date: 2022/06/09/00:11
 * @Description:
 */
public class EnhancedBinarySearch extends BICSearchStrategy {
    static final int LEVEL = 3;
    protected Logger logger = LogManager.getLogger(EnhancedBinarySearch.class);
    TestCaseResult.TestState[] testStates; // 切勿直接访问该数组
    //XXX:CompileErrorSearch block
    //all related code need involve
    int passPoint = Integer.MIN_VALUE;
    int falPoint = Integer.MAX_VALUE;
    private TestCaseMigrator testMigrator;
    private CtContext ctContext;
    private PotentialBFC pRFC;

    public EnhancedBinarySearch(TestCaseMigrator testCaseMigrator) {
        this.testMigrator = testCaseMigrator;
    }

    public void setTestMigrator(TestCaseMigrator testCaseMigrator) {
        this.testMigrator = testCaseMigrator;
    }

    public void setProjectBuilder(CtContext ctContext) {
        this.ctContext = ctContext;
    }

    @Override
    public String[] getSearchSpace(String startPoint, File coeDir) {
        // 得到反转数组,即从Origin到Commit
        List<String> candidateList = GitUtils.revListCommand(startPoint, coeDir);
        //TODO Song Xuezhi 测试确定是不是从历史到现在
        Collections.reverse(candidateList);
        return candidateList.toArray(candidateList.toArray(new String[0]));
    }

    @Override
    public String[] getSearchSpaceUntilMergeNode(String startPoint, File coeDir) {
        // 从上一个 merge commit 到 startPoint
        List<String> candidateList = GitUtils.getParentsUntilMergeNode(startPoint, coeDir);
        Collections.reverse(candidateList);
        return candidateList.toArray(candidateList.toArray(new String[0]));
    }

    private Map<String, Integer> getIndexMapping(String[] arr, String[] subArr) {
        Map<String, Integer> indexMap = new HashMap<>();

        for (int i = 0; i < arr.length; i++) {
            indexMap.put(arr[i], i);
        }

        Map<String, Integer> result = new HashMap<>();
        for (String element : subArr) {
            if (indexMap.containsKey(element)) {
                result.put(element, indexMap.get(element));
            }
        }

        return result;
    }

    private int getIndex(Map<String, Integer> commit2Idx, String commit, int defaultIdx) {
        if (commit2Idx == null) {
            return defaultIdx;
        }
        return commit2Idx.getOrDefault(commit, defaultIdx);
    }


    //该算法实际上是将git bisect的功能阉割实现了一遍，gitbisect实际上会考虑图的拓扑结构
    //这里我的考虑方式是使用拓扑排序，将提交历史变成线性结构。这是一种退而求其次的方式，但保证了真实的父子关系。
    @Override
    public Triple<String, String, Integer> search(PotentialBFC potentialBFC) {
        this.pRFC = potentialBFC;
        String bfcId = pRFC.getCommit().getName();
        File bfcFile = new File(Configurations.cachePath + File.separator + bfcId);
        try {
            passPoint = Integer.MIN_VALUE;
            // 方法主要逻辑
            String[] arr = getSearchSpace(pRFC.getCommit().getParent(0).getName(),
                    Path.of(pRFC.fileMap.get(bfcId)).toFile());
            falPoint = arr.length - 1;
            logger.info("Start search {}, search space is {}", pRFC.getCommit().getName(), arr.length);
            // 针对每一个BFC使用一个status数组记录状态，测试过的不再测试
            testStates = new TestCaseResult.TestState[arr.length];
            Arrays.fill(testStates, TestCaseResult.TestState.NOMARK);
            // recursionBinarySearch(arr, 1, arr.length - 1);//乐观二分查找，只要不能编译，就往最新的时间走
            int a = search(arr, 1, arr.length - 1, null); // 指数跳跃二分查找 XXX:CompileErrorSearch

            // 处理search结果

            // 可能是跳跃跨度过大，fal 节点与上一个节点不在一条支线上
            // 另一条支线可能没有待测功能，导致全是 CE
            if (a < 0 && passPoint < 0) {
                // 单独搜索 fal commit 的直系 parents 链，直到上一个 merge 节点
                String[] subSpace = getSearchSpaceUntilMergeNode(arr[falPoint],
                        Path.of(pRFC.fileMap.get(bfcId)).toFile());
                logger.info("start search sub space, end idx {}, commit: {}, size is {}", falPoint, arr[falPoint],
                        subSpace.length);
                Map<String, Integer> commit2Idx = getIndexMapping(arr, subSpace);
                a = search(subSpace, 1, subSpace.length - 1, commit2Idx);
            }

            //have pass but not hit regression
            if (a < 0 && passPoint >= 0) {
                if (passPoint > falPoint) {
                    falPoint = arr.length - 1;
                }
                if (passPoint < falPoint) {
                    logger.info("start searchStepByStep");
                    searchStepByStep(arr);
                }
                if (passPoint == falPoint) {
                    logger.info("Excepted! here passPoint eq falPoint");
                }
            }

            //handle hit result
            if (a >= 0 || (falPoint - passPoint) == 1) {
                String working = "";
                String bic = "";
                if (a >= 0) {
                    working = arr[a];
                    bic = arr[a + 1];
                } else if (passPoint >= 0 && falPoint - passPoint == 1) {
                    working = arr[passPoint];
                    bic = arr[falPoint];
                }
                if (working.isEmpty() && bic.isEmpty()) {
                    logger.error("work and bic eq empty");
                    return null;
                }

                new SycFileCleanup().cleanDirectory(bfcFile);
                return Triple.of(working, bic, 1);
            } else if (passPoint >= 0 && falPoint - passPoint > 1) {
                logger.info("regression+1,with gap");
                return Triple.of(arr[passPoint], arr[falPoint], falPoint - passPoint);
            }
            return null;
        } finally {
            new SycFileCleanup().cleanDirectory(bfcFile);// 删除在regression定义以外的项目文件
        }
    }


    //该算法实际上是将git bisect的功能阉割实现了一遍，gitbisect实际上会考虑图的拓扑结构
    //这里我的考虑方式是使用拓扑排序，将提交历史变成线性结构。这是一种退而求其次的方式，但保证了真实的父子关系。
    public void searchStepByStep(String[] arr) {
        int now = passPoint + 1;
        int i = 0;
        TestCaseResult.TestState result;
        while (now <= falPoint && i < 2) {
            ++i;
            result = getTestResult(arr[now], now);
            if (result == TestCaseResult.TestState.PASS) {
                passPoint = now;
            } else if (result == TestCaseResult.TestState.FAL) {
                falPoint = now;
                return;
            }
            ++now;
        }
        now = falPoint - 1;
        i = 0;
        while (now >= passPoint && i < 2) {
            ++i;
            result = getTestResult(arr[now], now);
            if (result == TestCaseResult.TestState.PASS) {
                passPoint = now;
                return;
            } else if (result == TestCaseResult.TestState.FAL) {
                falPoint = now;
            }
            --now;
        }
    }


    public TestCaseResult.TestState getTestResult(String bic, int index) {
        logger.info("index: {}, test commit : {}", index, bic);
        TestCaseResult.TestState result;
        if (testStates[index] != TestCaseResult.TestState.NOMARK) {
            result = testStates[index];
        } else {
            result = test(bic, index);
        }
        if (result == TestCaseResult.TestState.FAL && index < falPoint) {
            falPoint = index;
        }
        if (result == TestCaseResult.TestState.PASS && index > passPoint) {
            passPoint = index;
        }
        logger.info("result: {}", result.name());
        return result;
    }


    public TestCaseResult.TestState test(String bic, int index) {
        try {
            // 找到 bfc 后会清理其他无用测试，因此这里将某一个测试结果作为最终结果大概是合理的
            TestResult testResult = testMigrator.migrateAndTest(pRFC, bic);
            if (testResult.getCaseResultMap().values().stream().anyMatch(testCaseResult -> testCaseResult.getState() == TestCaseResult.TestState.PASS)) {
                testStates[index] = TestCaseResult.TestState.PASS;
            } else if (testResult.getCaseResultMap().values().stream().anyMatch(testCaseResult -> testCaseResult.getState() == TestCaseResult.TestState.FAL)) {
                testStates[index] = TestCaseResult.TestState.FAL;
            } else if (testResult.getCaseResultMap().values().stream().anyMatch(testCaseResult -> testCaseResult.getState() == TestCaseResult.TestState.CE)) {
                testStates[index] = TestCaseResult.TestState.CE;
            } else if (testResult.getCaseResultMap().values().stream().anyMatch(testCaseResult -> testCaseResult.getState() == TestCaseResult.TestState.TE)) {
                testStates[index] = TestCaseResult.TestState.FAL;
            } else {
                testStates[index] = TestCaseResult.TestState.CE;
            }
            return testStates[index];
        } catch (Exception e) {
            e.printStackTrace();
        }
        testStates[index] = TestCaseResult.TestState.CE;
        return testStates[index];
    }


//    // XXX:git bisect
//    public int gitBisect(String[] arr, int low, int high) {
//
//        if (low > high) {
//            FileUtilx.log("search fal");
//            return -1;
//        }
//
//        int middle = (low + high) / 2; // 初始中间位置
//
//        int a = test(arr[middle], middle);
//        boolean result = a == TestCaseResult.TestState.FAL;
//
//        if (a == TestCaseResult.TestState.CE || a == TestCaseResult.TestState.UNKNOWN) {
//            return -1;
//        }
//        int b = test(arr[middle - 1], middle);
//        boolean result1 = b == TestCaseResult.TestState.PASS;
//        if (b == TestCaseResult.TestState.CE || b == TestCaseResult.TestState.UNKNOWN) {
//            return -1;
//        }
//        if (result && result1) {
//            FileUtilx.log("regression+1");
//            return middle;
//        }
//        if (result) {
//            // 测试用例不通过往左走
//            return gitBisect(arr, low, middle - 1);
//
//        } else if (result1) {
//            return gitBisect(arr, middle + 1, high);
//        } else {
//            return -1;
//        }
//    }

    /**
     * 乐观二分查找，现在已经放弃使用
     * XXX:Optimism Search
     *
     * @param arr
     * @param low
     * @param high
     * @return
     */
//    public int recursionBinarySearch(String[] arr, int low, int high) {
//
//        if (low > high) {
//            FileUtilx.log("search fal");
//            return -1;
//        }
//
//        int middle = (low + high) / 2; // 初始中间位置
//
//        int a = test(arr[middle], middle);
//        boolean result = a == TestCaseResult.TestState.FAL;
//        int b = test(arr[middle - 1], middle);
//        boolean result1 = b == TestCaseResult.TestState.PASS;
//        if (result && result1) {
//            FileUtilx.log("regression+1");
//            return middle;
//        }
//        if (result) {
//            // 测试用例不通过往左走
//            return recursionBinarySearch(arr, low, middle - 1);
//
//        } else {
//            return recursionBinarySearch(arr, middle + 1, high);
//        }
//    }


    /**
     * arr数组中的元素是bfc执行rev-list所得到的commitID数组
     * XXX:CompileErrorSearch
     *
     * @param arr
     * @param low
     * @param high
     * @return if find regression return working index
     */
    public int search(String[] arr, int low, int high, Map<String, Integer> commit2Idx) {
        // 失败条件
        if (low > high || low < 0 || high > arr.length - 1) {
            logger.info("search fal");
            return -1;
        }

        int middle = (low + high) / 2;
        // 查找成功条件
        TestCaseResult.TestState result = getTestResult(arr[middle], getIndex(commit2Idx, arr[middle], middle));

        if (result == TestCaseResult.TestState.FAL && middle - 1 >= 0
                && getTestResult(arr[middle - 1], getIndex(commit2Idx, arr[middle - 1], middle - 1)) == TestCaseResult.TestState.PASS) {
            return middle - 1;
        }
        if (result == TestCaseResult.TestState.PASS && middle + 1 < arr.length
                && getTestResult(arr[middle + 1], getIndex(commit2Idx, arr[middle + 1], middle + 1)) == TestCaseResult.TestState.FAL) {
            return middle;
        }
        // 查找策略
        if (result == TestCaseResult.TestState.CE) {
            // 指数跳跃查找
            int left = expLeftBoundary(arr, low, middle, 0, commit2Idx);

            if (left != -1 && getTestResult(arr[left], getIndex(commit2Idx, arr[left], left)) == TestCaseResult.TestState.FAL) {
                // 往附近看一眼
                if (middle - 1 >= 0 && getTestResult(arr[left - 1], getIndex(commit2Idx, arr[left - 1], left - 1)) == TestCaseResult.TestState.PASS) {
                    return left - 1;
                }
                // 左边界开始新的查找
                int a = search(arr, low, left, commit2Idx);
                if (a != -1) {
                    return a;
                }
            }
            int right = expRightBoundary(arr, middle, high, 0, commit2Idx);

            if (right != -1 && getTestResult(arr[right], getIndex(commit2Idx, arr[right], right)) == TestCaseResult.TestState.PASS) {
                // 往附近看一眼
                if (middle + 1 < arr.length && getTestResult(arr[right + 1], getIndex(commit2Idx, arr[right + 1],
                        right + 1)) == TestCaseResult.TestState.FAL) {
                    return right;
                }
                int b = search(arr, right, high, commit2Idx);
                if (b != -1) {
                    return b;
                }
            }
            logger.info("search fal");
            return -1;
        } else if (result == TestCaseResult.TestState.FAL) {
            // notest 等unresolved的情况都乐观的往右
            return search(arr, low, middle - 1, commit2Idx);// 向左
        } else {
            return search(arr, middle + 1, high, commit2Idx); // 向右
        }
    }

    public int expLeftBoundary(String[] arr, int low, int high, int index, Map<String, Integer> commit2Idx) {
        int left = high;
        TestCaseResult.TestState status;
        int pos;
        for (int i = 0; i < 18; i++) {
            if (left < low) {
                return -1;
            } else {
                pos = left - (int) Math.pow(2, i);
                if (pos < low) {
                    if (index < LEVEL) {
                        return expLeftBoundary(arr, low, left, index + 1, commit2Idx);
                    } else {
                        return -1;
                    }
                }
                left = pos;
                status = getTestResult(arr[left], getIndex(commit2Idx, arr[left], left));
                if (status != TestCaseResult.TestState.CE) {
                    return rightTry(arr, left, high, commit2Idx);
                }
            }

        }
        return -1;
    }

    public int rightTry(String[] arr, int low, int high, Map<String, Integer> commit2Idx) {
        int right = low;
        TestCaseResult.TestState status;
        int pos;
        for (int i = 0; i < 18; i++) {
            if (right > high) {
                return right;
            } else {
                pos = right + (int) Math.pow(2, i);
                if (pos > high) {
                    return right;
                }
                status = getTestResult(arr[pos], getIndex(commit2Idx, arr[pos], pos));
                if (status == TestCaseResult.TestState.CE) {
                    return right;
                } else {
                    right = pos;
                }
            }
        }
        return right;
    }

    public int leftTry(String[] arr, int low, int high, Map<String, Integer> commit2Idx) {
        int left = high;
        TestCaseResult.TestState status;
        int pos;
        for (int i = 0; i < 18; i++) {
            if (left < low) {
                return left;
            } else {
                pos = left - (int) Math.pow(2, i);
                if (pos < low) {
                    return left;
                }
                status = getTestResult(arr[pos], getIndex(commit2Idx, arr[pos], pos));
                if (status == TestCaseResult.TestState.CE) {
                    return left;
                } else {
                    left = pos;
                }
            }
        }
        return left;
    }

    public int expRightBoundary(String[] arr, int low, int high, int index, Map<String, Integer> commit2Idx) {
        int right = low;
        TestCaseResult.TestState status;
        int pos;
        for (int i = 0; i < 18; i++) {
            if (right > high) {
                return -1;
            } else {
                pos = right + (int) Math.pow(2, i);
                if (pos > high) {
                    if (index < LEVEL) {
                        return expRightBoundary(arr, right, high, index + 1, commit2Idx);
                    } else {
                        return -1;
                    }
                }
                right = pos;
                status = getTestResult(arr[right], getIndex(commit2Idx, arr[right], right));
                if (status != TestCaseResult.TestState.CE) {
                    return leftTry(arr, low, right, commit2Idx);
                }
            }
        }
        return -1;
    }

}

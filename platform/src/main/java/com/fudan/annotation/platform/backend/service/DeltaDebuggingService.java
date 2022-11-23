package com.fudan.annotation.platform.backend.service;

import com.fudan.annotation.platform.backend.entity.*;

import java.io.IOException;
import java.util.List;

public interface DeltaDebuggingService {
    /**
     * description 插入新regression
     *
     * @param regressionUuid regressionUuid
     * @param userToken      userToken
     * @param stepRange      [start step, end step]
     */
    DeltaDebugResult getDeltaDebuggingResults(String regressionUuid, String userToken, List<Integer> stepRange, List<Double> cProb, List<Integer> cProbLeftIdx2Test) throws IOException;

//    /**
//     * description 插入新regression
//     *
//     * @param regressionUuid regressionID
//     * @param userToken      userToken
//     * @param stepNum        指定跑到第几部，可以为null
//     */
//    DDStep runDeltaDebuggingByStep(String regressionUuid, String userToken, Integer stepNum);
}

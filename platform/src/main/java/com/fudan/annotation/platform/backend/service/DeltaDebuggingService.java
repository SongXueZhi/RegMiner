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
     * @param stepRange      [start step num, end step num]
     * @param revisionName   revision name
     */
    DeltaDebugResult getRunProbDD(String regressionUuid, String revisionName, String userToken, List<Integer> stepRange) throws IOException;

    /**
     * description 插入新regression
     *
     * @param regressionUuid regressionUuid
     * @param userToken      userToken
     * @param stepRange      [start step, end step]
     * @param cProb          previous step cProb
     */
    DeltaDebugResult postRunProbDDbyStep(String regressionUuid, String revisionName, String userToken, List<Integer> stepRange, List<Double> cProb, List<Integer> leftIdx2Test, List<Integer> stepTestedInx) throws IOException;
}

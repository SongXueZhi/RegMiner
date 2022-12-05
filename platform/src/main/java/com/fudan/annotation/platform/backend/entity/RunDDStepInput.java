package com.fudan.annotation.platform.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * description: Run DD step input
 *
 * @author David
 * create: 2022-11-16 09:22
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RunDDStepInput {
    private String regressionUuid;
    private String revisionName;
    private Integer startStep;
    private Integer endStep;
    private List<Double> cprob;
    private List<Integer> leftIdx2Test;
    private List<Integer> testedHunkIdx;
    private String userToken;
}

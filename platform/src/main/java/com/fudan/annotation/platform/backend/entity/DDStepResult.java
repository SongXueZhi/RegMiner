package com.fudan.annotation.platform.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * description: delta debugging step result
 *
 * @author David
 * create: 2022-11-16 09:22
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DDStepResult implements Serializable {
    private Integer stepNum;
    private String stepTestResult; // PASS || FAIL || CE
    private List<Double> cProb;
    // index tested this step
    private List<Integer> stepTestedInx;
    // index left to be tested, while 'PASS', remove not included indexes
    private List<Integer> leftIdx2Test;
}

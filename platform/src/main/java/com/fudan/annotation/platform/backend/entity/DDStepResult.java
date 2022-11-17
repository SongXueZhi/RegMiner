package com.fudan.annotation.platform.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * description:
 *
 * @author David
 * create: 2022-11-16 09:22
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DDStepResult {
    private List<Double> cProb;
    private List<Integer> cProbIdx;
    private List<HunkEntity> ccHunks;
}

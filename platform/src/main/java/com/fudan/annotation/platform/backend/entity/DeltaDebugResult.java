package com.fudan.annotation.platform.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * description: delta debug API result
 *
 * @author David
 * create: 2022-11-18 12:12
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeltaDebugResult {
    private List<HunkEntity> allHunkEntities;
    private List<DDStepResult> stepInfo;
}

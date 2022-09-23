package com.fudan.annotation.platform.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * description:
 *
 * @author David
 * create: 2022-09-23 10:44
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CriticalChangeDD {
    // bic/bfc
    int criticalChangeId;
    String revisionName;
    String regressionUuid;
    String newPath;
    String oldPath;
    int beginA;
    int beginB;
    int endA;
    int endB;
    String type;
    String tool;
}
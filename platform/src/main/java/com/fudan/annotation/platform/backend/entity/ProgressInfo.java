package com.fudan.annotation.platform.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: sxz
 * @Date: 2022/05/31/23:08
 * @Description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgressInfo {
    //project information
    private String currentProjectName;
    private String projectQueueNum;
    private int totalProjectNum;
    //times
    private String totalStartTime;
    private String projectStatTime;
    //potential regression fixing commit information
    private String totalPRFCNum;
    private int pRFCDoneNum;
    private int regressionNum;
}

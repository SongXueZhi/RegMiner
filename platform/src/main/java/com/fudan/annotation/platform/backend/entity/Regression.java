package com.fudan.annotation.platform.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * description: regression
 *
 * @author Richy
 * create: 2021-12-10 15:56
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Regression {
    private Integer Id;
    private String regressionUuid;
    private Integer regressionStatus;
    private String projectUuid;
    private String projectFullName;
    private String bugId;
    private String bfc;
    private String buggy;
    private String bic;
    private String work;
    private String testcase;
    private String descriptionTxt;
    private String keyWord;
    private List<String> bugTypeNames;
}
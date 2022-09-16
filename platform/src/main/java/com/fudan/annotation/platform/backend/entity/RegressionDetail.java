package com.fudan.annotation.platform.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

/**
 * description:
 *
 * @author Richy
 * create: 2022-02-23 20:24
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegressionDetail {

    private String regressionUuid;
    private String projectFullName;
    private String bic;
    private String bfc;
    private String testCaseName;
    private String testFilePath;
    private String descriptionTxt;
    private String bicURL;
    private String bfcURL;


    private List<ChangedFile> bfcChangedFiles;
    private List<ChangedFile> bicChangedFiles;
}
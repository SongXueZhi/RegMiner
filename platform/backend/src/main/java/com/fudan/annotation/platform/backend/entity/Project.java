package com.fudan.annotation.platform.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * description:
 *
 * @author Richy
 * create: 2022-02-22 09:54
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    private String projectUuid;
    private String organization;
    private String projectName;
    private String url;
}
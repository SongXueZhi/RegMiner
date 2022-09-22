package com.fudan.annotation.platform.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * description: comment
 *
 * @author sunyujie
 * create: 2022-09-15
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comments {
    private int commentId;
    private String accountName;
    private String regressionUuid;
    private String context;
    private Date createTime;
}

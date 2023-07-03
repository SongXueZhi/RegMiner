package com.fudan.annotation.platform.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * description: account
 *
 * @author sunyujie
 * create: 2023-06-28 15:55
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BugTypes {
    private int bugTypeId;
    private String bugTypeName;
    private Date createTime;
    private String createdBy;
}

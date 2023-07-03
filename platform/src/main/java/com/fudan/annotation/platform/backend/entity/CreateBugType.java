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
public class CreateBugType {
    private String bugTypeName;
    private String accountName;
}

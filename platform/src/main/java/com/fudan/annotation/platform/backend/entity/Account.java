package com.fudan.annotation.platform.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * description: account
 *
 * @author Richy
 * create: 2021-12-10 15:55
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    private String uuid;
    private String accountName;
    private String password;
    private String email;
    /**
     * 用户权限：
     * 0 —— 管理员
     * 1 —— 普通用户
     */
    private Integer accountRight;
}
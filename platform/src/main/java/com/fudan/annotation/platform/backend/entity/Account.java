package com.fudan.annotation.platform.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * description: account
 *
 * @author sunyujie
 * create: 2021-12-10 15:55
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private int accountId;
    private String accountName;
    private String password;
    private String email;
    private String avatar;
    /**
     * 用户权限：
     * admin —— 管理员
     * user —— 普通用户
     */
    private String role;
}
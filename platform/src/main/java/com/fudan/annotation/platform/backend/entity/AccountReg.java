package com.fudan.annotation.platform.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * description: register msg
 *
 * @author sunyujie
 * create: 2022-09-27 15:24
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountReg {
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

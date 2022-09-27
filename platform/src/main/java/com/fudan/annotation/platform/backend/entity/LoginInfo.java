package com.fudan.annotation.platform.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * description:
 *
 * @author sunyujie
 * create: 2021-12-13 19:20
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginInfo {
    private String accountName;
    private String password;
}
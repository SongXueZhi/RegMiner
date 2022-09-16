package com.fudan.annotation.platform.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * description:
 *
 * @author Richy
 * create: 2021-12-13 19:20
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginInfo {

    private String username;

    private String password;
}
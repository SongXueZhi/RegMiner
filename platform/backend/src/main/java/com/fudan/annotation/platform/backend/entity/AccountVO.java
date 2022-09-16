package com.fudan.annotation.platform.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * description: login msg
 *
 * @author Richy
 * create: 2021-12-13 19:17
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountVO {
    private String uuid;
    private String username;
    private String password;
    private Integer accountRight;
    private String token;
}
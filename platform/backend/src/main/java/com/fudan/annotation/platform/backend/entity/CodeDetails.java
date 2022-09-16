package com.fudan.annotation.platform.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * description:
 *
 * @author Richy
 * create: 2022-03-03 10:26
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeDetails {
    String regressionUuid;
    String oldCode;
    String newCode;

}
package com.fudan.annotation.platform.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

/**
 * @Author: sxz
 * @Date: 2022/06/17/17:57
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeElement {
    String filePath;
    String codeContent;
}

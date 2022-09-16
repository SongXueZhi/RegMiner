package com.fudan.annotation.platform.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * description:
 *
 * @author Richy
 * create: 2022-06-27 20:24
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HunkEntity implements Serializable {
    private int criticalChangeId;
    private  String  newPath;
    private  String  oldPath;
    private  int  beginA;
    private  int  beginB;
    private  int  endA;
    private  int  endB;
    private String type;
}
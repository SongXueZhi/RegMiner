package com.fudan.annotation.platform.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * description:
 *
 * @author Richy
 * create: 2022-06-27 21:41
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CriticalChange {
    // bic/bfc
    String revisionName;
    private List<HunkEntity> hunkEntityList;

}
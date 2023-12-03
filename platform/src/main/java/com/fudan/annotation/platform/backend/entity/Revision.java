package com.fudan.annotation.platform.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * description:
 *
 * @author Richy
 * create: 2022-02-23 20:23
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Revision {
    List<ChangedFile> changedFiles = new ArrayList<>();
    /**
     * 1个regression包含四个Revision（bic\bfc\buggy\work）
     */
    private File localCodeDir;
    private String revisionName;
    private String commitID;

    public Revision(String revisionName, String commitID) {
        this.revisionName = revisionName;
        this.commitID = commitID;
    }
}
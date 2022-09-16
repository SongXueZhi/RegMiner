package com.fudan.annotation.platform.backend.entity.file;

import com.fudan.annotation.platform.backend.entity.ChangedFile;

/**
 * description:
 *
 * @author Richy
 * create: 2022-03-07 17:25
 **/
public class TestFile extends ChangedFile {
    public Type type;
    private String qualityClassName;

    public TestFile(String newPath) {
        super(newPath);
    }
}
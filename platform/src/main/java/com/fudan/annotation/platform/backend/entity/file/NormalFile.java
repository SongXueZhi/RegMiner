package com.fudan.annotation.platform.backend.entity.file;

import com.fudan.annotation.platform.backend.entity.ChangedFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * description:
 *
 * @author Richy
 * create: 2022-03-07 17:21
 **/
public class NormalFile extends ChangedFile {
    public NormalFile(String newPath) {
        super(newPath);
    }
}
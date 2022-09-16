package com.fudan.annotation.platform.backend.util;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;

/**
 * description: provide repository
 *
 * @author Richy
 * create: 2022-02-25 09:56
 **/
public class RepositoryUtil {

    public static Repository getRepoFromLocal(File codeDir) throws Exception {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        return builder
                .readEnvironment()
                .findGitDir(codeDir)
                .build();
    }
}
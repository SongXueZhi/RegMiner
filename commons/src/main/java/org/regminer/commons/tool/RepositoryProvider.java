package org.regminer.commons.tool;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;

public class RepositoryProvider {

    public static Repository getRepoFromClone(String localPath, String cloneUrl) throws Exception {
        File client = new File(localPath);
        client.mkdir();
        try (Git result = Git.cloneRepository()
                .setURI(cloneUrl)
                .setProgressMonitor(new SimpleProgressMonitor())
                .setCloneAllBranches(true)
                .setDirectory(client)
                .call()) {
            return result.getRepository();
        }
    }

    public static Repository getRepoFromLocal(File codeDir) throws Exception {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        return builder
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir(codeDir) // scan up the file system tree
                .build();
    }

}

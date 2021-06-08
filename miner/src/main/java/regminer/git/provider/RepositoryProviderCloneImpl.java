package regminer.git.provider;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;

public class RepositoryProviderCloneImpl implements RepositoryProvider {

	private final String repoPath;
    public RepositoryProviderCloneImpl(String repoPath) {
        this.repoPath = repoPath;
    }
    public Repository get(String clientPath) throws Exception {
        File client = new File(clientPath);
        client.mkdir();
        try (Git result = Git.cloneRepository()
                .setURI(repoPath)
                .setDirectory(client)
                .call()) {
            return result.getRepository();
        }
    }
}

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;


public class RepositoryProvider {

	private String repoPath;
    public RepositoryProvider(String repoPath) {
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

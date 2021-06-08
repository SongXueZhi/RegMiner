package regminer.git.provider;

import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;

public class RepositoryProviderExistingClientImpl implements RepositoryProvider {

    @Override
    public Repository get(String clientPath) throws Exception {
        try (Repository repo = new FileRepository(clientPath)) {
            return repo;
        }
    }

}

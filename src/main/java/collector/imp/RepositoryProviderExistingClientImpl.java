package collector.imp;

import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;

import collector.api.RepositoryProvider;

public class RepositoryProviderExistingClientImpl implements RepositoryProvider {

    @Override
    public Repository get(String clientPath) throws Exception {
        try (Repository repo = new FileRepository(clientPath)) {
            return repo;
        }
    }

}

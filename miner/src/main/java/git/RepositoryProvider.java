package git;

import org.eclipse.jgit.lib.Repository;

public interface RepositoryProvider {
	Repository get(String clientPath) throws Exception;
}

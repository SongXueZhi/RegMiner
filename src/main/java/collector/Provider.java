package collector;


import collector.api.RepositoryProvider;
import collector.imp.RepositoryProviderCloneImpl;
import collector.imp.RepositoryProviderExistingClientImpl;
import constant.Configuration;

public class Provider {
	public static final int EXISITING = 0;
	public static final int CLONE = 1;

	public RepositoryProvider create(int providerType) {
		if (providerType==EXISITING) {
			return new RepositoryProviderExistingClientImpl();
		}else if (providerType == CLONE) {
			return new RepositoryProviderCloneImpl(Configuration.CLONE_URL);
		}else {
			return new RepositoryProviderExistingClientImpl();
		}
	}
}
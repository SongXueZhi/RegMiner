package regminer.git.provider;


import regminer.constant.Conf;

public class Provider {
	public static final int EXISITING = 0;
	public static final int CLONE = 1;

	public RepositoryProvider create(int providerType) {
		if (providerType==EXISITING) {
			return new RepositoryProviderExistingClientImpl();
		}else if (providerType == CLONE) {
			return new RepositoryProviderCloneImpl(Conf.CLONE_URL);
		}else {
			return new RepositoryProviderExistingClientImpl();
		}
	}
}
package org.regminer.miner.git.provider;


import org.regminer.miner.constant.Configurations;

public class Provider {
	public static final int EXISITING = 0;
	public static final int CLONE = 1;

	public RepositoryProvider create(int providerType) {
		if (providerType==EXISITING) {
			return new RepositoryProviderExistingClientImpl();
		}else if (providerType == CLONE) {
			return new RepositoryProviderCloneImpl(Configurations.CLONE_URL);
		}else {
			return new RepositoryProviderExistingClientImpl();
		}
	}
}
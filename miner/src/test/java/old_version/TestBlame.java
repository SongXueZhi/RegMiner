package old_version;

import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import regminer.git.provider.Provider;
import regminer.miner.PotentialBFCDetector;
import regminer.model.PotentialRFC;

public class TestBlame {
	PotentialRFC pRFC;
	Repository repo = null;
	Git git = null;

	@Before
	public void InitCommand() throws Exception {
		repo = new Provider().create(Provider.EXISITING).get("D:\\document\\project\\Fruits\\.regminer.git");
		git = new Git(repo);
	}

	@Test
	public void detetctPRFC() throws Exception {
		ObjectId id = repo.resolve("b0db7cfd5fb68987972e320f48cb3f47140297c0");
		PotentialBFCDetector pBFCDetector = new PotentialBFCDetector(repo, git);
		List<PotentialRFC> pRFCs = pBFCDetector.detectPotentialBFC();
		ObjectId ids = pRFCs.get(0).getCommit().getId();
		Assert.assertEquals(id, ids);

	}

}

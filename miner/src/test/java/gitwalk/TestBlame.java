package gitwalk;

import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import git.provider.Provider;
import miner.PotentialBFCDetector;
import model.PotentialRFC;

public class TestBlame {
	PotentialRFC pRFC;
	Repository repo = null;
	Git git = null;

	@Before
	public void InitCommand() throws Exception {
		repo = new Provider().create(Provider.EXISITING).get("D:\\document\\project\\Fruits\\.git");
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

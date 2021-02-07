package collector;

import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;

import model.PotentialRFC;

public class PotentialBICDetector {

	public void detectPBIC(Repository repository, Git git) throws Exception {
		PotentialBFCDetector pBFCDetector = new PotentialBFCDetector(repository, git);
		List<PotentialRFC> pRFCs = pBFCDetector.detectPotentialBFC();
		RelatedTestCaseParser rTCPaser = new RelatedTestCaseParser(repository);
		for (PotentialRFC pRFC : pRFCs) {
			rTCPaser.parseTestCases(pRFC);
			Traverler traverler = new Traverler(repository);
			traverler.getBlameGraph(pRFC);
		}

	}
}

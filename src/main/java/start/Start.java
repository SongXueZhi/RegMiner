package start;

import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;

import collector.PotentialBFCDetector;
import collector.Provider;
import collector.RelatedTestCaseParser;
import collector.migrate.TestReducer;
import model.PotentialRFC;

public class Start {

	public static void main(String[] args) throws Exception {
		long t1 = System.currentTimeMillis();
		try (Repository repo = new Provider().create(Provider.EXISITING).get(constant.Configuration.LOCAL_PROJECT)) {
			try (Git git = new Git(repo)) {
				try {
					PotentialBFCDetector pBFCDetector = new PotentialBFCDetector(repo, git);
					List<PotentialRFC> pRFCs = pBFCDetector.detectPotentialBFC();
					RelatedTestCaseParser rTCParser = new RelatedTestCaseParser(repo);
					rTCParser.parseTestSuite(pRFCs);
					TestReducer tm = new TestReducer(repo);
					// tm.migrate(pRFCs.get(1));
					float i = 0;
					float j = (float) pRFCs.size();
					for (PotentialRFC pRfc : pRFCs) {
						i++;
						tm.reducer(pRfc);
						System.out.println(i / j + "%");
					}

//					TestcaseMigartion tm = new TestcaseMigartion(repo);
//					tm.testReduce(pRFC);
				} catch (Exception ex) {

				}
			}
		}
	}

}

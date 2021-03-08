package gitwalk.collector;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.junit.Before;
import org.junit.Test;

import collector.PotentialBFCDetector;
import collector.Provider;
import collector.RelatedTestCaseParser;
import collector.migrate.BICFinder;
import collector.migrate.TestReducer;
import model.ExperResult;
import model.PotentialRFC;

public class TestTcParse {
	Repository repo = null;
	Git git = null;
	long t1;
	long t2;
	final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

	@Before
	public void InitCommand() throws Exception {
		t1 = System.currentTimeMillis();
		repo = new Provider().create(Provider.EXISITING).get("/home/sxz/Documents/meta/fastjson/.git");
		git = new Git(repo);

	}

	@Test
	public void testParse() {
		try {

			PrintStream ps = new PrintStream("/home/sxz/Desktop/result.txt");
			System.setOut(ps);
			PotentialBFCDetector pBFCDetector = new PotentialBFCDetector(repo, git);
			List<PotentialRFC> pRFCs = pBFCDetector.detectPotentialBFC();
			RelatedTestCaseParser rTCParser = new RelatedTestCaseParser(repo);
			rTCParser.parseTestSuite(pRFCs);
			TestReducer tm = new TestReducer(repo);
			// tm.migrate(pRFCs.get(1));
			float i = 0;

			float j = (float) pRFCs.size();
			Iterator<PotentialRFC> iterator = pRFCs.iterator();
			int z = 0;
			BICFinder finder = new BICFinder("fastjson");
			while (iterator.hasNext()) {
				PotentialRFC pRfc = iterator.next();
				i++;
				System.out.println(i / j + "%");
				Set<String> rTC = tm.reducer(pRfc);
				if (rTC == null) {
					iterator.remove();
				} else {
//					Map<String, SZZBFCObject> map = new HashMap<>();
//					String time = DATE_FORMAT.format(((long) pRfc.getCommit().getCommitTime()) * 1000).concat(" +0000");
//					map.put("fastJson" + z, new SZZBFCObject(time, time, pRfc.getCommit().getName(), time));
//					JSONObject object = new JSONObject();
//					object.putAll(map);
//					File file = new File("issue_list.json");
//					FileUtils.write(file, object.toJSONString());
//					Thread.sleep(1500);
//
//					testMigrater.migrate(pRfc, finder.getBICSet());
//					file.delete();
					finder.searchBIC(pRfc);
				}
			}
			System.out.println("成功" + ExperResult.numSuc + "个，共" + j + "个: " + ExperResult.numSuc / j);

//			int z = 0;
//			for (PotentialRFC bfc : pRFCs) {
//			
//			}


//		TestcaseMigartion tm = new TestcaseMigartion(repo);
//		tm.testReduce(pRFC);
		} catch (Exception ex) {

		}

	}
}

package gitwalk.collector;

import java.io.File;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

import collector.PotentialBFCDetector;
import collector.Provider;
import collector.RelatedTestCaseParser;
import collector.migrate.TestMigrator;
import model.ExperResult;
import model.PotentialRFC;
import model.SZZBFCObject;

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
			TestMigrator tm = new TestMigrator(repo);
			// tm.migrate(pRFCs.get(1));
			float i = 0;
			int z =0;
			float j = (float) pRFCs.size();
			Iterator<PotentialRFC> iterator = pRFCs.iterator();
			Map<String, SZZBFCObject> map = new HashMap<>();
			while (iterator.hasNext()) {
				PotentialRFC pRfc = iterator.next();
				i++;
				System.out.println(i / j + "%");
				Set<String> rTC = tm.reducer(pRfc);
				if (rTC == null) {
					iterator.remove();
				} else {
					z++;
					String time = DATE_FORMAT.format(((long) pRfc.getCommit().getCommitTime()) * 1000).concat(" +0000");
					map.put("FASTJSON" + z, new SZZBFCObject(time, time, pRfc.getCommit().getName(), time));
				}
			}
			JSONObject object = new JSONObject();
			object.putAll(map);
			FileUtils.write(new File("/home/sxz/Desktop/SZZUnleashed-master/examples/data/issue_list.json"),
					object.toJSONString());
			System.out.println("成功" + ExperResult.numSuc + "个，共" + j + "个: " + ExperResult.numSuc / j);
//		TestcaseMigartion tm = new TestcaseMigartion(repo);
//		tm.testReduce(pRFC);
		} catch (Exception ex) {

		}

	}
}

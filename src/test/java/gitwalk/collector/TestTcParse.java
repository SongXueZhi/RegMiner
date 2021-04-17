package gitwalk.collector;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.HashSet;
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
import collector.migrate.TestCaseDeterminer;
import constant.Conf;
import constant.ExperResult;
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
		repo = new Provider().create(Provider.EXISITING).get(Conf.LOCAL_PROJECT_GIT);
		git = new Git(repo);

	}

	@Test
	public void testParse() {

		try {
			// 设定标准输出流至文件
			PrintStream ps = new PrintStream(Conf.LOG_Path);
			System.setOut(ps);
			// 检测满足条件的BFC
			PotentialBFCDetector pBFCDetector = new PotentialBFCDetector(repo, git);
			List<PotentialRFC> pRFCs = pBFCDetector.detectPotentialBFC();
			// 工具类准备,1)测试方法查找 2)测试用例确定 3)BIC查找
			RelatedTestCaseParser rTCParser = new RelatedTestCaseParser(repo);
			TestCaseDeterminer tm = new TestCaseDeterminer(repo);
			BICFinder finder = new BICFinder();
			// 声明一些辅助变量
			int z = 0;
			float i = 0;
			float j = (float) pRFCs.size();
			Set<String> setResult = new HashSet<>();
			// 开始遍历每一个 Potential BFC
			Iterator<PotentialRFC> iterator = pRFCs.iterator();
			while (iterator.hasNext()) {
				PotentialRFC pRfc = iterator.next();
				i++;
				System.out.println(i / j + "%");
				// 解析有哪些测试方法
				rTCParser.parseTestCases(pRfc);
				// 确定被解析的方法那些是真实的测试用例
				// TODO 此处的方法和类之间的affix按照mvn的习惯用"#"连接,也没有配置子项目
				tm.determine(pRfc);
				if (pRfc.getTestCaseFiles().size() == 0) { // 找不到测试用例直接跳过
					iterator.remove();
				} else {
					// 确定测试用例之后开始查找bic
					String r = finder.searchBIC(pRfc);
					if (r != null) {
						setResult.add(pRfc.getCommit().getName() + ";" + r);
					}
				}
			}
			System.out.println("成功" + ExperResult.numSuc + "个，共" + j + "个: " + ExperResult.numSuc / j);
			System.out.println("classNotFind " + ExperResult.classNotFind + "methodNotFind " + ExperResult.methodNotFind
					+ "packageNotExits " + ExperResult.packageNotExits + "packageNotFind " + ExperResult.packageNotFind
					+ "symbolNotFind " + ExperResult.symbolNotFind + "unknow " + ExperResult.unknow + "variableNotFind "
					+ ExperResult.variableNotFind);

			for (String s : setResult) {
				System.out.println(s);
			}

//			int z = 0;
//			for (PotentialRFC bfc : pRFCs) {
//			
//			}

//		TestcaseMigartion tm = new TestcaseMigartion(repo);
//		tm.testReduce(pRFC);
		} catch (Exception ex) {
			System.out.print("");
		}

	}
//code for SZZ
//	Map<String, SZZBFCObject> map = new HashMap<>();
//	String time = DATE_FORMAT.format(((long) pRfc.getCommit().getCommitTime()) * 1000).concat(" +0000");
//	map.put("fastJson" + z, new SZZBFCObject(time, time, pRfc.getCommit().getName(), time));
//	JSONObject object = new JSONObject();
//	object.putAll(map);
//	File file = new File("issue_list.json");
//	FileUtils.write(file, object.toJSONString());
//	Thread.sleep(1500);
//
//	testMigrater.migrate(pRfc, finder.getBICSet());
//	file.delete();
}

package start;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;

import constant.Conf;
import constant.ExperResult;
import git.provider.Provider;
import miner.PotentialBFCDetector;
import miner.RelatedTestCaseParser;
import miner.migrate.BICFinder;
import miner.migrate.TestCaseDeterminer;
import model.PotentialRFC;
import monitor.ProgressMonitor;
import utils.FileUtilx;
import utils.ThreadPoolUtil;

/**
 * 
 * @author sxz
 * 方法入口
 */
public class MinerEntry {
	static Repository repo = null;
	static Git git = null;
	static LinkedList<PotentialRFC> pRFCs;
	static Set<String> setResult = new HashSet<>();

	public static void main(String[] args) throws Exception {
		final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
		repo = new Provider().create(Provider.EXISITING).get(Conf.LOCAL_PROJECT_GIT);
		git = new Git(repo);
		ProgressMonitor.load(); // 加载断点
		try {
			// 检测满足条件的BFC
			PotentialBFCDetector pBFCDetector = new PotentialBFCDetector(repo, git);
			pRFCs = (LinkedList<PotentialRFC>) pBFCDetector.detectPotentialBFC();
			ProgressMonitor.rePlan(pRFCs);
			// 开始每一个bfc所对应的bic，查找任务。
			singleThreadHandle(); // 单线程处理模式
			//mutilThreadHandle();// 多线程模式
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	public static void singleThreadHandle() throws Exception {
		// 工具类准备,1)测试方法查找 2)测试用例确定 3)BIC查找
		RelatedTestCaseParser rTCParser = new RelatedTestCaseParser(repo);
		TestCaseDeterminer tm = new TestCaseDeterminer(repo);
		BICFinder finder = new BICFinder();
		// 声明一些辅助变量
		int z = 0;
		float i = 0;
		float j = (float) pRFCs.size();

		// 开始遍历每一个 Potential BFC
		Iterator<PotentialRFC> iterator = pRFCs.iterator();
		while (iterator.hasNext()) {
			PotentialRFC pRfc = iterator.next();
			i++;
			FileUtilx.log(i / j + "%");
			// 解析有哪些测试方法
			rTCParser.parseTestCases(pRfc);
			// 确定被解析的方法那些是真实的测试用例
			// TODO 此处的方法和类之间的affix按照mvn的习惯用"#"连接,没有配置子项目
			tm.determine(pRfc);
			if (pRfc.getTestCaseFiles().size() == 0) { // 找不到测试用例直接跳过
				iterator.remove();
			} else {
				// 确定测试用例之后开始查找bic
				String r = finder.searchBIC(pRfc);
				String item = pRfc.getCommit().getName() + "," + r;
				if (r != null) {
					if (!setResult.contains(item)) {
						FileUtilx.apendResult(item);
					}
					setResult.add(item);
				}
			}
			ProgressMonitor.addDone(pRfc.getCommit().getName());
		}
		//此处log的bfc到bfc-1的数量成功率
		FileUtilx.log("成功" + ExperResult.numSuc + "个，共" + j + "个: " + ExperResult.numSuc / j);
//		FileUtilx.log("classNotFind " + ExperResult.classNotFind + "methodNotFind " + ExperResult.methodNotFind
//				+ "packageNotExits " + ExperResult.packageNotExits + "packageNotFind " + ExperResult.packageNotFind
//				+ "symbolNotFind " + ExperResult.symbolNotFind + "unknow " + ExperResult.unknow + "variableNotFind "
//				+ ExperResult.variableNotFind);
	}

	public static void mutilThreadHandle() {
		int cpuSize = ThreadPoolUtil.cpuIntesivePoolSize();
		for (int i = 0; i <= cpuSize; i++) {
			new SycTaskHandle().start();
		}
	}
	/**
	 * @author sxz
	 * 此类当前禁用
	 * 多线程模式proces无法确定工作目录
	 * 此类计划应用于CPU密集任务
	 */
	static class SycTaskHandle extends Thread {

		@Override
		public void run() {
			threadCoreTask();
		}

		public void threadCoreTask() {
			RelatedTestCaseParser rTCParser = new RelatedTestCaseParser(repo);
			TestCaseDeterminer tm = new TestCaseDeterminer(repo);
			BICFinder finder = new BICFinder();
			while (true) {
				PotentialRFC pRFC;
				synchronized (pRFCs) {
					if (pRFCs.peek() != null) {
						pRFC = pRFCs.pop(); // 获取任务
					} else {
						// 结束线程
						break;
					}
				}
				try {
					// 确定被解析的方法那些是真实的测试用例
					// TODO 此处的方法和类之间的affix按照mvn的习惯用"#"连接,也没有配置子项目
					rTCParser.parseTestCases(pRFC);
					tm.determine(pRFC);
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (pRFC.getTestCaseFiles().size() != 0) { // 找不到测试用例直接跳过
					// 确定测试用例之后开始查找bic
					String r = finder.searchBIC(pRFC);
					String item = pRFC.getCommit().getName() + "," + r;
					if (r != null) {
						if (!setResult.contains(item)) {
							FileUtilx.apendResult(item);
						}
						setResult.add(item);
					}
				}
			}
		}
	}
}

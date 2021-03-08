package collector.migrate;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import constant.Conf;
import exec.TestExecutor;
import model.PotentialRFC;
import utils.FileUtil;

public class BICFinder {
	String projectName = "fastjson";
	private final static String dataPath = "results/fix_and_introducers_pairs.json";
	TestExecutor exec = new TestExecutor();
	TestMigrater testMigrater = new TestMigrater(projectName);
	PotentialRFC pRFC;

	public BICFinder(String projectName) {

		this.projectName = projectName;
	}

	public Set<String> getBICSet() {
		createBICLog();
		return readBICSetFromFile();
	}

	public void createBICLog() {
		exec.setDirectory(new File("/home/sxz/Desktop/RegMiner"));
		exec.exec("rm -rf issues");
		exec.exec("rm -rf results");
		exec.exec(
				"java -jar szz_find_bug_introducers-0.1.jar -i issue_list.json -r /home/sxz/Documents/meta/fastjson -d 3 -c 1");
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {

		}
	}

	public Set<String> readBICSetFromFile() {
		String originData = FileUtil.readContentFromFile(dataPath);
		originData = originData.replace("[", "").replace("]", "").replace("\"", "").replace("\n", "");
		String[] originDataArray = originData.split(",");
		Set<String> bICSet = new HashSet<>();
		for (int i = 0; i < originDataArray.length; i++) {
			bICSet.add(originDataArray[i]);
			System.out.println(originDataArray[i]);
		}
		bICSet.remove(originDataArray[0]);
		return bICSet;
	}

	public void searchBIC(PotentialRFC pRFC) {
		Set<String> rt = pRFC.getTestCaseSet();
		if (rt == null || rt.size() == 0) {
			System.out.println("意外的错误：NOTESTSET");
			return;
		}
		if(pRFC.getCommit().getParentCount() <= 0) {
			System.out.println("searchBIC no Parent");
			return ;
		}
		this.pRFC = pRFC;
		List<String> candidateList = revListCommand(pRFC.getCommit().getParent(0).getName().toString());
		// 反转数组
		Collections.reverse(candidateList);
		// candidateList.stream().forEach(System.out::println);
		String[] arr = candidateList.toArray(new String[candidateList.size()]);
		recursionBinarySearch(arr, 1, arr.length - 1);
	}

	public int test(String bic) {
		try {
			return testMigrater.migrate(pRFC, bic);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1000;
	}
	public int recursionBinarySearch(String[] arr, int low, int high) {

		if (low > high) {
			System.out.println("查找失败");
			return -1;
		}

		int middle = (low + high) / 2; // 初始中间位置

		int a =test(arr[middle]);
		boolean result = (a == TestMigrater.FAL) ? true : false;
		int b = test(arr[middle - 1]);
		boolean result1 = ( b== TestMigrater.PASS) ? true : false;
		if (result && result1) {
			System.out.println("回归+1");
			return middle;
		} 
		if (result) {
			// 测试用例不通过往左走
			return recursionBinarySearch(arr, low, middle - 1);

		} else {
			return recursionBinarySearch(arr, middle + 1, high);
		}
	}

	public List<String> revListCommand(String commitId) {
		exec.setDirectory(new File(Conf.metaPath + projectName));
		exec.runCommand("git checkout -f master");
		return exec.runCommand("git rev-list " + commitId);
	}
}

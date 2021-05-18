package miner.migrate;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import constant.Conf;
import exec.TestExecutor;
import finalize.SycFileCleanup;
import model.PotentialRFC;
import model.RelatedTestCase;
import model.TestFile;
import utils.FileUtilx;

public class BICFinder {
	String projectName = "";
	private final static String dataPath = "results/fix_and_introducers_pairs.json";
	TestExecutor exec = new TestExecutor();
	TestCaseMigrater testMigrater = new TestCaseMigrater();
	PotentialRFC pRFC;
	final int level = 0;
	int[] status; // 切勿直接访问该数组

	public BICFinder() {
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
		String originData = FileUtilx.readContentFromFile(dataPath);
		originData = originData.replace("[", "").replace("]", "").replace("\"", "").replace("\n", "");
		String[] originDataArray = originData.split(",");
		Set<String> bICSet = new HashSet<>();
		for (int i = 0; i < originDataArray.length; i++) {
			bICSet.add(originDataArray[i]);
			FileUtilx.log(originDataArray[i]);
		}
		bICSet.remove(originDataArray[0]);
		return bICSet;
	}

	public String searchBIC(PotentialRFC pRFC) {
		List<TestFile> testSuites = pRFC.getTestCaseFiles();
		if (testSuites == null || testSuites.size() == 0) {
			FileUtilx.log("意外的错误：NOSET");
			return null;
		}
		if (pRFC.getCommit().getParentCount() <= 0) {
			FileUtilx.log("searchBIC no Parent");
			return null;
		}
		this.pRFC = pRFC;
		// 获取BFC到Origin的所有CommitID
		List<String> candidateList = revListCommand(pRFC.getCommit().getParent(0).getName().toString());
		// 得到反转数组,即从Origin到Commit
		Collections.reverse(candidateList);
		String[] arr = candidateList.toArray(new String[candidateList.size()]);
		// 针对每一个BFC使用一个status数组记录状态，测试过的不再测试
		status = new int[arr.length];
		for (int i = 0; i < status.length; i++) {
			status[i] = -2000;
		}
		// recursionBinarySearch(arr, 1, arr.length - 1);
		int a = search(arr, 1, arr.length - 1);
		// 处理search结果
		// TODO 重构需要方法查分
		String bfcName = pRFC.getCommit().getName();
		File bfcFile = new File(Conf.CACHE_PATH + File.separator + pRFC.getCommit().getName());
		if (a < 0) {
			new SycFileCleanup().cleanDirectory(bfcFile);
			return null;
		} else {
			exec.setDirectory(new File(Conf.PROJECT_PATH));
			StringJoiner sj = new StringJoiner(";", "", "");
			for (TestFile tc : pRFC.getTestCaseFiles()) {
				Map<String, RelatedTestCase> methodMap = tc.getTestMethodMap();
				if (methodMap == null) {
					continue;
				}
				for (Iterator<Map.Entry<String, RelatedTestCase>> it = methodMap.entrySet().iterator(); it.hasNext();) {
					Map.Entry<String, RelatedTestCase> entry = it.next();
					String testCase = tc.getQualityClassName() + Conf.methodClassLinkSymbolForTest
							+ entry.getKey().split("[(]")[0];
					sj.add(testCase);
				}
			}

			new SycFileCleanup().cleanDirectoryOnFilter(bfcFile, Arrays.asList(bfcName, arr[a + 1], arr[a]));// 删除在regression定义以外的项目文件
			return arr[a + 1] + "," + arr[a] + "," + sj.toString();
		}
	}

	public int getTestResult(String bic, int index) {
		if (status[index] != -2000) {
			return status[index];
		} else {
			return test(bic, index);
		}
	}

	public int test(String bic, int index) {
		try {
			int result = testMigrater.migrate(pRFC, bic);
			status[index] = result;
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1000;
	}

//	public int recursionBinarySearch(String[] arr, int low, int high) {
//
//		if (low > high) {
//			FileUtilx.log("查找失败");
//			return -1;
//		}
//
//		int middle = (low + high) / 2; // 初始中间位置
//
//		int a = test(arr[middle], middle);
//		boolean result = (a == TestMigrater.FAL) ? true : false;
//		int b = test(arr[middle - 1], middle);
//		boolean result1 = ( b== TestMigrater.PASS) ? true : false;
//		if (result && result1) {
//			FileUtilx.log("回归+1");
//			return middle;
//		} 
//		if (result) {
//			// 测试用例不通过往左走
//			return recursionBinarySearch(arr, low, middle - 1);
//
//		} else {
//			return recursionBinarySearch(arr, middle + 1, high);
//		}
//	}
	// if find regression return working index
	public int search(String[] arr, int low, int high) {
		// 失败条件
		if (low > high || low < 0 || high > arr.length - 1) {
			FileUtilx.log("查找失败");
			return -1;
		}

		int middle = (low + high) / 2;
		// 查找成功条件
		int statu = getTestResult(arr[middle], middle);

		if (statu == TestCaseMigrater.FAL && getTestResult(arr[middle - 1], middle - 1) == TestCaseMigrater.PASS) {
			FileUtilx.log("回归+1");
			return middle - 1;
		}
		if (statu == TestCaseMigrater.PASS && getTestResult(arr[middle + 1], middle + 1) == TestCaseMigrater.FAL) {
			FileUtilx.log("回归+1");
			return middle;
		}

		// 查找策略
		if (statu == TestCaseMigrater.CE) {
			// 指数跳跃查找
			int left = expLeftBoundry(arr, low, middle, 0);

			if (left != -1 && getTestResult(arr[left], left) == TestCaseMigrater.FAL) {
				// 往附近看一眼
				if (getTestResult(arr[left - 1], left - 1) == TestCaseMigrater.PASS) {
					return left - 1;
				}
				// 左边界开始新的查找
				int a = search(arr, low, left);
				if (a != -1) {
					return a;
				}
			}
			int right = expRightBoundry(arr, middle, high, 0);

			if (right != -1 && getTestResult(arr[right], right) == TestCaseMigrater.PASS) {
				// 往附近看一眼
				if (getTestResult(arr[right + 1], right + 1) == TestCaseMigrater.FAL) {
					return right;
				}
				int b = search(arr, right, high);
				if (b != -1) {
					return b;
				}
			}
			FileUtilx.log("查找失败");
			return -1;
		} else if (statu == TestCaseMigrater.FAL) {
			// notest 等unresolved的情况都乐观的往右
			return search(arr, low, middle - 1);// 向左
		} else {
			return search(arr, middle + 1, high); // 向右
		}
	}

	public int expLeftBoundry(String[] arr, int low, int high, int index) {
		int left = high;
		int status = 0;
		int pos = -1;
		for (int i = 0; i < 18; i++) {
			if (left < low) {
				return -1;
			} else {
				pos = left - (int) Math.pow(2, i);
				if (pos < low) {
					if (index < level) {
						return expLeftBoundry(arr, low, left, index + 1);
					} else {
						return -1;
					}
				}
				left = pos;
				status = getTestResult(arr[left], left);
				if (status != -1) {
					return rightTry(arr, left, high);
				}
			}

		}
		return -1;
	}

	public int rightTry(String[] arr, int low, int high) {
		int right = low;
		int status = 0;
		int pos = -1;
		for (int i = 0; i < 18; i++) {
			if (right > high) {
				return right;
			} else {
				pos = right + (int) Math.pow(2, i);
				if (pos > high) {
					return right;
				}
				status = getTestResult(arr[pos], pos);
				if (status == -1) {
					return right;
				} else {
					right = pos;
				}
			}
		}
		return right;
	}

	public int leftTry(String[] arr, int low, int high) {
		int left = high;
		int status = 0;
		int pos = -1;
		for (int i = 0; i < 18; i++) {
			if (left < low) {
				return left;
			} else {
				pos = left - (int) Math.pow(2, i);
				if (pos < low) {
					return left;
				}
				status = getTestResult(arr[pos], pos);
				if (status == -1) {
					return left;
				} else {
					left = pos;
				}
			}
		}
		return left;
	}

	public int expRightBoundry(String[] arr, int low, int high, int index) {
		int right = low;
		int status = 0;
		int pos = -1;
		for (int i = 0; i < 18; i++) {
			if (right > high) {
				return -1;
			} else {
				pos = right + (int) Math.pow(2, i);
				if (pos > high) {
					if (index < level) {
						return expRightBoundry(arr, right, high, index + 1);
					} else {
						return -1;
					}
				}
				right = pos;
				status = getTestResult(arr[right], right);
				if (status != -1) {
					return leftTry(arr, low, right);
				}
			}
		}
		return -1;
	}

	public List<String> revListCommand(String commitId) {
		exec.setDirectory(new File(Conf.META_PATH));
		exec.runCommand("git checkout -f master");
		return exec.runCommand("git rev-list " + commitId);
	}
}

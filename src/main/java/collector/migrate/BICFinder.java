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
	final int level = 0;
	int[] status; // 切勿直接访问该数组

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
		// 针对每一个BFC使用一个status数组记录状态，测试过的不再测试
		status = new int[arr.length];
		for (int i = 0; i < status.length; i++) {
			status[i] = -2000;
		}
		// recursionBinarySearch(arr, 1, arr.length - 1);
		search(arr, 1, arr.length - 1);
	}

	public int getTestResult(String bic, int index) {
		if (status[index] != -2000) {
			return status[index];
		} else {
			return test(bic, index);
		}
	}

	public int test(String bic,int index) {
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
//			System.out.println("查找失败");
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
//			System.out.println("回归+1");
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

	public int search(String[] arr, int low, int high) {
		// 失败条件
		if (low > high || low < 0 || high > arr.length - 1) {
			System.out.println("查找失败");
			return -1;
		}

		int middle = (low + high) / 2;
		// 查找成功条件
		int statu = getTestResult(arr[middle], middle);

		if (statu == TestMigrater.FAL && getTestResult(arr[middle - 1], middle - 1) == TestMigrater.PASS) {
			System.out.println("回归+1");
			return middle - 1;
		}
		if (statu == TestMigrater.PASS && getTestResult(arr[middle + 1], middle + 1) == TestMigrater.FAL) {
			System.out.println("回归+1");
			return middle;
		}

		// 查找策略
		if (statu == TestMigrater.CE) {
			// 指数跳跃查找
			int left = expLeftBoundry(arr, low, middle, 0);

			if(left!= -1 && getTestResult(arr[left], left) == TestMigrater.FAL) {
				int a = search(arr, low, left);
				if( a!=-1) {
					return a;
				}
			}
			int right = expRightBoundry(arr, middle, high, 0);
			
			if(right!=-1 && getTestResult(arr[right], right) == TestMigrater.PASS ) {
				int b =search(arr, right, high);
				if(b!=-1) {
					return b;
				}
			}
			System.out.println("查找失败");
			return -1;
		} else if (statu == TestMigrater.PASS) {
			return search(arr, middle + 1, high); // 向右
		} else {
			return search(arr, low, middle - 1); // 向左
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
		exec.setDirectory(new File(Conf.metaPath + projectName));
		exec.runCommand("git checkout -f master");
		return exec.runCommand("git rev-list " + commitId);
	}
}

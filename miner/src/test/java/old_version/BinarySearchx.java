package old_version;

import org.junit.Test;

public class BinarySearchx {

	public static int[] target = new int[] { -1, -1, -1, -1, 1, 0, -1, -1, 0, 0, -1, -1, -1, -1, -1, -1, -1, 1, -1, -1,
			-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	final int level = 3;

	@Test
	public void test() {
		System.out.println(target.length);
		int a = search(target, 1, target.length - 1);
		System.out.println(a);
	}

	public int search(int[] arr, int low, int high) {
		// 失败条件
		if (low > high || low < 0 || high > arr.length - 1) {
			return -1;
		}

		int middle = (low + high) / 2;
		System.out.println("index" + middle + ": " + arr[middle]);
		// 成功条件
		if (arr[middle] == 0 && arr[middle - 1] == 1) {
			return middle - 1;
		}
		// 查找策略
		if (arr[middle] == -1) {
			// zhishutiaoy
			int left = expLeftBoundry(arr, low, middle, 0);
			int a = search(arr, low, left);
			if (a != -1) {
				return a;
			} else {
				int right = expRightBoundry(arr, middle, high, 0);
				return search(arr, right, high);
			}
		} else if (arr[middle] == 1) {
			return search(arr, middle + 1, high); // 向右
		} else {
			return search(arr, low, middle - 1); // 向左
		}
	}

	public int expLeftBoundry(int[] arr, int low, int high, int index) {
		int left = high;
		int status = 0;
		int pos = 0;
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
				status = arr[left];
				System.out.println("index" + left + ": " + status);
				if (status != -1) {
					System.out.println(" left!");
					return rightTry(arr, left, high);
				}
			}

		}
		return -1;
	}

	public int rightTry(int[] arr, int low, int high) {
		int right = low;
		int status = 0;
		int pos = 0;
		for (int i = 0; i < 18; i++) {
			if (right > high) {
				System.out.println("LEFT right try end " + right);
				return right;
			} else {
				pos = right + (int) Math.pow(2, i);
				if (pos > high) {
					System.out.println("LEFT right try end " + right);
					return right;
				}
				status = arr[pos];
				System.out.println("LEFT right try" + pos + ": " + status);
				if (status == -1) {
					return right;
				} else {
					right = pos;
				}
			}
		}
		System.out.println("LEFT right try end " + right);
		return right;
	}

	public int leftTry(int[] arr, int low, int high) {
		int left = high;
		int status = 0;
		int pos = 0;
		for (int i = 0; i < 18; i++) {
			if (left < low) {
				return left;
			} else {
				pos = left - (int) Math.pow(2, i);
				if (pos < low) {
					System.out.println("RIGHT left try end " + left);
					return left;
				}
				status = arr[pos];
				System.out.println("RIGHT left try" + pos + ": " + status);
				if (status == -1) {
					return left;
				} else {
					left = pos;
				}
			}
		}
		System.out.println("RIGHT left try end " + left);
		return left;
	}

	public int expRightBoundry(int[] arr, int low, int high, int index) {
		int right = low;
		int status = 0;
		int pos = 0;
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
				status = arr[right];
				System.out.println("index" + right + ": " + status);
				if (status != -1) {
					System.out.println(" right!");
					return leftTry(arr, low, right);
				}
			}
		}
		return -1;
	}
}

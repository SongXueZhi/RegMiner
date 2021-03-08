package gitwalk;

public class BinarySearch {
	/*
	 * 循环实现二分查找算法arr 已排好序的数组x 需要查找的数-1 无法查到数据
	 */
	public static int binarySearch(int[] arr, int x) {
		int low = 0;
		int high = arr.length - 1;
		while (low <= high) {
			int middle = (low + high) / 2;
			if (x == arr[middle]) {
				return middle;
			} else if (x < arr[middle]) {
				high = middle - 1;
			} else {
				low = middle + 1;
			}
		}
		return -1;
	}

	// 递归实现二分查找
	public static int recursionBinarySearch(int[] arr, int key, int low, int high) {

		if (key < arr[low] || key > arr[high] || low > high) {
			return -1;
		}

		int middle = (low + high) / 2; // 初始中间位置
		if (arr[middle] > key) {
			// 比关键字大则关键字在左区域
			return recursionBinarySearch(arr, key, low, middle - 1);
		} else if (arr[middle] < key) {
			// 比关键字小则关键字在右区域
			return recursionBinarySearch(arr, key, middle + 1, high);
		} else {
			return middle;
		}
	}

	public static void main(String[] args) {
		int[] arr = { 6, 12, 33, 87, 90, 97, 108, 561 };
		System.out.println("循环查找：" + (binarySearch(arr, 87) + 1));
		System.out.println("递归查找" + recursionBinarySearch(arr, 561, 0, arr.length - 1));
	}
}

//package gitwalk;
//
//public class SearchDistrict {
//
//	public void search() {
//		String[] cSet = new String[] { "ce", "ce", "ce", "ce", "pass", "pass", "pass", "fal", "fal", "ce", "ce", "ce",
//				"ce", // 中间
//				"ce", "ce", "ce", "ce", "ce", "fal",
//				"fal", "fal", "fal", "fal", "fal", "fal", "fal" };
//
//	}
//
//	// 递归实现二分查找
//	public static int recursionBinarySearch(String[] arr, int low, int high) {
//
//		if (low > high) {
//			return -1;
//		}
//
//		int middle = (low + high) / 2; // 初始中间位置
//		if (arr[middle].equals("pass")) {
//			// 比关键字大则关键字在左区域
//			return recursionBinarySearch(arr, low, middle - 1);
//		} else if (arr[middle].equals("fal")) {
//			// 比关键字小则关键字在右区域
//			return recursionBinarySearch(arr, middle + 1, high);
//		} else {
//			return 0;
//			}
//	}
//
//	public static int[] search(String[] arr, int middle) {
//		int rate = 1;
////		for (int i = 1; i < (arr.length / 2); i++) {
////			index = middle - (i * rate);
////			if (!arr[index].equals("ce") && !arr[index - 1].equals("ce")) {
////				result[0]=index;
////				break;
////			}
////			if (!arr[index].equals("ce")) {
////				left = index;
////				searchRight(arr, left, middle, rate);
////			}
////		}
//		return new int[] { searchleft(arr, 1, middle, rate), searchRight(arr, middle, arr.length - 1, rate) };
//	}
//
//	public static int searchleft(String[] arr, int left, int right, int rate) {
//		int index = 0;
//
//		for (int i = 1; i < (arr.length / 2); i++) {
//			index = right - (i * rate);
//			if (!arr[index].equals("ce") && arr[index - 1].equals("ce")) {
//				return index;
//			}
//			if (!arr[index].equals("ce")) {
////				searchRight
//			}
//		}
//	}
//
//	public static int searchRight(String[] arr, int left, int right, int rate) {
//		int index = 0;
//		for (int i = 1; i < (arr.length / 2); i++) {
//			index = left + (i * rate);
//			if (!arr[index - 1].equals("ce") && arr[index].equals("ce")) {
//				return index;
//			}
//			if (!arr[index].equals("ce")) {
//				// searchleft
//			}
//		}
//		return 0;
//	}
//}

package regminer.finalize;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 
 * @author sxz
 *
 */
public class SycFileCleanup {
	/**
	 * 
	 * @param dir root path to delete
	 * @param filter don't delete filter
	 */
	public void cleanDirectoryOnFilter(File dir, List<String> filter) {
		new SycCleaner(dir, filter, true).start();
	}

	public void cleanDirectory(File dir) {
		new SycCleaner(dir, null, false).start();
	}

	static class SycCleaner extends Thread {
		File dir;
		List<String> filter;
		boolean onFilter;

		public SycCleaner(File dir, List<String> filter, boolean onFilter) {
			this.dir = dir;
			this.filter = filter;
			this.onFilter = onFilter;
		}

		@Override
		public void run() {
			if (onFilter) {
				cleanDirectoryOnFilter();
			} else {
				cleanDirectory();
			}
		}

		public void cleanDirectoryOnFilter() {
			File[] childrenArray = dir.listFiles();
			for (File file : childrenArray) {
				// 如果不在filter中则删除
				if (filter.contains(file.getName())) {
					continue;
				}
				boolean isDelete = FileUtils.deleteQuietly(file);
				if (!isDelete) {
					try {
						FileUtils.forceDelete(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		/**
		 * Note that, here I still can't delete directory in windows 10 system
		 * cause by be occupied
		 */
		public void cleanDirectory() {
			boolean isDelete = FileUtils.deleteQuietly(dir);
			if (!isDelete) {
				try {
					if(!dir.canRead()){
						dir.setWritable(true,false);
					}
					if (!dir.canWrite()){
						dir.setWritable(true,false);
					}
//					System.gc();
					FileUtils.forceDelete(dir);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

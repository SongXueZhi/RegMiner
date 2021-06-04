package finalize;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.file.PathUtils;

/**
 * 
 * @author sxz
 *
 */
public class SycFileCleanup {
	/**
	 * 
	 * @param dir
	 * @param filter
	 */
	public void cleanDirectoryOnFilter(File dir, List<String> filter) {
		new SycCleanner(dir, filter, true).start();
	}

	public void cleanDirectory(File dir) {
		new SycCleanner(dir, null, false).start();
	}

	class SycCleanner extends Thread {
		File dir;
		List<String> filter;
		boolean onFilter = false;

		public SycCleanner(File dir, List<String> filter, boolean onFilter) {
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
			File[] childsArray = dir.listFiles();
			for (File file : childsArray) {
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
					System.gc();
					FileUtils.forceDelete(dir);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

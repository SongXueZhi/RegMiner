package regminer.finalize;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * 
 * @author sxz
 *
 */
public class SycFileCleanupTest {

	@Test
	public void testCleanDirectory() {
		File file = new File("/home/sxz/Documents/pcode/fastjson/cache/ba07c7fbda7ffb4f5fad24f5d02c03794db8c7c9");
		List<String> filter = new ArrayList<>();
		filter.add("ba07c7fbda7ffb4f5fad24f5d02c03794db8c7c9");
		filter.add("58b3eb8e7531c5c3bfa1458cb78aad5d63fcfcf4");
		filter.add("a9117d9436c8f23e0928798b7ae72b69d5282155");
		SycFileCleanup syFC = new SycFileCleanup();
		syFC.cleanDirectoryOnFilter(file, filter);
	}

}

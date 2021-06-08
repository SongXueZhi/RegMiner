package regminer.utils;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;

public class GitUtil {

	public static String getContextWithFile(Repository repo, RevCommit commit, String filePath) throws Exception {
		RevWalk walk = new RevWalk(repo);
		RevTree revTree = commit.getTree();
		TreeWalk treeWalk = TreeWalk.forPath(repo, filePath, revTree);
		// 文件名错误
		if (treeWalk == null)
			return null;

		ObjectId blobId = treeWalk.getObjectId(0);
		ObjectLoader loader = repo.open(blobId);
		byte[] bytes = loader.getBytes();
		if (bytes != null)
			return new String(bytes);
		return null;

	}

}

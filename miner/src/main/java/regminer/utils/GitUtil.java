package regminer.utils;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import regminer.model.HunkEntity;

import java.util.LinkedList;
import java.util.List;

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


	public static List<HunkEntity> getHunksBetweenCommits(Repository repository, ObjectId oldCommitId, ObjectId newCommitId) {
		List<HunkEntity> result = new LinkedList<>();
		try (Git git = new Git(repository);
			 ObjectReader reader = repository.newObjectReader();
			 DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE)) {

			CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
			newTreeIter.reset(reader, newCommitId);
			CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
			oldTreeIter.reset(reader, oldCommitId);

			// finally get the list of changed files
			List<DiffEntry> diffEntries = git.diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();
			diffFormatter.setRepository(repository);

			for (DiffEntry diffEntry: diffEntries) {
				FileHeader fileHeader = diffFormatter.toFileHeader(diffEntry);
				List<? extends HunkHeader> hunkHeaders = fileHeader.getHunks();
				for (HunkHeader hunk : hunkHeaders) {
					for (Edit edit : hunk.toEditList()) {
						HunkEntity hunkEntity = new HunkEntity();
						hunkEntity.setOldPath(hunk.getFileHeader().getOldPath());
						hunkEntity.setNewPath(hunk.getFileHeader().getNewPath());
						hunkEntity.setBeginA(edit.getBeginA());
						hunkEntity.setBeginB(edit.getBeginB());
						hunkEntity.setEndA(edit.getEndA());
						hunkEntity.setEndB(edit.getEndB());
						hunkEntity.setType(edit.getType().toString());
						result.add(hunkEntity);
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return result;
	}
}

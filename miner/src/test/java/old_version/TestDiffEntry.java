package old_version;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.junit.Before;
import org.junit.Test;

import regminer.git.provider.Provider;

public class TestDiffEntry {
	Repository repo = null;
	Git git = null;

	@Before
	public void InitCommand() throws Exception {
		repo = new Provider().create(Provider.EXISITING).get("D:\\document\\project\\Fruits\\.regminer.git");
		git = new Git(repo);
	}

	@Test
	public void testBalme() throws GitAPIException, IOException {
		BlameCommand blamer = new BlameCommand(repo);
		ObjectId commitID = repo.resolve("76aa6966193286a383267251ece4e777386d43c2");
		blamer.setStartCommit(commitID);
		blamer.setFilePath("src/main/java/basket/fruits/Solution.java");
		BlameResult result = blamer.call();

		final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("YYYY-MM-dd HH:mm");
		final RawText rawText = result.getResultContents();
		for (int i = 0; i < rawText.size(); i++) {
			final PersonIdent sourceAuthor = result.getSourceAuthor(i);
			final RevCommit sourceCommit = result.getSourceCommit(i);
			System.out.println(sourceAuthor.getName()
					+ (sourceCommit != null
							? " - " + DATE_FORMAT.format(((long) sourceCommit.getCommitTime()) * 1000) + " - "
									+ sourceCommit.getName()
							: "")
					+ ": " + rawText.getString(i));
		}
	}

	@Test
	public void testDff() throws Exception {

		RevWalk walk = new RevWalk(repo);
		List<RevCommit> commitList = new ArrayList<>();
		// 获取最近提交的两次记录
		Iterable<RevCommit> commits = git.log().setMaxCount(3).call();
		for (RevCommit commit : commits) {
			commitList.add(commit);
			System.out.println(commit.getFullMessage());
			System.out.println(commit.getAuthorIdent().getWhen());

			if (commitList.size() == 3) {
				ObjectReader reader = repo.newObjectReader();
				CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
				oldTreeIter.reset(reader, commitList.get(2).getTree().getId());
				CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
				newTreeIter.reset(reader, commitList.get(1).getTree().getId());
				List<DiffEntry> diff = git.diff().setOldTree(oldTreeIter).setNewTree(newTreeIter)
						.setShowNameAndStatusOnly(true).call();

				ByteArrayOutputStream out = new ByteArrayOutputStream();
				DiffFormatter df = new DiffFormatter(out);
				// 设置比较器为忽略空白字符对比（Ignores all whitespace）
				df.setDiffComparator(RawTextComparator.WS_IGNORE_ALL);
				df.setRepository(git.getRepository());
				System.out.println("------------------------------regminer.start-----------------------------");
				// 每一个diffEntry都是第个文件版本之间的变动差异
				for (DiffEntry diffEntry : diff) {
					// 打印文件差异具体内容
					df.format(diffEntry);
					String diffText = out.toString("UTF-8");
					System.out.println(diffText);

					// 获取文件差异位置，从而统计差异的行数，如增加行数，减少行数
					FileHeader fileHeader = df.toFileHeader(diffEntry);
					List<HunkHeader> hunks = (List<HunkHeader>) fileHeader.getHunks();
					int addSize = 0;
					int subSize = 0;
					for (HunkHeader hunkHeader : hunks) {
						EditList editList = hunkHeader.toEditList();
						for (Edit edit : editList) {
							subSize += edit.getEndA() - edit.getBeginA();
							addSize += edit.getEndB() - edit.getBeginB();
						}
					}
					System.out.println("addSize=" + addSize);
					System.out.println("subSize=" + subSize);
					System.out.println("------------------------------end-----------------------------");
					out.reset();
					diffEntry.getNewPath();
				}

			}
		}
	}

	public AbstractTreeIterator prepareTreeParser(RevCommit commit, Repository repo) {
		System.out.println(commit.getId());
		try (RevWalk walk = new RevWalk(repo)) {
			RevTree tree = walk.parseTree(commit.getTree().getId());

			CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
			try (ObjectReader oldReader = repo.newObjectReader()) {
				oldTreeParser.reset(oldReader, tree.getId());
			}

			walk.dispose();

			return oldTreeParser;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;

	}
}

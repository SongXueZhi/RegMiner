package git;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.Edit.Type;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import model.BlameNode;
import model.NormalFile;
import model.PotentialRFC;
import utils.FileUtilx;

public class Traverler {

	BlameCommand blamer;
	final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("YYYY-MM-dd HH:mm");

	public Traverler(Repository repo) {
		blamer = new BlameCommand(repo);
	}

	public List<BlameNode> getBlameGraph(PotentialRFC pRFC) throws Exception {
		RevCommit BFCP = pRFC.getCommit().getParent(0);
		if (BFCP == null) {
			return null;
		}
		blamer.setStartCommit(BFCP.getId());

		List<BlameNode> level1Nodes = new ArrayList<>();
		for (NormalFile file : pRFC.getNormalJavaFiles()) {
			blamer.setFilePath(file.getOldPath());
			BlameResult result = blamer.call();
			for (Edit edit : file.getEditList()) {
				level1Nodes.addAll(blameEdit(edit, result));
			}
		}
		return level1Nodes;
	}

	public List<BlameNode> blameEdit(Edit edit, BlameResult result) throws Exception {

		List<BlameNode> nodes = new ArrayList<>();
		Type editType = edit.getType();

		if (editType == Edit.Type.INSERT || editType == Edit.Type.EMPTY) {
			FileUtilx.log("not replace or delete ");
			return null;
		}
		for (int i = edit.getBeginA(); i < edit.getEndA(); i++) {
			BlameNode node = new BlameNode();
			node.setCommit(getSourceCommit(result, i));
			int line = getSourceLine(result, i);
			node.setLine(line);
			node.setPair(new int[] { line, i });
			nodes.add(node);
		}
		return nodes;
	}

	public RevCommit getSourceCommit(BlameResult result, int i) throws Exception {
		result.computeAll();
		return result.getSourceCommit(i);
	}

	public int getSourceLine(BlameResult result, int i) throws Exception {
		result.computeAll();
		return result.getSourceLine(i);
	}
}
package regminer.experiment;

import org.eclipse.jgit.revwalk.RevCommit;
import regminer.model.NormalFile;

import java.util.List;

public class TrackedCommit {
    public String name;
    private RevCommit commit;
    private List<NormalFile> normalFiles;

    public TrackedCommit(RevCommit commit) {
        this.commit=commit;
    }

    public RevCommit getCommit() {
        return commit;
    }

    public void setCommit(RevCommit commit) {
        this.commit = commit;
    }

    public List<NormalFile> getNormalFiles() {
        return normalFiles;
    }

    public void setNormalFiles(List<NormalFile> normalFiles) {
        this.normalFiles = normalFiles;
    }
}

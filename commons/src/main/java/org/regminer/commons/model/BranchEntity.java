package org.regminer.commons.model;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.List;

/**
 * @Author: sxz
 * @Date: 2022/06/14/12:06
 * @Description:
 */
public class BranchEntity {
    Ref ref;
    List<RevCommit> commits;

    public Ref getRef() {
        return ref;
    }

    public void setRef(Ref ref) {
        this.ref = ref;
    }

    public List<RevCommit> getCommits() {
        return commits;
    }

    public void setCommits(List<RevCommit> commits) {
        this.commits = commits;
    }
}

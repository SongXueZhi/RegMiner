package org.regminer.commons.model;

import org.eclipse.jgit.revwalk.RevCommit;


/**
 * @Author: sxz
 * @Date: 2022/06/09/00:48
 * @Description:
 */
public abstract class CommitEntity {
    RevCommit revCommit;
    private String projectName;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public RevCommit getRevCommit() {
        return revCommit;
    }

    public void setRevCommit(RevCommit revCommit) {
        this.revCommit = revCommit;
    }


}

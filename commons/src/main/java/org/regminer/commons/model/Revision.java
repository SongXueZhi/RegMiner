package org.regminer.commons.model;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class Revision {

    File localCodeDir = null;
    private String name;
    private String commitID;
    private final List<ChangedFile> changedFiles = new LinkedList<>();

    public Revision(String commmitID, String name) {
        this.commitID = commmitID;
        this.name = name;
    }

    public File getLocalCodeDir() {
        return localCodeDir;
    }

    public void setLocalCodeDir(File localCodeDir) {
        this.localCodeDir = localCodeDir;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommitID() {
        return commitID;
    }

    public void setCommitID(String commitID) {
        this.commitID = commitID;
    }

    public List<ChangedFile> getChangedFiles() {
        return changedFiles;
    }
    
    public String toString() {
    	return this.commitID;
    }

}

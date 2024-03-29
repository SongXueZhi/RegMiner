package org.regminer.common.model;

import org.eclipse.jgit.diff.Edit;

import java.util.List;

public class ChangedFile {
    private String newCommitId;
    private String newPath;
    private String oldPath;
    private List<Methodx> methods;
    private List<Edit> editList;
    private Type type;

    public ChangedFile(String newPath) {
        this.newPath = newPath;
    }

    public void setNewCommitId(String newCommitId) {
        this.newCommitId = newCommitId;
    }

    public String getNewCommitId() {
        return newCommitId;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getOldPath() {
        return oldPath;
    }

    public void setOldPath(String oldPath) {
        this.oldPath = oldPath;
    }

    public List<Edit> getEditList() {
        return editList;
    }

    public void setEditList(List<Edit> editList) {
        this.editList = editList;
    }


    public String getNewPath() {
        return newPath;
    }

    public void setNewPath(String newPath) {
        this.newPath = newPath;
    }

    public List<Methodx> getMethods() {
        return methods;
    }

    public void setMethods(List<Methodx> methods) {
        this.methods = methods;
    }

    public enum Type {
        TEST_SUITE, TEST_RELATE, JAVA_FILE, ANOTHER
    }

}

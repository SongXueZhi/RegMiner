package org.regminer.commons.code.analysis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.util.ArrayList;
import java.util.List;

import java.io.File;

/**
 * @Author: sxz
 * @Date: 2023/12/10/16:16
 * @Description:
 */
public class RefactoringManager {
    protected Logger logger = LogManager.getLogger(this.getClass());
    GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();

    //TODO zangjian use to fix ce after migration
    public List<Refactoring> detect(Repository repo, String commitId) {
        final List<Refactoring> refactorings = new ArrayList<>();
        miner.detectAtCommit(repo, commitId, new RefactoringHandler() {
            @Override
            public void handle(String commitId, List<Refactoring> detectedRefactorings) {
                refactorings.addAll(detectedRefactorings);
            }
        });
        return refactorings;
    }

    public List<Refactoring> detect(Repository repo, String commitId1, String commitId2) throws Exception {
        final List<Refactoring> refactorings = new ArrayList<>();
        miner.detectBetweenCommits(repo, commitId1, commitId2, new RefactoringHandler() {
            @Override
            public void handle(String commitId, List<Refactoring> detectedRefactorings) {
                refactorings.addAll(detectedRefactorings);
            }
        });
        return refactorings;
    }

    public void applyRefactorToPreCommit(File from, File to, List<Refactoring> refactoringList) {
        //TODO zangjian apply refactor in bfc to bfc-1
    }
}

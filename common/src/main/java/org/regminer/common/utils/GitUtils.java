package org.regminer.common.utils;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.regminer.common.tool.RepositoryProvider;
import org.regminer.common.tool.SimpleProgressMonitor;

import java.io.File;
import java.util.List;


public class GitUtils {

    public static boolean clone(File localFile, String cloneUrl) {
        localFile.mkdir();
        try (Git result = Git.cloneRepository()
                .setURI(cloneUrl)
                .setProgressMonitor(new SimpleProgressMonitor())
                .setCloneAllBranches(true)
                .setDirectory(localFile)
                .call()) {
            return true;
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return false;
        }
    }

    public static boolean checkout(String commitID, File codeDir) {
        try (Repository repository = RepositoryProvider.getRepoFromLocal(codeDir); Git git = new Git(repository)) {
            if (commitID.contains("~1")){
                try (RevWalk revWalk = new RevWalk(repository);) {
                    RevCommit commit = revWalk.parseCommit(repository.resolve(commitID));
                    git.reset().setMode(ResetCommand.ResetType.HARD).call();
                    git.checkout().setName(commit.getName()).setCreateBranch(false).setForceRefUpdate(true).call();
                }
            }else{
                git.reset().setMode(ResetCommand.ResetType.HARD).call();
                git.checkout().setName(commitID).setCreateBranch(false).setForceRefUpdate(true).call();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

   // TODO Given projectDir,get ALL commits in project
    // all commits  format in


    public static List<DiffEntry> getDiffEntriesBetweenCommits(File codeDir, String newID, String oldID) {
        try (Repository repository = RepositoryProvider.getRepoFromLocal(codeDir); Git git = new Git(repository)) {
             return git.diff().
                    setOldTree(prepareTreeParser(repository,oldID)).
                    setNewTree(prepareTreeParser(repository,newID)).
                    call();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private static AbstractTreeIterator prepareTreeParser(Repository repository, String objectId) throws Exception {
        // from the commit we can build the tree which allows us to construct the TreeParser
        //noinspection Duplicates
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(repository.resolve(objectId));
            RevTree tree = walk.parseTree(commit.getTree().getId());

            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, tree.getId());
            }

            walk.dispose();

            return treeParser;
        }
    }


}

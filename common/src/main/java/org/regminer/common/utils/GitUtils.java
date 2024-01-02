package org.regminer.common.utils;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
            if (commitID.contains("~1")) {
                try (RevWalk revWalk = new RevWalk(repository);) {
                    RevCommit commit = revWalk.parseCommit(repository.resolve(commitID));
                    checkout(git, commit.getName(),codeDir.getAbsolutePath());
                }
            } else {
                checkout(git, commitID, codeDir.getAbsolutePath());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void checkout(Git git, String commit, String repoPath) throws GitAPIException {
        // check index.lock
        String lockPath = repoPath + File.separator + ".git" + File.separator + "index.lock";
        File lock = new File(lockPath);

        if (lock.exists() && lock.delete()) {
            System.out.println("index.lock exists, deleted!");
        }
        // 删除工作树中未跟踪的所有文件和目录，但会保留已跟踪的文件和 Git 子模块
        git.clean().setCleanDirectories(true).setForce(true).call();
        git.reset().setMode(ResetCommand.ResetType.HARD).call();

        // check modify
        git.add().addFilepattern(".").call();
        git.stashCreate().call();

        git.checkout().setName(commit).setCreateBranch(false).setForceRefUpdate(true).call();
    }

    public static String getHead(File codeDir) {
        try (Repository repository = RepositoryProvider.getRepoFromLocal(codeDir);
             RevWalk revWalk = new RevWalk(repository);){
             return revWalk.parseCommit(repository.resolve("HEAD")).getName();
        } catch (Exception e) {
            return null;
        }
    }

    // TODO Given projectDir,get ALL commits in project
    // all commits  format in


    public static List<DiffEntry> getDiffEntriesBetweenCommits(File codeDir, String newID, String oldID) {
        try (Repository repository = RepositoryProvider.getRepoFromLocal(codeDir); Git git = new Git(repository)) {
            return git.diff().
                    setOldTree(prepareTreeParser(repository, oldID)).
                    setNewTree(prepareTreeParser(repository, newID)).
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

    // 使用拓扑排序遍历commitID前的所有commit
    public static List<String> revListCommand(String commitId, File codeDir) {
        List<String> revList = new ArrayList<>();
        try (Repository repo = RepositoryProvider.getRepoFromLocal(codeDir)) {
            // Use RevWalk to traverse the commit history starting from the specified commit ID
            try (RevWalk walk = new RevWalk(repo)) {
                RevCommit startCommit = walk.parseCommit(repo.resolve(commitId));
                walk.markStart(startCommit);
                for (RevCommit commit : walk) {
                    revList.add(commit.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return revList;
    }


    public static Map<ObjectId, List<RevCommit>> buildChildrenMap(Git git, RevCommit endCommit) throws IOException {
        Map<ObjectId, List<RevCommit>> map = new HashMap<>();
        try (RevWalk walk = new RevWalk(git.getRepository())) {
            // 获取当前分支的最新提交
            ObjectId branchHead = git.getRepository().resolve(git.getRepository().getBranch());
            walk.markStart(walk.parseCommit(branchHead));
            for (RevCommit commit : walk) {
                for (RevCommit parent : commit.getParents()) {
                    map.computeIfAbsent(parent.getId(), k -> new ArrayList<>()).add(commit);
                }
                // 只获取 child，对于更早的就没必要构建了
                if (commit.equals(endCommit)) break;
            }
        }
        return map;
    }
}

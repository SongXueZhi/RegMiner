package org.regminer.common.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.regminer.common.tool.RepositoryProvider;
import org.regminer.common.tool.SimpleProgressMonitor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GitUtils {
    private static final Logger logger = LogManager.getLogger(GitUtils.class);

    private GitUtils() {
        // utility class
    }

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
            logger.error(exception.getMessage());
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
            logger.error("checkout failed! repo is {}, commit is {}", codeDir.getAbsolutePath(), commitID);
            logger.error(e.getMessage());
            return false;
        }
    }

    private static void checkout(Git git, String commit, String repoPath) throws GitAPIException {
        // check index.lock
        String lockPath = repoPath + File.separator + ".git" + File.separator + "index.lock";
        File lock = new File(lockPath);

        if (lock.exists() && lock.delete()) {
            logger.info("index.lock exists, deleted!");
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
            logger.error(e.getMessage());
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

    public static List<String> getParentsUntilMergeNode(String commitId, File codeDir) {
        List<String> revList = new ArrayList<>();
        try (Repository repo = RepositoryProvider.getRepoFromLocal(codeDir)) {
            // Use RevWalk to traverse the commit history starting from the specified commit ID
            try (RevWalk walk = new RevWalk(repo)) {
                RevCommit commit = walk.parseCommit(repo.resolve(commitId));
                while (commit.getParents().length == 1) {
                    commit = walk.parseCommit(commit.getParents()[0]);
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

    /**
     * 获取指定代码仓指定 commit 下指定文件路径的文件内容。
     * @param codeDir 代码仓的本地目录
     * @param commitId commit ID
     * @param filePath 文件路径
     * @return 指定文件的内容
     */
    public static String getFileContentAtCommit(File codeDir, String commitId, String filePath) {
        try (Repository repository = RepositoryProvider.getRepoFromLocal(codeDir);
             RevWalk revWalk = new RevWalk(repository)) {

            // 定位到指定的 commit
            RevCommit commit = revWalk.parseCommit(repository.resolve(commitId));

            // 使用 TreeWalk 来查找特定的文件
            try (TreeWalk treeWalk = new TreeWalk(repository)) {
                treeWalk.addTree(commit.getTree());
                treeWalk.setRecursive(true);
                treeWalk.setFilter(PathFilter.create(filePath));

                if (!treeWalk.next()) {
                    throw new IllegalStateException("File not found in the specified commit");
                }

                // 读取并返回文件内容
                ObjectLoader loader = repository.open(treeWalk.getObjectId(0));
                byte[] bytes = loader.getBytes();
                return new String(bytes, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            logger.error("Error reading file at commit. path: {}, commit: {}", filePath, commitId, e);
            return null;
        }
    }
}

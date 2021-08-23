package regminer.experiment;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import regminer.callgraph.JParser;
import regminer.callgraph.MethodRetriever;
import regminer.miner.migrate.Migrator;
import regminer.model.ChangedFile;
import regminer.model.NormalFile;
import regminer.model.PotentialRFC;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CommitParser extends Migrator {
    Repository repo;
    Git git;
    private JParser jParser = new JParser();

    public CommitParser() {
    }

    public CommitParser(Repository repo, Git git) {
        this.repo = repo;
        this.git = git;
    }
    public Repository getRepo() {
        return repo;
    }

    public void setRepo(Repository repo) {
        this.repo = repo;
    }

    public Git getGit() {
        return git;
    }

    public void setGit(Git git) {
        this.git = git;
    }
    public TrackedCommit parseCommit(String commitID) throws Exception {
        RevCommit newCommit = repo.parseCommit(repo.resolve(commitID));
        RevCommit oldCommit = repo.parseCommit(repo.resolve(commitID+"~1"));
        TrackedCommit trackedCommit = new TrackedCommit(newCommit);
        trackedCommit.setNormalFiles(getDiffFiles(oldCommit,newCommit));
        trackedCommit.name=newCommit.getName();
        return trackedCommit;
    }

    private List<NormalFile> getDiffFiles(RevCommit oldCommit, RevCommit newCommit) throws Exception {
        List<NormalFile> files = new LinkedList<>();
        ObjectId id = newCommit.getTree().getId();
        ObjectId oldId = oldCommit.getTree().getId();
        try (ObjectReader reader = repo.newObjectReader()) {
            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            oldTreeIter.reset(reader, oldId);
            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            newTreeIter.reset(reader, id);
            // finally get the list of changed files
            List<DiffEntry> diffs = git.diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();
            for (DiffEntry entry : diffs) {
                getChangedFile(entry, files);
            }
        }
        return files;
    }


    private void getChangedFile(DiffEntry entry, List<NormalFile> files) throws Exception {
        String path = entry.getNewPath();
        if ((!path.contains("test")) && path.endsWith(".java")) {
            NormalFile file = new NormalFile(entry.getNewPath());
            file.setOldPath(entry.getOldPath());
            file.setEditList(getEdits(entry));
            files.add(file);
        }
    }

    private List<Edit> getEdits(DiffEntry entry) throws Exception {
        List<Edit> result = new LinkedList<Edit>();
        try (DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE)) {
            diffFormatter.setRepository(repo);
            FileHeader fileHeader = diffFormatter.toFileHeader(entry);
            List<? extends HunkHeader> hunkHeaders = fileHeader.getHunks();
            for (HunkHeader hunk : hunkHeaders) {
                result.addAll(hunk.toEditList());
            }
        }
        return result;
    }

    public Map<String, MethodDeclaration> handle(PotentialRFC pRFC) throws Exception {

        String commitID = pRFC.getCommit().getName();
        File bfcDir = checkout(commitID, commitID, "bfc");
        pRFC.fileMap.put(commitID, bfcDir);
        jParser.setSrcRoot(bfcDir);
        List<NormalFile> normalFiles = pRFC.getNormalJavaFiles();
        Map<String, MethodDeclaration> methodMap = new HashMap<>();

        for (NormalFile normalFile : normalFiles) {
            parse(normalFile, bfcDir, methodMap);
        }
        return methodMap;
    }

    public Map<String, MethodDeclaration> handleTrackedCommit(File bfcDir,TrackedCommit commit) throws Exception {
        String commitID =commit.name;
        checkoutInSu(bfcDir,commitID);
        jParser.setSrcRoot(bfcDir);
        List<NormalFile> normalFiles = commit.getNormalFiles();
        Map<String, MethodDeclaration> methodMap = new HashMap<>();
        for (NormalFile normalFile : normalFiles) {
            parse(normalFile, bfcDir, methodMap);
        }
        return methodMap;
    }

    public void parse(NormalFile normalFile, File bfcDir, Map<String, MethodDeclaration> methodMap) throws FileNotFoundException {
        CompilationUnit unit = jParser.getCodeUnit(new File(bfcDir, normalFile.getNewPath()));
        List<MethodDeclaration> methodDeclarations = new MethodRetriever().getMethodList(unit);
        List<Edit> edits = normalFile.getEditList();
        for (Edit edit : edits) {
            matchAll(edit, methodDeclarations, methodMap, normalFile);
        }
    }

    private void matchAll(Edit edit, List<MethodDeclaration> methods, Map<String, MethodDeclaration> methodMap, NormalFile normalFile) {
        for (MethodDeclaration method : methods) {
            match(edit, method, methodMap, normalFile);
        }
    }

    private void match(Edit edit, MethodDeclaration method, Map<String, MethodDeclaration> methodMap, NormalFile normalFile) {
        int editStart = edit.getBeginB() + 1;
        int editEnd = edit.getEndB();

        int methodStart = method.getBegin().get().line;
        int methodStop = method.getEnd().get().line;

        if ((editStart <= methodStart && editEnd >= methodStop) || (editStart >= methodStart && editEnd <= methodStop)
                || (editEnd >= methodStart && editEnd <= methodStop)
                || (editStart >= methodStart && editStart <= methodStop)) {
            String name = normalFile.getNewPath() + "_" + methodStart + "_" + methodStop;
            if (!methodMap.containsKey(name)) {
                methodMap.put(name, method);
            }
        }
    }

}

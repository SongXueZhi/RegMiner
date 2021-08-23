package regminer.experiment;

import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.lib.Repository;
import regminer.ast.JdtMethodRetriever;
import regminer.constant.Conf;
import regminer.finalize.SycFileCleanup;
import regminer.git.GitTracker;
import regminer.git.provider.Provider;
import regminer.miner.PotentialBFCDetector;
import regminer.miner.migrate.Migrator;
import regminer.model.CodeBlock;
import regminer.model.NormalFile;
import regminer.model.PotentialRFC;
import regminer.start.ConfigLoader;
import regminer.utils.CompilationUtil;
import regminer.utils.FileUtilx;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BFCEvolutionAnalyzer3 extends Migrator {
    private final static double PROB_UNIT = 0.05;
    private double notRegProb = 1 - PROB_UNIT;
    private String index = "fastjson_bfc1";
    String dataFilePath = "resource"+File.separator+index;
    Repository repo;
    Git git;
    GitTracker gitTracker = new GitTracker();
    FixMethodParser fixMethodParser = new FixMethodParser();
    File log = new File(Conf.PROJECT_PATH + File.separator + index+".csv");

    public void prepare() throws Exception {
        ConfigLoader.refresh();
        repo = new Provider().create(Provider.EXISITING).get(Conf.LOCAL_PROJECT_GIT);
        git = new Git(repo);
    }

    public void handleTask(List<String> filter) throws Exception {
        PotentialBFCDetector pBFCDetector = new PotentialBFCDetector(repo, git);
        List<PotentialRFC> potentialRFCList = pBFCDetector.detectPotentialBFC(filter);
        for (PotentialRFC pRFC : potentialRFCList) {
            String commitID = pRFC.getCommit().getName();
            File bfcDir = checkout(commitID, commitID, "bfc");
            pRFC.fileMap.put(commitID, bfcDir);
            Map<String, CodeBlock> codeBlockMap = fixMethodParser.handle(pRFC);
            double score = regressionProbCalculate(trackCodeBlocks(codeBlockMap,bfcDir));
            emptyCache(commitID);
            FileUtilx.apendResultToFile(pRFC.getCommit().getName() + "," + score, log);
        }
    }

    public double regressionProbCalculate(int sum) {
        double methodNotRegressionProb = Math.pow(notRegProb, sum);
        return 1 - methodNotRegressionProb;
    }

    public int trackCodeBlocks(Map<String, CodeBlock> codeBlockMap, File bfcDir) {
        int sum = 0;
        for (Map.Entry<String, CodeBlock> entry : codeBlockMap.entrySet()) {
            CodeBlock block = entry.getValue();
            sum+= gitTracker.trackCodeBlockByLogl(block.getStartLine(), block.getStopLine(), block.getNewPath(), bfcDir,block.isIfMethod());
        }
        return sum;
    }

    public void emptyCache(String bfcID) {
        File bfcFile = new File(Conf.CACHE_PATH + File.separator + bfcID);
        new SycFileCleanup().cleanDirectory(bfcFile);
    }

    public List<String> readProjectCommitTOHandle() {
        return new ArrayList<>(FileUtilx.readSetFromFile(dataFilePath));
    }

    public static void main(String[] args) throws Exception {
        BFCEvolutionAnalyzer3 analyzer2 = new BFCEvolutionAnalyzer3();
        analyzer2.prepare();
        analyzer2.handleTask(analyzer2.readProjectCommitTOHandle());
    }

    class FixMethodParser {

        public Map<String, CodeBlock> handle(PotentialRFC pRFC) {
            List<NormalFile> normalFiles = pRFC.getNormalJavaFiles();
            Map<String, CodeBlock> codeBlockMap = new HashMap<>();
            for (NormalFile normalFile : normalFiles) {
                parse(normalFile, pRFC.fileMap.get(pRFC.getCommit().getName()), codeBlockMap);
            }
            return codeBlockMap;
        }

        public void parse(NormalFile normalFile, File bfcDir, Map<String, CodeBlock> codeBlockMap) {
            String code = FileUtilx.readContentFromFile(new File(bfcDir, normalFile.getNewPath()));
            CompilationUnit unit = CompilationUtil.parseCompliationUnit(code);
            List<Comment> comments = unit.getCommentList();
            List<ImportDeclaration> importDeclarations = unit.imports();
            JdtMethodRetriever retriever = new JdtMethodRetriever();
            unit.accept(retriever);
            List<MethodDeclaration> methodDeclarations = retriever.getMemberList();
            List<Edit> edits = normalFile.getEditList();
            for (Edit edit : edits) {
                matchAll(edit, methodDeclarations, codeBlockMap, unit, normalFile, comments,importDeclarations);
            }
        }

        private boolean judgeHunkIsComment(Edit edit, List<Comment> comments, CompilationUnit unit) {
            int editStart = edit.getBeginB()+1;
            int editEnd = edit.getEndB();
            for (Comment comment : comments) {
                int commentStart = unit.getLineNumber(comment.getStartPosition()) - 1;
                int commentEnd = unit.getLineNumber(comment.getStartPosition() + comment.getLength()) - 1;
                if (editStart >= commentStart && editEnd <= commentEnd) {
                    return true;
                }
            }
            return false;
        }

        private boolean judgeHunkIsImport(Edit edit,List<ImportDeclaration> importDeclarations,CompilationUnit unit){
            int editStart = edit.getBeginB()+1;
            int editEnd = edit.getEndB();
            for (ImportDeclaration importDeclaration : importDeclarations) {
                int commentStart = unit.getLineNumber(importDeclaration.getStartPosition()) - 1;
                int commentEnd = unit.getLineNumber(importDeclaration.getStartPosition() + importDeclaration.getLength()) - 1;
                if (editStart >= commentStart && editEnd <= commentEnd) {
                    return true;
                }
            }
            return false;
        }
        private void matchAll(Edit edit, List<MethodDeclaration> methods, Map<String, CodeBlock> codeBlockMap, CompilationUnit unit, NormalFile normalFile, List<Comment> comments,List<ImportDeclaration> importDeclarations) {
            boolean flag = false;
            for (MethodDeclaration method : methods) {
                if (match(edit, method, codeBlockMap, unit, normalFile)){
                    flag =true;
                }
            }
            //if hunk if not any method
            if (!flag) {
                // make sure track block not comments
                if (judgeHunkIsComment(edit, comments, unit) || judgeHunkIsImport(edit,importDeclarations,unit)) {
                    return;
                }
                int start = edit.getBeginB()+1;
                int end = edit.getEndB();
                String name = normalFile.getNewPath() + "_"
                        + start+ "_" + end;
                if (!codeBlockMap.containsKey(name)) {
                    CodeBlock codeBlock = new CodeBlock();
                    codeBlock.setIfMethod(false);
                    codeBlock.setStartLine(start);
                    codeBlock.setStopLine(end);
                    codeBlock.setNewPath(normalFile.getNewPath());
                    codeBlockMap.put(name, codeBlock);
                }
            }
        }

        //
        private boolean match(Edit edit, MethodDeclaration method, Map<String, CodeBlock> codeBlockMap, CompilationUnit unit, NormalFile normalFile) {
            int editStart = edit.getBeginB()+1;
            int editEnd = edit.getEndB();

            int methodStart = unit.getLineNumber(method.getStartPosition()) ;
            int methodStop = unit.getLineNumber(method.getStartPosition() + method.getLength());

            if ((editStart <= methodStart && editEnd >= methodStop) || (editStart >= methodStart && editEnd <= methodStop)
                    || (editEnd >= methodStart && editEnd <= methodStop)
                    || (editStart >= methodStart && editStart <= methodStop)) {
                String name = normalFile.getNewPath() + "_" + methodStart + "_" + methodStop;
                if (!codeBlockMap.containsKey(name)) {
                    CodeBlock block = new CodeBlock();
                    block.setIfMethod(true);
                    block.setMethodDeclaration(method);
                    block.setStartLine(methodStart);
                    block.setStopLine(methodStop);
                    block.setNewPath(normalFile.getNewPath());
                    codeBlockMap.put(name, block);
                }
                return true;
            } else {
                return false;
            }
        }
    }
}

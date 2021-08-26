package regminer.miner.migrate.compile;

import com.zhixiangli.code.similarity.CodeSimilarity;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.*;
import regminer.ast.JdtFieldRetriever;
import regminer.coverage.model.CoverMethod;
import regminer.coverage.model.CoverNode;
import regminer.miner.BFCFixPatchParser;
import regminer.model.Methodx;
import regminer.model.NormalFile;
import regminer.model.PotentialRFC;
import regminer.utils.CompilationUtil;
import regminer.utils.FileUtilx;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CompileErrorProf {

    public final static String srcPath = "src/main/java";
    BFCFixPatchParser fixPatchParser = new BFCFixPatchParser();

    /**
     * @param pRFC
     * @param commitDir
     * @param coverNodes
     * @throws Exception
     */
    public void tryRepairCE(PotentialRFC pRFC, File commitDir, List<CoverNode> coverNodes) throws Exception {
        FileUtilx.log("Compile error and try repair.....");
        // pRFC need make sure fix patch,which can't mending to commit
        if (!pRFC.isHaveEquipFixPatch()) {
            fixPatchParser.equipFixPatchToRFC(pRFC);
        }
        File bfcDir = pRFC.fileMap.get(pRFC.getCommit().getName());
        List<NormalFile> normalFiles = pRFC.getNormalJavaFiles();
        // mending missing node  in commit
        List<String> doneClass = new ArrayList<>();
        List<CoverNode> doneNode = new ArrayList<>();
        List<MethodDeclaration> doneMethodDeclarations = new ArrayList<>();
        for (CoverNode coverNode : coverNodes) { //todo adddone
            // if cover class not exist in commit c, mending it  to c from bfcdir
            String packagePath = coverNode.getCoverPackage().getName().replace(".", File.separator);
            String classPath = packagePath + File.separator
                    + coverNode.getCoverClass().getFileName();
            File file = new File(commitDir + File.separator + srcPath + File.separator + classPath);
            if (!file.exists()) { // whether class in commit
                mendingClass(classPath, file, bfcDir, commitDir, normalFiles);
                doneClass.add(file.getAbsolutePath());
            } else {
                if (doneClass.contains(file.getAbsolutePath()) || doneNode.contains(coverNode)) {
                    continue;
                }
                // If cover class  exits in commit , judge methodNode
                // cover method node Also can't in fix Path
                CoverMethod coverMethod = coverNode.getCoverMethod();
                if (notMethodInFixPatch(classPath, coverMethod.getName(), coverMethod.getLine(), normalFiles)) {
                    // mending method
                    File bfcFile = new File(bfcDir + File.separator + srcPath + File.separator + classPath);
                    CompilationUnit bfcUnit = CompilationUtil.parseCompliationUnit(FileUtilx.readContentFromFile(bfcFile));
                    CompilationUnit bicUnit = CompilationUtil.parseCompliationUnit(FileUtilx.readContentFromFile(file));
                    Methodx methodxInBFC = getMethodDeclarationFromCoverMethod(coverMethod, bfcUnit);
                    if (doneMethodDeclarations.contains(methodxInBFC.getMethodDeclaration())) {
                        continue;
                    }
                    if (!methodMatch(bicUnit, methodxInBFC)) {
                        mendingMethod(file, bicUnit, bfcUnit, methodxInBFC, doneMethodDeclarations);
                    }
                }
            }
        }

    }

    public void mendingMethod(File commitFile, CompilationUnit bicUnit, CompilationUnit bfcUnit, Methodx methodx, List<MethodDeclaration> doneMethodDeclarations) {
        //copy method to Class
        AST ast = bicUnit.getAST();
        TypeDeclaration typeDeclaration = (TypeDeclaration) bicUnit.types().get(0);
        MethodDeclaration methodDeclaration = methodx.getMethodDeclaration();
        ASTNode node = methodDeclaration;
        if ((methodDeclaration.getParent() instanceof TypeDeclaration)) {
            MethodDeclaration methodNode = (MethodDeclaration) ASTNode.copySubtree(ast, methodDeclaration);
            typeDeclaration.bodyDeclarations().add(methodNode);
        }
        if (methodDeclaration.getParent().getParent() instanceof TypeDeclaration) {
            TypeDeclaration inner = (TypeDeclaration) ASTNode.copySubtree(ast, methodDeclaration.getParent());
            node = inner;
            typeDeclaration.bodyDeclarations().add(inner);
        }
//        if (methodDeclaration.getParent().getParent() instanceof FieldDeclaration) {
//            ImportDeclaration inner = (ImportDeclaration) ASTNode.copySubtree(ast, methodDeclaration.getParent().getParent());
//            node = inner;
//            typeDeclaration.bodyDeclarations().add(inner);
//        }

        List<ImportDeclaration> importDeclarations = bfcUnit.imports();
        for (ImportDeclaration importDeclaration : importDeclarations) {
            String importName = importDeclaration.getName().getFullyQualifiedName();
            if (importName.lastIndexOf(".") > -1) {
                importName = importName.substring(importName.lastIndexOf(".") + 1);
            } else {
                importName = importName;
            }
            if (node.toString().contains(importName)) {
                ImportDeclaration imAstNode = (ImportDeclaration) ASTNode.copySubtree(ast, importDeclaration);
                bicUnit.imports().add(imAstNode);
            }
        }

        JdtFieldRetriever jdtFieldRetriever = new JdtFieldRetriever();
        bfcUnit.accept(jdtFieldRetriever);
        Map<FieldDeclaration, List<VariableDeclarationFragment>> fieldMapBFC = jdtFieldRetriever.fieldMap;
        JdtFieldRetriever jdtFieldRetriever1 = new JdtFieldRetriever();
        bicUnit.accept(jdtFieldRetriever1);

        Map<FieldDeclaration, List<VariableDeclarationFragment>> fieldMapBIC = jdtFieldRetriever1.fieldMap;
        for (Map.Entry<FieldDeclaration, List<VariableDeclarationFragment>> entry : fieldMapBFC.entrySet()) {
            VariableDeclarationFragment variableDeclarationFragment = entry.getValue().get(0);
            if ((!(node instanceof FieldDeclaration)) && node.toString().contains(variableDeclarationFragment.getName().toString())) {
                for (Map.Entry<FieldDeclaration, List<VariableDeclarationFragment>> entry1 : fieldMapBIC.entrySet()) {
                    VariableDeclarationFragment variableDeclarationFragmentBIC = entry1.getValue().get(0);
                    if (variableDeclarationFragmentBIC.getName().toString().equals(variableDeclarationFragment.getName().toString())) {
                        entry1.getKey().delete();
                        FieldDeclaration fieldNode = (FieldDeclaration) ASTNode.copySubtree(ast, entry.getKey());
                        typeDeclaration.bodyDeclarations().add(fieldNode);
                        break;
                    }
                }
            }
        }
        try {
            FileUtils.writeStringToFile(commitFile, bicUnit.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        doneMethodDeclarations.add(methodDeclaration);
    }

    public Methodx getMethodDeclarationFromCoverMethod(CoverMethod coverMethod, CompilationUnit bfcUnit) {
        List<Methodx> methodxes = CompilationUtil.getAllMethod(bfcUnit);

        for (Methodx methodx : methodxes) {
            int startLine = methodx.getStartLine();
            int endLine = methodx.getStopLine();
            if (methodx.getSimpleName().equals(coverMethod.getName())
                    && (coverMethod.getLine() >= startLine && coverMethod.getLine() <= endLine)) {
                return methodx;
            }
        }
        return null;
    }

    public boolean methodMatch(CompilationUnit unit, Methodx bfcMethodx) {
        List<Methodx> methodxes = CompilationUtil.getAllMethod(unit);
        HashMap<Methodx, Double> methodSimilarityMap = new HashMap<>();
        CodeSimilarity codeSimilarity = new CodeSimilarity();
        for (Methodx methodx : methodxes) {
            if (methodx.getSignature().equals(bfcMethodx.getSignature())) {
                return true;
            }
            if (methodx.getSimpleName().equals(bfcMethodx.getSimpleName())) {
                double sim = codeSimilarity.get(methodx.getMethodDeclaration().toString(), bfcMethodx.getMethodDeclaration().toString());
                methodSimilarityMap.put(methodx, sim);
            }
        }
        if (methodSimilarityMap.size() > 0) {
            for (Map.Entry<Methodx, Double> entry : methodSimilarityMap.entrySet()) {
                if (entry.getValue() > 0.5) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean notMethodInFixPatch(String classPath, String methodName, int startLine, List<NormalFile> normalFiles) {
        for (NormalFile normalFile : normalFiles) {
            if (normalFile.getNewPath().contains(classPath)) {
                for (Methodx methodx : normalFile.editMethodxes) {
                    if (methodx.getSimpleName().equals(methodName) && methodx.getStartLine() == startLine) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean notClassInFixPatch(String classPath, List<NormalFile> normalFiles) {
        for (NormalFile file : normalFiles) {
            if (file.getNewPath().contains(classPath)) {
                return false;
            }
        }
        return true;
    }

    public void mendingClass(String classPath, File file, File bfcDir, File commitDir, List<NormalFile> normalFiles) {
        try {
            // if package not exists ,mkdir
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            File fileInBFC = new File(bfcDir + File.separator + srcPath +
                    File.separator + classPath);
            FileUtils.copyToDirectory(fileInBFC, file.getParentFile());
            //mending interface and parentClass
            CompilationUnit unit = CompilationUtil.parseCompliationUnit(FileUtilx.readContentFromFile(fileInBFC));
            TypeDeclaration typeDeclaration = (TypeDeclaration) unit.types().get(0);
            List<Type> interfaces = typeDeclaration.superInterfaceTypes();
            mendingInterfaces(fileInBFC, interfaces, unit.imports(), commitDir, bfcDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mendingInterfaces(File fileINBFC, List<Type> interfaces, List<ImportDeclaration> imports, File commitDir, File bfcDir) throws IOException {
        for (Type interfacesAnDType : interfaces) {
            File targetFile = null;
            String name = interfacesAnDType.toString();
            ImportDeclaration targetImport = null;
            for (ImportDeclaration importDeclaration : imports) {
                String importName = importDeclaration.getName().getFullyQualifiedName();
                if (importName.lastIndexOf(".") > -1) {
                    importName = importName.substring(importName.lastIndexOf(".") + 1);
                } else {
                    importName = importName;
                }
                if (importName.equals(name)) {
                    targetImport = importDeclaration;
                    break;
                }
            }
            if (targetImport == null) {
                File[] files = fileINBFC.getParentFile().listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        continue;
                    }
                    String fileName = files[i].getName();
                    if (fileName.endsWith(".java")) {
                        fileName = fileName.replace(".java", "");
                        if (fileName.equals(name)) {
                            targetFile = files[i];
                        }
                    }
                }
            } else {
                String classPath = targetImport.getName().toString().replace(".", File.separator) + ".java"; //TODO judege class exits
                targetFile = new File(bfcDir, srcPath + File.separator + classPath);
            }
            String classPath = targetFile.toString().split(srcPath)[1];
            File desDir = new File(commitDir + File.separator + srcPath + classPath).getParentFile();
            if (targetFile.exists()) {
                if (!desDir.exists()) {
                    desDir.mkdirs();
                }
                FileUtils.copyToDirectory(targetFile, desDir);
            }
        }

    }
}

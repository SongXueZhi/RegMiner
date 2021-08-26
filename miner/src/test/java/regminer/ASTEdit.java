package regminer;

import org.eclipse.jdt.core.dom.*;
import regminer.model.Methodx;
import regminer.utils.CompilationUtil;
import regminer.utils.FileUtilx;

import java.io.File;
import java.util.List;

public class ASTEdit {
    static String path = "/home/sxz/Desktop/FastDateFormat.java";
    static String patten = "/home/sxz/Desktop/tmp/FormatCache.java";

    public static void main(String[] args) {
        getAllMethod();
        CompilationUnit unit = CompilationUtil.parseCompliationUnit(FileUtilx.readContentFromFile(new File(path)));
        MethodDeclaration method = getMethodDeclarationString();
        AST ast = unit.getAST();
        MethodDeclaration methodNode = (MethodDeclaration) ASTNode.copySubtree(ast,method);
        TypeDeclaration typeDeclaration =(TypeDeclaration) unit.types().get(0);
        typeDeclaration.bodyDeclarations().add(methodNode);
        System.out.println(typeDeclaration.toString());
        FieldDeclaration[] fieldDeclarations = typeDeclaration.getFields();
        for (FieldDeclaration fieldDeclaration :fieldDeclarations){
            if (fieldDeclaration.toString().contains("cDateTimeInstanceCache")){
                    fieldDeclaration.delete();
            }
        }
        FieldDeclaration field = getFiled();
        FieldDeclaration fieldDeclaration =(FieldDeclaration) ASTNode.copySubtree(ast,field);
        typeDeclaration.bodyDeclarations().add(fieldDeclaration);
        System.out.println(typeDeclaration.toString());
        System.out.println(unit .toString());
    }

    public static void getAllMethod(){
        CompilationUnit unit = CompilationUtil.parseCompliationUnit(FileUtilx.readContentFromFile(new File(path)));
        List<Methodx> Methods = CompilationUtil.getAllMethod(unit);
        for (Methodx methodx:Methods){
            MethodDeclaration methodDeclaration = methodx.getMethodDeclaration();
            methodDeclaration.getName();
        }
        System.out.println("");
    }
    public static FieldDeclaration getFiled(){
        CompilationUnit unit = CompilationUtil.parseCompliationUnit(FileUtilx.readContentFromFile(new File(patten)));
        TypeDeclaration typeDeclarations = (TypeDeclaration)unit.types().get(0);
        FieldDeclaration[] fieldDeclarations = typeDeclarations.getFields();
        for (FieldDeclaration fieldDeclaration1: fieldDeclarations){
            if (fieldDeclaration1.toString().contains("cDateTimeInstanceCache")){
                return fieldDeclaration1;
            }
        }
        return null;
    }
    public static MethodDeclaration getMethodDeclarationString() {
        CompilationUnit unit = CompilationUtil.parseCompliationUnit(FileUtilx.readContentFromFile(new File(patten)));
        List<Methodx> methodxes = CompilationUtil.getAllMethod(unit);
        MethodDeclaration declaration = null;
        for (Methodx methodx : methodxes) {
            if (methodx.getMethodDeclaration().getName().toString().equals("getPatternForStyle")) {
                declaration = methodx.getMethodDeclaration();
            }
        }
        return declaration;
    }
}

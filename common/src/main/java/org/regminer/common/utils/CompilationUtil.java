package org.regminer.common.utils;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;
import org.regminer.common.ast.JdtClassRetriever;
import org.regminer.common.model.Methodx;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class CompilationUtil {
    public static CompilationUnit parseCompliationUnit(String fileContent) {

        ASTParser parser = ASTParser.newParser(AST.JLS13); // handles JDK 1.0, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6
        parser.setSource(fileContent.toCharArray());
        // In order to parse 1.6 code, some compiler options need to be set to 1.6
        Map<String, String> options = JavaCore.getOptions();
        JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, options);
        parser.setCompilerOptions(options);

        CompilationUnit result = (CompilationUnit) parser.createAST(null);
        return result;
    }

    public static List<Methodx> getAllMethod(String codeContent) {
        List<Methodx> methods = new ArrayList<>();
        CompilationUnit unit = parseCompliationUnit(codeContent);

        // 获取包名
        String packageName = unit.getPackage() != null ? unit.getPackage().getName().getFullyQualifiedName() : "";

        // 类访问者
        unit.accept(new ASTVisitor() {
            @Override
            public boolean visit(TypeDeclaration typeDecl) {
                String className = typeDecl.getName().getIdentifier();
                // 创建完整的类名（包括包名）
                String fullClassName = packageName.isEmpty() ? className : packageName + "." + className;

                for (MethodDeclaration method : typeDecl.getMethods()) {
                    // 处理每个方法
                    String simpleName = method.getName().toString();
                    List<ASTNode> parameters = method.parameters();
                    StringJoiner sj = new StringJoiner(",", simpleName + "(", ")");
                    for (ASTNode param : parameters) {
                        sj.add(param.toString());
                    }
                    String signature = sj.toString();
                    int startLine = unit.getLineNumber(method.getStartPosition()) - 1;
                    int endLine = unit.getLineNumber(method.getStartPosition() + method.getLength()) - 1;

                    // 使用完整的类名
                    methods.add(new Methodx(fullClassName, signature, startLine, endLine, simpleName, method));
                }
                return super.visit(typeDecl);
            }
        });

        return methods;
    }


    public static String getQualityClassName(String codeContent) {
        String result;
        CompilationUnit unit = parseCompliationUnit(codeContent);
        JdtClassRetriever retriever = new JdtClassRetriever();
        unit.accept(retriever);
        result = retriever.getQualityName();
        return result;
    }
}

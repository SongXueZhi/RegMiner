package org.regminer.common.code.analysis;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;
import org.regminer.common.code.analysis.ast.JdtClassRetriever;
import org.regminer.common.model.Methodx;

import java.io.BufferedReader;
import java.io.StringReader;
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

    public static String addOrReplaceMethod(String codeContent, Methodx newMethod) {
        CompilationUnit unit = parseCompliationUnit(codeContent);
        final int[] affectedLines = {0, 0};
        String newCodeContent = codeContent;
        try {
            List<Methodx> existingMethods = getAllMethod(codeContent);
            // 检查是否存在与newMethod签名相同的方法
            boolean methodExists = false;
            for (Methodx existingMethod : existingMethods) {
                if (existingMethod.getSignature().equals(newMethod.getSignature())) {
                    // 替换方法
                    affectedLines[0] = existingMethod.getStartLine();
                    affectedLines[1] = existingMethod.getStopLine();
                    methodExists = true;
                    break;
                }
            }
            // 如果不存在，则插入新的方法节点
            if (!methodExists) {
                unit.accept(new ASTVisitor() {
                    @Override
                    public boolean visit(TypeDeclaration typeDecl) {
                        // 记录受影响的行号
                        affectedLines[0] = unit.getLineNumber(typeDecl.getStartPosition()) + 1;
                        affectedLines[1] = unit.getLineNumber(typeDecl.getStartPosition()) + 1;
                        return false; // 停止访问其他类
                    }
                });
            }
            newCodeContent = replaceMethod(codeContent, newMethod, affectedLines);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newCodeContent;
    }

    private static String replaceMethod(String codeContent, Methodx newMethod, int[] affectedLines) {
        try (BufferedReader reader = new BufferedReader(new StringReader(codeContent))) {
            StringBuilder res = new StringBuilder();
            String line;
            int currentLine = 1;
            boolean writen = false;
            while ((line = reader.readLine()) != null) {
                if (currentLine >= affectedLines[0] && currentLine <= affectedLines[1]) {
                    // 在受影响的行号范围内，将内容替换为新方法的内容
                    if (!writen) {
                        res.append(newMethod.getMethodDeclaration().toString());
                        writen = true;
                    }
                } else {
                    // 在未受影响的行号范围内，保留原有内容
                    res.append(line).append(System.lineSeparator());
                }
                currentLine++;
            }
            // 获取替换后的代码
            return res.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return codeContent;
    }
}

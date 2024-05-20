import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddMethodWithJDT {
    public static void main(String[] args) {
        try {
            // 读取Java源文件
            File sourceFile = new File("/Users/sxz/Documents/coding/project/RegMiner/commons/src/test/resources" +
                    "/RandomStringGeneratorTest.java");
            List<String> lines = Files.readAllLines(sourceFile.toPath());
            StringBuilder sourceCode = new StringBuilder();
            for (String line : lines) {
                sourceCode.append(line).append(System.lineSeparator());
            }

            // 提取License信息
//            String licenseInfo = extractLicenseInfo(lines);

            // 设置AST解析器
            ASTParser parser = ASTParser.newParser(AST.JLS_Latest);
            parser.setSource(sourceCode.toString().toCharArray());
            parser.setKind(ASTParser.K_COMPILATION_UNIT);
            parser.setBindingsRecovery(true);
            parser.setResolveBindings(true);
            parser.setEnvironment(null, null, null, true);
            parser.setUnitName(sourceFile.getName());
            parser.setCompilerOptions(JavaCore.getOptions());

            // 解析源代码
            CompilationUnit cu = (CompilationUnit) parser.createAST(null);

            // 获取类型声明（类声明）
            TypeDeclaration typeDeclaration = (TypeDeclaration) cu.types().get(0);

            // 创建新方法
            AST ast = cu.getAST();
            MethodDeclaration newMethod = ast.newMethodDeclaration();
            newMethod.setName(ast.newSimpleName("newMethodName"));
            newMethod.setReturnType2(ast.newPrimitiveType(PrimitiveType.VOID));
            newMethod.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
            Block body = ast.newBlock();
            newMethod.setBody(body);

            // 添加新方法到类型声明
            typeDeclaration.bodyDeclarations().add(newMethod);

            // 格式化修改后的代码
            String formattedCode = formatCode(cu.toString());

            // 将License信息和格式化后的代码写回文件
            try (FileWriter writer = new FileWriter(sourceFile)) {
//                writer.write(licenseInfo);  // 写入License信息
                writer.write(formattedCode);
            }

            System.out.println("方法已成功添加并保存，License信息保持不变。");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private static String extractLicenseInfo(List<String> lines) {
//        StringBuilder licenseInfo = new StringBuilder();
//        for (String line : lines) {
//            if (line.startsWith("/*") || line.startsWith("//") || (licenseInfo.length() > 0 && !line.trim().isEmpty())) {
//                licenseInfo.append(line).append(System.lineSeparator());
//                if (line.endsWith("*/")) {
//                    break;
//                }
//            } else {
//                break;
//            }
//        }
//        return licenseInfo.toString();
//    }

    private static String formatCode(String sourceCode) {
        CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(null);
        TextEdit textEdit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT, sourceCode, 0, sourceCode.length(), 0, null);
        Document document = new Document(sourceCode);
        try {
            if (textEdit != null) {
                textEdit.apply(document);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return document.get();
    }
}

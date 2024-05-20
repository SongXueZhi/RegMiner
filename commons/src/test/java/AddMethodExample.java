import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.PrimitiveType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class AddMethodExample {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try {
            // 读取Java源文件
            File sourceFile = new File("/Users/sxz/Documents/coding/project/RegMiner/commons/src/test/resources" +
                    "/RandomStringGeneratorTest.java");
            List<String> lines = Files.readAllLines(sourceFile.toPath());
            StringBuilder sourceCode = new StringBuilder();
            for (String line : lines) {
                sourceCode.append(line).append(System.lineSeparator());
            }

            // 解析源代码
            CompilationUnit cu = new JavaParser().parse(sourceCode.toString()).getResult().get();

            // 获取类声明
            ClassOrInterfaceDeclaration classDeclaration = cu.getClassByName("RandomStringGeneratorTest").orElseThrow(() -> new RuntimeException("Class not found"));
            classDeclaration.getFullyQualifiedName().get();
            // 创建新方法
            MethodDeclaration newMethod = new MethodDeclaration();
            newMethod.setName("newMethodName");
            newMethod.setType(PrimitiveType.byteType());
            newMethod.setBody(new BlockStmt());
            newMethod.addModifier(Modifier.Keyword.PUBLIC);

            // 添加新方法到类
            classDeclaration.addMember(newMethod);


            // 将修改后的AST写回文件
            try (FileWriter writer = new FileWriter(sourceFile)) {
                writer.write(cu.toString());
            }

            System.out.println("方法已成功添加并保存，License信息保持不变。");

        } catch (IOException e) {
            e.printStackTrace();
        }
        long stop = System.currentTimeMillis();
        System.out.println("Time: " + (stop - start) + "ms");
    }
}

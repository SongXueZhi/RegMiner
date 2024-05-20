import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ObjectTypeParser {

    public static void main(String[] args) {
        // 替换为你要解析的Java文件的路径
        String filePath = "/Users/sxz/Documents/coding/project/RegMiner/commons/src/test/resources" +
        "/RandomStringGeneratorTest.java";
        try {
            FileInputStream in = new FileInputStream(filePath);
            CompilationUnit cu = new JavaParser().parse(in).getResult().get();

            cu.accept(new VoidVisitorAdapter<Void>() {
                @Override
                public void visit(ClassOrInterfaceDeclaration n, Void arg) {
                    System.out.println("Class: " + n.getName());
                    super.visit(n, arg);
                }

                @Override
                public void visit(FieldDeclaration n, Void arg) {
                    n.getVariables().forEach(variable -> {
                        System.out.println("Field: " + variable.getName());
                        System.out.println("Type: " + variable.getType());
                    });
                    super.visit(n, arg);
                }
            }, null);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

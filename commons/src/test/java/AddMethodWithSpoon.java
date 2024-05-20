import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.compiler.FileSystemFile;
import spoon.reflect.declaration.ModifierKind;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class AddMethodWithSpoon {
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


            // 使用Spoon解析源文件
            Launcher launcher = new Launcher();
            launcher.addInputResource(new FileSystemFile(sourceFile));
            launcher.getEnvironment().setNoClasspath(true);
            launcher.buildModel();
            CtModel model = launcher.getModel();

            // 获取类声明
//            Factory factory = launcher.getFactory();
//            CtClass<?> ctClass = (CtClass<?>) model.getAllTypes().stream().filter(ctType ->
//                  ctType.getQualifiedName().contains("RandomString")).findFirst().orElseThrow(RuntimeException::new
//            );
//
//            // 创建新方法
//            CtTypeReference<Void> voidType = factory.Type().VOID_PRIMITIVE;
//            CtMethod<Void> newMethod = factory.Method().create(
//                    ctClass,
//                    new HashSet<>(Collections.singletonList(ModifierKind.PUBLIC)),
//                    voidType,
//                    "newMethodName",
//                    Collections.emptyList(),
//                    Collections.emptySet()
//            );
//
//            // 设置方法体
//            CtBlock<Void> body = factory.Core().createBlock();
//            newMethod.setBody(body);
//
//            // 将新方法添加到类中
//            ctClass.addMethod(newMethod);
//
//            // 使用Spoon的pretty-printer将修改后的代码写回文件
//            try (FileWriter writer = new FileWriter(sourceFile)) {
//                writer.write(launcher.getEnvironment().createPrettyPrinterAutoImport().printTypes(ctClass));
//            }
//
//            System.out.println("方法已成功添加并保存，License信息保持不变。");

        } catch (IOException e) {
            e.printStackTrace();
        }
        long stop = System.currentTimeMillis();
        System.out.println("Time: " + (stop - start) + "ms");
    }

}

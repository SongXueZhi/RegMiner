package org.regminer.common.code.analysis;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

import java.util.List;

/**
 * @Author: sxz
 * @Date: 2024/01/12/16:28
 * @Description:
 */
public class ClassGraphExample {

    public void  getClassGraph(){
        try (ScanResult scanResult = new ClassGraph().verbose()
                .overrideClasspath("spring-commons/target/classes","spring-commons/target/test" +
                        "-classes")
                .enableAllInfo()
                .enableExternalClasses().enableInterClassDependencies().enableMethodInfo().enableFieldInfo()
                .enableClassInfo().enableAnnotationInfo()
                .scan()) {

            scanResult.getAllClasses().forEach(classInfo -> {
                if (classInfo.getName().contains("KotlinCopyMethodUnitTests")){
                    System.out.println("Class: " + classInfo.getClassDependencies());
                    System.out.println("Class: " + classInfo.getName());
                    System.out.println("Superclass: " + classInfo.getSuperclasses());
                    System.out.println("Implemented Interfaces: " + classInfo.getClassesImplementing());

                    // 其他可能的依赖关系分析...
                    System.out.println();

                }

                // 其他处理...
            });
        }
    }

    public static void main(String[] args) {
        ClassGraphExample codeAnalyzer = new ClassGraphExample();
        codeAnalyzer.getClassGraph();
    }
}

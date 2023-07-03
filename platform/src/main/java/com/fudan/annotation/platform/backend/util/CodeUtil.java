package com.fudan.annotation.platform.backend.util;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.io.*;
import java.util.Map;

/**
 * description:
 *
 * @author Richy
 * create: 2022-03-07 17:44
 **/
public class CodeUtil {
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
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T clone(T obj){
        T cloneObj = null;
        try {
            //写入字节流
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream obs = new ObjectOutputStream(out);
            obs.writeObject(obj);
            obs.close();

            //分配内存，写入原始对象，生成新对象
            ByteArrayInputStream ios = new ByteArrayInputStream(out.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(ios);
            //返回生成的新对象
            cloneObj = (T) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cloneObj;
    }
}
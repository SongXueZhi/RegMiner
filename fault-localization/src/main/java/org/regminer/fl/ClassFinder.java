package org.regminer.fl;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.FileUtils;

public class ClassFinder {

    private Set<String> testMethods = new HashSet<>();

    public Set<String> getTestMethods() {
        return testMethods;
    }

    public Set<String> getJavaClassesOldVersion(String path) {
        return getJavaClassesOldVersion(path, "class");
    }

    public Set<String> getJavaClassesOldVersion(String path, String filter) {
        Set<String> classes = new HashSet<>();
        File directory = new File(path);
        Collection<File> files = FileUtils.listFiles(directory, new String[] { filter }, true);
        for (File file : files) {
            String relativePath = directory.toURI().relativize(file.toURI()).getPath();
            String className = relativePath.replace("/", ".").substring(0, relativePath.length() - filter.length() - 1);
            classes.add(className);
        }
        return classes;
    }

    public Set<String> getJavaClasses(String srcPath, List<String> deps) {
        Set<String> classes = new HashSet<>();
        List<String> classpath = new ArrayList<>();
        classpath.add(srcPath);
        for (String dep : deps) {
            classpath.add(dep);
        }
        URL[] urls = new URL[classpath.size()];
        try {
            int cnt = 0;
            for (String path : classpath) {
                urls[cnt] = new File(path).toURI().toURL();
                cnt++;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        URLClassLoader classLoader = new URLClassLoader(urls);
        File directory = new File(srcPath);
        Collection<File> files = FileUtils.listFiles(directory, new String[] { "class" }, true);
        for (File file : files) {
            String relativePath = directory.toURI().relativize(file.toURI()).getPath();
            String className = relativePath.replace("/", ".").substring(0, relativePath.length() - ".class".length());
            Class<?> clazz = null;
            try {
                clazz = classLoader.loadClass(className);
                classes.add(className);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            classLoader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

    public Set<String> getTestClasses(String testPath, String srcPath, List<String> deps) {
        Set<String> classes = new HashSet<>();
        List<String> classpath = new ArrayList<>();
        classpath.add(testPath);
        classpath.add(srcPath);
        for (String dep : deps) {
            classpath.add(dep);
        }
        URL[] urls = new URL[classpath.size()];
        try {
            int cnt = 0;
            for (String path : classpath) {
                urls[cnt] = new File(path).toURI().toURL();
                cnt++;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        URLClassLoader classLoader = new URLClassLoader(urls);
        File directory = new File(testPath);
        Collection<File> files = FileUtils.listFiles(directory, new String[] { "class" }, true);
        for (File file : files) {
            String relativePath = directory.toURI().relativize(file.toURI()).getPath();
            String className = relativePath.replace("/", ".").substring(0, relativePath.length() - ".class".length());
            Class<?> clazz = null;
            try {
                clazz = classLoader.loadClass(className);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (isTest(clazz)) {
                classes.add(className);
            }
        }
        try {
            classLoader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

    private boolean isTest(Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        boolean isTest = false;
        for (Method method : methods) {
            for (Annotation a : method.getAnnotations()) {
                if (a.annotationType().getCanonicalName().equals("org.junit.Test")) {
                    testMethods.add(clazz.getName() + "#" + method.getName());
                    isTest = true;
                    break;
                }
            }
            if (method.getParameterTypes().length == 0 && method.getName().startsWith("test") && method.getReturnType().equals(Void.TYPE) && Modifier.isPublic(method.getModifiers())) {
                testMethods.add(clazz.getName() + "#" + method.getName());
                isTest = true;
            }
        }
        if (isTest)
            return true;
        return false;
    }
}

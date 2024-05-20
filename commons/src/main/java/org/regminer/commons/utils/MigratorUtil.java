package org.regminer.commons.utils;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.*;
import org.regminer.commons.code.analysis.CompilationUtil;
import org.regminer.commons.constant.Configurations;
import org.regminer.commons.constant.Constant;
import org.regminer.commons.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: sxz
 * @Date: 2023/11/29/13:46
 * @Description:
 */
public class MigratorUtil {

    private static final Logger LOGGER = LogManager.getLogger(MigratorUtil.class);

    public static File checkoutCiForBFC(String bfcId, String commitId) throws IOException {
        File codeDir = FileUtilx.getDirFromBfcAndBic(bfcId, commitId);
        FileUtils.copyDirectory(new File(Configurations.metaPath), codeDir);
        GitUtils.checkout(commitId, codeDir);
        return codeDir;
    }

    public static void mergeTwoVersion_BaseLine(PotentialBFC pRFC, File tDir) {
        /**
         *
         * 注意！bfc的patch中可能存在 普通java文件，测试文件（相关测试用例，非测试用例但测试目录下的java文件），配置文件(测试目录下的，其他)
         * 在base_line中我们只迁移 测试文件，和之前不存在的配置文件（暂时不做文本的merge）
         */
        List<TestFile> testSuite = pRFC.getTestCaseFiles();
        // 非测试用例的在测试目录下的其他文件

        //###XXX:TestDenpendency BlocK
        List<TestFile> underTestDirJavaFiles = pRFC.getTestRelates();
        List<SourceFile> sourceFiles = pRFC.getSourceFiles();
        //##block end

        // merge测试文件
        // 整合任务
        MergeTask mergeJavaFileTask = new MergeTask();
        mergeJavaFileTask.addAll(testSuite).addAll(underTestDirJavaFiles).addAll(sourceFiles).compute();//XXX

        Set<String> commits = mergeJavaFileTask.getElementList().stream().map(ChangedFile::getNewCommitId).collect(Collectors.toSet());
        LOGGER.info("migrate testFiles from {} to {}", commits, tDir);

        // :TestDenpendency BlocK
        File bfcDir = pRFC.fileMap.get(pRFC.getCommit().getName());
        mergeTestFiles(bfcDir, tDir, testSuite, underTestDirJavaFiles, sourceFiles);
    }

    private static void mergeTestFiles(File bfcDir, File tDir, List<TestFile> testSuite, List<TestFile> underTestDirJavaFiles, List<SourceFile> sourceFiles) {
        // merge测试文件
        // 整合任务
        MergeTask mergeJavaFileTask = new MergeTask();
        mergeJavaFileTask.addAll(testSuite).addAll(underTestDirJavaFiles).addAll(sourceFiles).compute();//XXX
        // :TestDenpendency BlocK
        for (Map.Entry<String, ChangedFile> entry : mergeJavaFileTask.getMap().entrySet()) {
            String newPathInBfc = entry.getKey();
            if (newPathInBfc.contains(Constant.NONE_PATH)) {
                continue;
            }
            String fileContent = GitUtils.getFileContentAtCommit(tDir, entry.getValue().getNewCommitId(), newPathInBfc);
            if (fileContent == null) {
                continue; // 文件在指定 commit 中不存在
            }

            File tFile = new File(tDir, newPathInBfc);
            try {
                if (tFile.exists()) {
                    FileUtils.deleteQuietly(tFile);
                }
                // 直接copy过去
                if (!tFile.getParentFile().exists()) {
                    tFile.getParentFile().mkdirs();
                }
                FileUtils.writeStringToFile(tFile, fileContent, StandardCharsets.UTF_8, false);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void mergeFiles(Map<String, String> path2ContentMap, File tDir) {
        for (Map.Entry<String, String> entry : path2ContentMap.entrySet()) {
            String newPathInBfc = entry.getKey();
            if (newPathInBfc.contains(Constant.NONE_PATH)) {
                continue;
            }
            String fileContent = entry.getValue();
            File tFile = new File(tDir, newPathInBfc);
            try {
                if (tFile.exists()) {
                    FileUtils.deleteQuietly(tFile);
                }
                // 直接copy过去
                if (!tFile.getParentFile().exists()) {
                    tFile.getParentFile().mkdirs();
                }
                FileUtils.writeStringToFile(tFile, fileContent, StandardCharsets.UTF_8, false);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * @param pRFC
     * @param targetProjectDirectory
     * @throws IOException
     */
    public void copyToTarget(PotentialBFC pRFC, File targetProjectDirectory) throws IOException {
        // copy
        String targetPath = null;
        File bfcFile = pRFC.fileMap.get(pRFC.getCommit().getName());
        List<ChangedFile> taskFiles = new ArrayList<>();
        //now none test file be remove
        //test Related file is removed after test bfc
        taskFiles.addAll(pRFC.getTestCaseFiles());

        //###XXX:TestDenpendency BlocK
        taskFiles.addAll(pRFC.getTestRelates());
        taskFiles.addAll(pRFC.getSourceFiles());
        //####Block end#####

        for (ChangedFile cFile : taskFiles) {
            File file = new File(bfcFile, cFile.getNewPath());
            // 测试文件是被删除则什么也不作。
            if (cFile.getNewPath().contains(Constant.NONE_PATH)) {
                continue;
            }
            targetPath = cFile.getNewPath();
            // 测试文件不是删除，则copy
            targetPath = FileUtilx.getDirectoryFromPath(targetPath);
            File file1 = new File(targetProjectDirectory, targetPath);
            if (!file1.exists()) {
                file1.mkdirs();
            }
            FileUtils.copyFileToDirectory(file, file1);
        }
    }

    public static void purgeUnlessTestFile(File tDir, List<String> relatedFileList) {
        // 从 tDir 中，删除所有不在 relatedFileList 中的 test 文件
        List<String> fileNameList = relatedFileList.stream().map(s -> getFileName(s)).collect(Collectors.toList());
        // 递归处理目录及其子目录
        processDirectory(tDir, fileNameList);
    }

    private static void processDirectory(File directory, List<String> fileNameList) {
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && testFilter(file.getPath()) && file.getName().endsWith(".java")
                        && !fileNameList.contains(file.getName().toLowerCase())) {
                    // 如果文件是测试文件并且不在相关文件列表中，则删除
                    file.delete();
                } else if (file.isDirectory()) {
                    // 如果是子目录，递归处理
                    processDirectory(file, fileNameList);
                }
            }
        }
    }

    private static String getFileName(String path) {
        String[] strs = path.split("/");
        return strs[strs.length - 1].toLowerCase();
    }

    public static boolean testFilter(String path) {
        String fileName = getFileName(path);
        // test 目录下的 test 文件
        return (path.toLowerCase().contains("/test/") || path.toLowerCase().contains("/tests/")) &&
                (fileName.toLowerCase().startsWith("test") || fileName.toLowerCase().endsWith("test.java") || fileName.toLowerCase().endsWith("tests.java"));
    }

    public static void purgeUnlessTestcase(List<TestFile> testSuiteList, PotentialBFC pRFC) {
        File bfcDir = pRFC.fileMap.get(pRFC.getCommit().getName());
        for (TestFile testFile : testSuiteList) {
            String path = testFile.getNewPath();
            File file = new File(bfcDir, path);
            try {
                CompilationUnit unit = CompilationUtil.parseCompliationUnit(FileUtils.readFileToString(file,
                        "UTF-8"));
                Set<String> testCaseSet = testFile.getTestMethodMap().keySet();
                List<TypeDeclaration> types = unit.types();
                for (TypeDeclaration type : types) {
                    MethodDeclaration[] mdArray = type.getMethods();
                    for (int i = 0; i < mdArray.length; i++) {
                        MethodDeclaration method = mdArray[i];
                        String name = method.getName().toString();
                        List<ASTNode> parameters = method.parameters();
                        // SingleVariableDeclaration
                        StringJoiner sj = new StringJoiner(",", name + "(", ")");
                        for (ASTNode param : parameters) {
                            sj.add(param.toString());
                        }
                        String signature = sj.toString();
                        if ((method.toString().contains("@Test") || name.startsWith("test") || name.endsWith("test")) && !testCaseSet.contains(signature)) {
                            method.delete();
                        }
                    }
                }
                List<ImportDeclaration> imports = unit.imports();
                int len = imports.size();
                ImportDeclaration[] importDeclarations = new ImportDeclaration[len];
                for (int i = 0; i < len; i++) {
                    importDeclarations[i] = imports.get(i);
                }

                for (ImportDeclaration importDeclaration : importDeclarations) {
                    String importName = importDeclaration.getName().getFullyQualifiedName();
                    if (importName.lastIndexOf(".") > -1) {
                        importName = importName.substring(importName.lastIndexOf(".") + 1);
                    } else {
                        importName = importName;
                    }

                    boolean flag = false;
                    for (TypeDeclaration type : types) {
                        if (type.toString().contains(importName)) {
                            flag = true;
                            break;
                        }
                    }
                    if (!(flag || importDeclaration.toString().contains("*"))) {
                        importDeclaration.delete();
                    }
                }
                if (file.exists()) {
                    file.delete();
                }
                FileUtils.writeStringToFile(file, unit.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

package org.regminer.migrate.api;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.dom.*;
import org.regminer.common.constant.Configurations;
import org.regminer.common.constant.Constant;
import org.regminer.common.model.ChangedFile;
import org.regminer.common.model.PotentialBFC;
import org.regminer.common.model.SourceFile;
import org.regminer.common.model.TestFile;
import org.regminer.common.utils.CompilationUtil;
import org.regminer.common.utils.FileUtilx;
import org.regminer.common.utils.GitUtils;
import org.regminer.migrate.model.MergeTask;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: sxz
 * @Date: 2023/11/29/13:46
 * @Description:
 */
public class Migrator {

    private static final Logger LOGGER = LogManager.getLogger(Migrator.class);
    public File checkoutCiForBFC(String bfcId, String commitId) throws IOException {
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
        // 根据需要迁移的测试文件所在的 commit 进行分组
        // 相关测试用例
        Map<String, List<TestFile>> testSuiteMap = pRFC.getTestCaseFiles()
                .stream().collect(Collectors.groupingBy(TestFile::getNewCommitId));
        // 非测试用例的在测试目录下的其他文件

        //###XXX:TestDenpendency BlocK
        Map<String, List<TestFile>> underTestDirJavaFilesMap = pRFC.getTestRelates()
                .stream().collect(Collectors.groupingBy(TestFile::getNewCommitId));
        Map<String, List<SourceFile>> sourceFilesMap = pRFC.getSourceFiles()
                .stream().collect(Collectors.groupingBy(SourceFile::getNewCommitId));
        //##block end
        File bfcDir = pRFC.fileMap.get("BASE");
        String head = GitUtils.getHead(bfcDir);
        testSuiteMap.forEach((s, testFiles) -> {
            LOGGER.info("migrate testFiles from {} to {}", s, pRFC.getCommit().getName());
            GitUtils.checkout(s, bfcDir);
            mergeTestFiles(bfcDir, tDir, testFiles,
                    underTestDirJavaFilesMap.getOrDefault(s, new ArrayList<>()),
                    sourceFilesMap.getOrDefault(s, new ArrayList<>()));
        });
        // checkout 回去
        GitUtils.checkout(head, bfcDir);
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
            File bfcFile = new File(bfcDir, newPathInBfc);
            File tFile = new File(tDir, newPathInBfc);
            try {
                if (tFile.exists()) {
                    FileUtils.deleteQuietly(tFile);
                }
                // 直接copy过去
                if (!tFile.getParentFile().exists()) {
                    tFile.getParentFile().mkdirs();
                }
                Files.copy(bfcFile.toPath(), tFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
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

    public void purgeUnlessTestcase(List<TestFile> testSuiteList, PotentialBFC pRFC) {
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

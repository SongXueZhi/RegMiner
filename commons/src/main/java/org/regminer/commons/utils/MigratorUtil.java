package org.regminer.commons.utils;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.regminer.commons.code.analysis.SpoonCodeAnalyst;
import org.regminer.commons.constant.Configurations;
import org.regminer.commons.constant.Constant;
import org.regminer.commons.model.*;
import spoon.Launcher;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.declaration.*;
import spoon.reflect.visitor.filter.TypeFilter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
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

    public static void mergeTwoVersion_BaseLine(PotentialBFC pRFC, File tDir) throws IOException {
        /**
         *
         * 注意！bfc的patch中可能存在 普通java文件，测试文件（相关测试用例，非测试用例但测试目录下的java文件），配置文件(测试目录下的，其他)
         * 在base_line中我们只迁移 测试文件，和之前不存在的配置文件（暂时不做文本的merge）
         */
        List<TestSuiteFile> testSuite = pRFC.getTestSuiteFiles();
        // 非测试用例的在测试目录下的其他文件

        //###XXX:TestDenpendency BlocK
        List<TestSourceFile> underTestDirJavaFiles = pRFC.getTestSourceFiles().stream().filter(
                testSourceFile -> testSourceFile.getType() == ChangedFile.Type.TEST_DEPEND).collect(Collectors.toList());

        List<ResourceOrConfigFile> resourceOrConfigFiles = pRFC.getResourceOrConfigFiles();
        //##block end

        // merge测试文件
        // 整合任务
        MergeTask mergeJavaFileTask = new MergeTask();
        mergeJavaFileTask.addAll(testSuite).addAll(underTestDirJavaFiles).addAll(resourceOrConfigFiles).compute();//XXX

        Set<String> commits =
                mergeJavaFileTask.getElementList().stream().map(ChangedFile::getNewCommitId).collect(Collectors.toSet());
        LOGGER.info("migrate testFiles from {} to {}", commits, tDir);

        // :TestDenpendency BlocK
        File bfcDir = Path.of(pRFC.fileMap.get(pRFC.getCommit().getName())).toFile();
        mergeTestFiles(bfcDir, tDir, testSuite, underTestDirJavaFiles, resourceOrConfigFiles);
    }

    private static void mergeTestFiles(File bfcDir, File tDir, List<TestSuiteFile> testSuite,
                                       List<TestSourceFile> underTestDirJavaFiles, List<ResourceOrConfigFile> resourceOrConfigFiles) throws IOException {
        // 合并测试文件
        MergeTask mergeJavaFileTask = new MergeTask();
        mergeJavaFileTask.addAll(testSuite).addAll(underTestDirJavaFiles).addAll(resourceOrConfigFiles).compute();

        for (Map.Entry<String, ChangedFile> entry : mergeJavaFileTask.getMap().entrySet()) {
            String newPathInBfc = entry.getKey();
            if (newPathInBfc.contains(Constant.NONE_PATH)) {
                continue;
            }
            // 确定源文件路径
            Path pathFrom = Path.of(bfcDir.getAbsolutePath(), newPathInBfc);

            // 确保目标文件夹存在
            createDirectoriesIfNotExists(pathFrom.getParent());

            // 如果文件在BFC目录中不存在，从commit中获取文件内容并写入
            if (!Files.exists(pathFrom)) {
                String fileContent = GitUtils.getFileContentAtCommit(tDir, entry.getValue().getNewCommitId(), newPathInBfc);
                if (fileContent == null) {
                    continue; // 文件在指定 commit 中不存在
                }
                Files.writeString(pathFrom, fileContent, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
            }

            // 确定目标文件路径并复制文件
            Path pathTo = Path.of(tDir.getAbsolutePath(), newPathInBfc);
            try {
                createDirectoriesIfNotExists(pathTo.getParent());
                Files.copy(pathFrom, pathTo, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                LOGGER.error("Error copying file from {} to {}: {}", pathFrom, pathTo, e.getMessage());
            }
        }
    }

    private static void createDirectoriesIfNotExists(Path path) throws IOException {
        if (path != null && !Files.exists(path)) {
            Files.createDirectories(path);
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

    public static void purgeUnlessTestcase(List<TestSuiteFile> testSuiteList, PotentialBFC pRFC) {
        SpoonCodeAnalyst spoonCodeAnalyst = new SpoonCodeAnalyst();
        File bfcDir = Path.of(pRFC.fileMap.get(pRFC.getCommit().getName())).toFile();

        for (TestSuiteFile testSuiteFile : testSuiteList) {
            File file = new File(bfcDir, testSuiteFile.getNewPath());
            Launcher launcher = spoonCodeAnalyst.modelCode(file.getAbsolutePath());
            CtCompilationUnit compilationUnit = launcher.getFactory().CompilationUnit().getMap().get(file.getAbsolutePath());

            Set<String> testCaseSet = testSuiteFile.getTestMethodMap().keySet();
            removeUnusedTestMethods(compilationUnit, testCaseSet);
            Graph<CtMethod<?>, DefaultEdge> graph = buildTestCallGraph(compilationUnit);
            Set<CtMethod<?>> vertexSet = graph.vertexSet();
            removeUnusedMethods(compilationUnit, vertexSet);
            removeUnusedFields(compilationUnit, vertexSet);
            removeUnusedImports(compilationUnit);
            compilationUnit.updateAllParentsBelow();
            writeToFile(file, compilationUnit.prettyprint());
        }
    }

    private static void removeUnusedTestMethods(CtCompilationUnit compilationUnit, Set<String> testCaseSet) {
        compilationUnit.getDeclaredTypes().forEach(ctType ->
                ctType.getMethods().forEach(ctMethod -> {
                    String name = ctMethod.getSimpleName();
                    String signature = ctMethod.getSignature();
                    if (isTestMethod(ctMethod, name) && !testCaseSet.contains(signature)) {
                        ctMethod.delete();
                    }
                })
        );
    }

    private static boolean isTestMethod(CtMethod<?> ctMethod, String name) {
        return ctMethod.toString().contains("@Test") || name.startsWith("test") || name.endsWith("Test");
    }


    private  static void removeUnusedFields(CtCompilationUnit compilationUnit, Set<CtMethod<?>> methodSet) {
        Set<CtField<?>> reachableFieldSet = new HashSet<>();
        methodSet.forEach(
                ctMethod -> {
                    ctMethod.getElements(new TypeFilter<>(CtFieldAccess.class)).forEach(
                            ctFieldAccess -> {
                                reachableFieldSet.add(ctFieldAccess.getVariable().getFieldDeclaration());
                            }
                    );
                }
        );
        compilationUnit.getDeclaredTypes().forEach(ctType -> {
            ctType.getFields().forEach(ctField -> {
                if (!reachableFieldSet.contains(ctField)) {
                    ctField.delete();
                }
            });
        });
    }


    private  static   Graph<CtMethod<?>, DefaultEdge>  buildTestCallGraph(CtCompilationUnit compilationUnit){
        Set<CtMethod<?>> ctMethods = new HashSet<>();
        compilationUnit.getDeclaredTypes().forEach(ctType -> {
                    ctType.getMethods().forEach(ctMethod -> {
                        String name = ctMethod.getSimpleName();
                        if (isTestMethod(ctMethod, name)) {
                            ctMethods.add(ctMethod);
                        }
                    });
                }
        );
        SpoonCodeAnalyst spoonCodeAnalyst = new SpoonCodeAnalyst();
        return spoonCodeAnalyst.buildCallGraph(new LinkedList<>(ctMethods),
                new HashSet<>());

    }

    private static void removeUnusedMethods(CtCompilationUnit compilationUnit,Set<CtMethod<?>> vertexSet) {
        compilationUnit.getDeclaredTypes().forEach(ctType -> {
            ctType.getMethods().forEach(ctMethod -> {
                if (!vertexSet.contains(ctMethod)) {
                    ctMethod.delete();
                }
            });
        });
    }



    private static void removeUnusedImports(CtCompilationUnit compilationUnit) {
        List<CtImport> importsToRemove = new ArrayList<>();
        List<CtImport> imports = compilationUnit.getImports();

        for (CtImport ctImport : imports) {
            String importName = extractImportName(ctImport);
            boolean isUsed = compilationUnit.getDeclaredTypes().stream()
                    .anyMatch(type -> type.toString().contains(importName));

            if (!isUsed && !ctImport.toString().contains("*")) {
                importsToRemove.add(ctImport);
            }
        }

        importsToRemove.forEach(CtImport::delete);
    }

    private static String extractImportName(CtImport ctImport) {
        String importName = ctImport.toString();
        int lastDotIndex = importName.lastIndexOf(".");
        if (lastDotIndex > -1) {
            importName = importName.substring(lastDotIndex + 1).replace(";", "");
        }
        return importName;
    }

    private static void writeToFile(File file, String content) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(content);
        } catch (IOException e) {
            LOGGER.error("Failed to write to file: {}", file.getAbsolutePath());
        }
    }
}

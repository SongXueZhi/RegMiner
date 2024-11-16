package org.regminer.ct.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.lib.Repository;
import org.regminer.commons.code.analysis.SpoonCodeAnalyst;
import org.regminer.commons.constant.Configurations;
import org.regminer.commons.constant.Constant;
import org.regminer.commons.model.ChangedFile.Type;
import org.regminer.commons.model.PotentialBFC;
import org.regminer.commons.model.RelatedTestCase;
import org.regminer.commons.model.TestSourceFile;
import org.regminer.commons.model.TestSuiteFile;
import org.regminer.commons.tool.RepositoryProvider;
import org.regminer.commons.utils.ChangedFileUtil;
import org.regminer.commons.utils.FileUtilx;
import org.regminer.commons.utils.MigratorUtil;
import spoon.reflect.declaration.CtMethod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

//获取每一个测试文件中的测试方法（暂时不用），并且过滤测试文件是否真实
//如果不包含junit或者@test则移除
//过滤完成后，如果若有测试文件都被移除，则pRFC移除
public class TestCaseParser {
    protected Logger logger = LogManager.getLogger(this);


    private void handlePotentialTestFile(PotentialBFC pRFC) {
        try (Repository repo = RepositoryProvider.getRepoFromLocal(new File(Configurations.projectPath));
             Git git = new Git(repo)) {
            if (!ChangedFileUtil.searchPotentialTestFiles(pRFC.getCommit(), git, pRFC.getTestSourceFiles(),
                    pRFC.getResourceOrConfigFiles())) {
                logger.warn("no commits that only involve modifications to test files in the {} commits before and " +
                                "after commit '{}'.",
                        Constant.SEARCH_DEPTH * 2, pRFC.getCommit());
            }
        } catch (Exception e) {
            logger.error("search test files failed, error message is: {}", e.getMessage());
        }
    }

    // 现在每个测试文件被分为测试相关和测试文件。
    public void parseTestCases(PotentialBFC pRFC) {
        File bfcDir = Path.of(pRFC.fileMap.get(pRFC.getCommit().getName())).toFile();
        // Prepare for no testcase in bfc but in range of (c~2,c^2)
        // 可能在这里找不到测试，需要尝试在本次 commit 四周寻找是否有单独的新增测试
        if (pRFC.getTestcaseFrom() == PotentialBFC.TESTCASE_FROM_SEARCH) {
            logger.info("BFC doesn't contains TestCases, try to search");
            handlePotentialTestFile(pRFC);
        }

        if (pRFC.getTestSourceFiles().stream()
                .noneMatch(testFile -> pRFC.getCommit().getName().equals(testFile.getNewCommitId()))) {
            // 不包含当前 commit 中的测试文件时，测试前也需要迁移测试文件
            // 先迁移，再解析 4. 否则解析结果可能和 git 提供的文件修改记录对应不上
            try {
                MigratorUtil.mergeTwoVersion_BaseLine(pRFC, bfcDir);
            } catch (IOException e) {
                logger.error("merge test files failed, error message is: {}", e.getMessage());
            }
        }
        List<TestSuiteFile> testSuiteFiles = new ArrayList<>();
        pRFC.setTestSuiteFiles(testSuiteFiles);
        Iterator<TestSourceFile> iterator = pRFC.getTestSourceFiles().iterator();
        while (iterator.hasNext()) {
            TestSourceFile testSourceFile = iterator.next();
            if (testSourceFile.getNewPath().equals(Constant.NONE_PATH)) {
                continue;
            }
            String code = FileUtilx.readContentFromFile(new File(bfcDir, testSourceFile.getNewPath()));
            if (code == null) {
                continue;
            }
            if (!isTestSuite(code)) {
                testSourceFile.setType(Type.TEST_DEPEND);
            } else {
                testSourceFile.setType(Type.TEST_SUITE);
                TestSuiteFile testSuiteFile = new TestSuiteFile(testSourceFile);
                Map<String, RelatedTestCase> methodMap = parse(testSuiteFile, bfcDir.getAbsolutePath());
                testSuiteFile.getTestMethodMap().clear();
                testSuiteFile.getTestMethodMap().putAll(methodMap);
                testSuiteFiles.add(testSuiteFile);
                iterator.remove();
            }
        }
        pRFC.getTestSourceFiles().addAll(testSuiteFiles);//保持原有的文件
    }


    private Map<String, RelatedTestCase> parse(TestSourceFile file, String dirPath) {
        SpoonCodeAnalyst spoonCodeAnalyst = new SpoonCodeAnalyst();
        List<Edit> editList = file.getEditList();

//		cleanEmpty(editList);
        List<CtMethod<?>> methodList =
                spoonCodeAnalyst.getMethods(spoonCodeAnalyst.modelCode(dirPath + File.separator + file.getNewPath()).getModel());
        Map<String, RelatedTestCase> testCaseMap = new HashMap<>();
        getTestMethod(editList, methodList, testCaseMap);
        testCaseMap.forEach((s, testCase) -> testCase.setRelativeFilePath(file.getNewPath()));
        return testCaseMap;
    }

    private void getTestMethod(List<Edit> editList, List<CtMethod<?>> methodList,
                               Map<String, RelatedTestCase> testCaseMap) {
        for (Edit edit : editList) {
            matchAll(edit, methodList, testCaseMap);
        }
    }

    private void matchAll(Edit edit, List<CtMethod<?>> methods, Map<String, RelatedTestCase> testCaseMap) {
        for (CtMethod<?> method : methods) {
            match(edit, method, testCaseMap);
        }
    }

    //
    private void match(Edit edit, CtMethod<?> method, Map<String, RelatedTestCase> testCaseMap) {
        int editStart = edit.getBeginB() + 1;
        int editEnd = edit.getEndB();

        int methodStart = method.getPosition().getLine();
        int methodStop = method.getPosition().getEndLine();

        if (editStart <= methodStart && editEnd >= methodStop || editStart >= methodStart && editEnd <= methodStop
                || editEnd >= methodStart && editEnd <= methodStop
                || editStart >= methodStart && editStart <= methodStop) {
            String name = method.getSignature();
            if (!testCaseMap.containsKey(name)) {
                RelatedTestCase testCase = new RelatedTestCase();
                // 暂时不设定方法的类型
                // testCase.setType(RelatedTestCase.Type.Created);
                testCase.setEnclosingClassName(method.getDeclaringType().getQualifiedName());
                testCase.setMethod(method);
                testCase.setMethodName(method.getSimpleName());
                testCaseMap.put(name, testCase);
            }
        }

    }

    public boolean isTestSuite(String code) {
        if (code.contains("org.junit") || code.contains("org.testng") || code.contains("@Test")) {
            return true;
        }
        //in some cases, there's no junit or testng marks, but it's a test file
        Pattern testClassPattern = Pattern.compile("public\\s+class\\s+.*[tT]est");
        Pattern testMethodPattern = Pattern.compile("public\\s+void\\s+.*[tT]est");
        return testClassPattern.matcher(code).find() && testMethodPattern.matcher(code).find();
    }
}

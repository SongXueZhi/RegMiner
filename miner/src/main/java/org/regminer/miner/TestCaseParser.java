package org.regminer.miner;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.diff.Edit;
import org.jetbrains.annotations.NotNull;
import org.regminer.common.constant.Constant;
import org.regminer.common.model.*;
import org.regminer.common.model.ChangedFile.Type;
import org.regminer.common.utils.CompilationUtil;
import org.regminer.common.utils.FileUtilx;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

//获取每一个测试文件中的测试方法（暂时不用），并且过滤测试文件是否真实
//如果不包含junit或者@test则移除
//过滤完成后，如果若有测试文件都被移除，则pRFC移除
public class TestCaseParser {

    public void handlePotentialTestFile(@NotNull List<PotentialTestCase> potentialTestCaseList, File bfcDir, PotentialBFC pRFC){
        for (PotentialTestCase potentialTestCase : potentialTestCaseList) {
            //if index > 0 ,test file in (c,c+2),we need copy test file to bfcdir
            //file map size > 0, meaning have test file need to copy
            if (potentialTestCase.getIndex() > 0 && !potentialTestCase.fileMap.isEmpty()) {
                List<TestFile> testFiles = potentialTestCase.getTestFiles();
                List<SourceFile> sourceFiles = potentialTestCase.getSourceFiles();
                copyPotentialTestFileToBFC(testFiles,bfcDir,potentialTestCase);
                copyPotentialTestFileToBFC(sourceFiles,bfcDir,potentialTestCase);
                pRFC.setTestCaseFiles(testFiles);
                pRFC.setSourceFiles(sourceFiles);
            }
        }

    }
    private void copyPotentialTestFileToBFC(List<? extends ChangedFile> files, File bfcDir, PotentialTestCase potentialTestCase){
        Iterator<? extends ChangedFile> iterator = files.iterator();
        while (iterator.hasNext()){
            ChangedFile changedFile = iterator.next();
            try {
                FileUtils.copyToDirectory(potentialTestCase.fileMap.get(changedFile.getNewPath()),bfcDir);
            } catch (Exception e){
                iterator.remove();
                e.printStackTrace();
            }
        }
    }

    // 现在每个测试文件被分为测试相关和测试文件。
    public void parseTestCases(PotentialBFC pRFC) {
        File bfcDir = pRFC.fileMap.get(pRFC.getCommit().getName());
        // Prepare for no testcase in bfc but in range of (c~2,c^2)
        if (pRFC.getTestcaseFrom() == PotentialBFC.TESTCASE_FROM_SEARCH) {
            handlePotentialTestFile(pRFC.getPotentialTestCaseList(),bfcDir,pRFC);
        }

//        System.out.println("prfc testcase file size: " + pRFC.getTestCaseFiles().size());
        Iterator<TestFile> iterator = pRFC.getTestCaseFiles().iterator();
        while (iterator.hasNext()) {
            TestFile file = iterator.next();
            if (file.getNewPath().equals(Constant.NONE_PATH)){
                continue;
            }
            String code = FileUtilx.readContentFromFile(new File(bfcDir,file.getNewPath()));
            if (code == null) {
                continue;
            }
            if (!isTestSuite(code)) {
//                System.out.println("remove here!");
                file.setType(Type.TEST_RELATE);
                pRFC.getTestRelates().add(file);
                iterator.remove();
            } else {
                file.setType(Type.TEST_SUITE);
                file.setQualityClassName(CompilationUtil.getQualityClassName(code));
                Map<String, RelatedTestCase> methodMap = parse(file, code);
                file.setTestMethodMap(methodMap);
            }
        }
//        System.out.println("prfc testcase file size(after parsing): " + pRFC.getTestCaseFiles().size());
    }

    private Map<String, RelatedTestCase> parse(TestFile file, String code) {
        List<Edit> editList = file.getEditList();
//		cleanEmpty(editList);
        List<Methodx> methodList = CompilationUtil.getAllMethod(code);
        Map<String, RelatedTestCase> testCaseMap = new HashMap<>();
        getTestMethod(editList, methodList, testCaseMap);
        return testCaseMap;
    }

    private void getTestMethod(List<Edit> editList, List<Methodx> methodList,
                               Map<String, RelatedTestCase> testCaseMap) {
        for (Edit edit : editList) {
            matchAll(edit, methodList, testCaseMap);
        }
    }

    private void matchAll(Edit edit, List<Methodx> methods, Map<String, RelatedTestCase> testCaseMap) {
        for (Methodx method : methods) {
            match(edit, method, testCaseMap);
        }
    }

    //
    private void match(Edit edit, Methodx method, Map<String, RelatedTestCase> testCaseMap) {
        int editStart = edit.getBeginB()+1;
        int editEnd = edit.getEndB();

        int methodStart = method.getStartLine();
        int methodStop = method.getStopLine();

        if (editStart <= methodStart && editEnd >= methodStop || editStart >= methodStart && editEnd <= methodStop
                || editEnd >= methodStart && editEnd <= methodStop
                || editStart >= methodStart && editStart <= methodStop) {
            String name = method.getSignature();
            if (!testCaseMap.containsKey(name)) {
                RelatedTestCase testCase = new RelatedTestCase();
                // 暂时不设定方法的类型
                // testCase.setType(RelatedTestCase.Type.Created);
                testCase.setMethod(method);
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

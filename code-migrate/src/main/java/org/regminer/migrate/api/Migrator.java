package org.regminer.migrate.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.regminer.common.constant.Configurations;
import org.regminer.common.constant.Constant;
import org.regminer.common.model.ChangedFile;
import org.regminer.common.model.PotentialBFC;
import org.regminer.common.model.SourceFile;
import org.regminer.common.model.TestFile;
import org.regminer.common.utils.FileUtilx;
import org.regminer.common.utils.GitUtils;
import org.regminer.migrate.model.MergeTask;

/**
 * @Author: sxz
 * @Date: 2023/11/29/13:46
 * @Description:
 */
public class Migrator {
    public File checkoutCiForBFC(String bfcId, String commitId) throws IOException {
        File codeDir = FileUtilx.getDirFromBfcAndBic(bfcId, commitId);
        FileUtils.copyDirectoryToDirectory(new File(Configurations.META_PATH), codeDir);
        GitUtils.checkout(commitId,codeDir);
        return codeDir;
    }
    public void mergeTwoVersion_BaseLine(PotentialBFC pRFC, File tDir) {
        /**
         *
         * 注意！bfc的patch中可能存在 普通java文件，测试文件（相关测试用例，非测试用例但测试目录下的java文件），配置文件(测试目录下的，其他)
         * 在base_line中我们只迁移 测试文件，和之前不存在的配置文件（暂时不做文本的merge）
         */
        // 相关测试用例
        List<TestFile> testSuite = pRFC.getTestCaseFiles();
        // 非测试用例的在测试目录下的其他文件

        //###XXX:TestDenpendency BlocK
        List<TestFile> underTestDirJavaFiles = pRFC.getTestRelates();
        List<SourceFile> sourceFiles = pRFC.getSourceFiles();
        //##block end

        // merge测试文件
        // 整合任务
        MergeTask mergeJavaFileTask = new MergeTask();
        mergeJavaFileTask.addAll(testSuite).addAll(underTestDirJavaFiles).addAll(sourceFiles).compute();//XXX:TestDenpendency BlocK
        File bfcDir = pRFC.fileMap.get(pRFC.getCommit().getName());
        for (Map.Entry<String, ChangedFile> entry : mergeJavaFileTask.getMap().entrySet()) {
            String newPathInBfc = entry.getKey();
            if (newPathInBfc.contains(Constant.NONE_PATH)) {
                continue;
            }
            File bfcFile = new File(bfcDir, newPathInBfc);
            File tFile = new File(tDir, newPathInBfc);
            if (tFile.exists()) {
                tFile.deleteOnExit();
            }
            // 直接copy过去
            try {
                FileUtils.forceMkdirParent(tFile);
                FileUtils.copyFileToDirectory(bfcFile, tFile.getParentFile());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

    }

    /**
     *
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
}
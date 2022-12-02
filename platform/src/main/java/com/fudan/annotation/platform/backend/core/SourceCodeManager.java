package com.fudan.annotation.platform.backend.core;

import com.fudan.annotation.platform.backend.entity.Revision;
import com.fudan.annotation.platform.backend.util.FileUtil;
import com.fudan.annotation.platform.backend.util.GitUtil;
import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.DirectoryScanner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * description:
 *
 * @author Richy
 * create: 2022-02-23 20:00
 **/
@Component
public class SourceCodeManager {

    private final static String workSpace = System.getProperty("user.home") + File.separator + "data" + File.separator +
            "miner_space";

    public static String metaProjectsDirPath = workSpace + File.separator + "meta_projects";
    public static String cacheProjectsDirPath = workSpace + File.separator + "transfer_cache";

    public File getProjectDir(String projectUuid, String regressionUuid, String userToken) {
//        String projectDirName = projectFullName.replace("/", "_");
        checkRequiredDir();
        File projectDir = new File(metaProjectsDirPath + File.separator + projectUuid);
        if (projectDir.exists()) {
            return projectDir;
        } else {
//            try {
//                GitUtil.clone(projectDir, "https://github.com/" + projectFullName + ".git");
//                return projectDir;
//            } catch (Exception exception) {
//                System.out.println(exception.getMessage());
//            }
            return null;
        }
    }

    public File getMetaProjectDir(String projectUuid) {
        return new File(metaProjectsDirPath + File.separator + projectUuid);
    }

    public File getCodeDir(String regressionUuid, String userToken, String revisionFlag) {
        return new File(cacheProjectsDirPath + File.separator + userToken + File.separator + regressionUuid + File.separator + revisionFlag);
    }

    public File checkout(Revision revision, File projectFile, String regressionUuid, String userToken) {
        //copy source code from meta project dir
        File projectCacheDir =
                new File(cacheProjectsDirPath + File.separator + userToken + File.separator + regressionUuid);
        if (projectCacheDir.exists() && !projectCacheDir.isDirectory()) {
            projectCacheDir.delete();
        }
        projectCacheDir.mkdirs();

        File revisionDir = new File(projectCacheDir, revision.getRevisionName());
        try {
            if (revisionDir.exists()) {
                FileUtils.forceDelete(revisionDir);
            }
            FileUtils.copyDirectoryToDirectory(projectFile, projectCacheDir);

        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
        new File(projectCacheDir, projectFile.getName()).renameTo(revisionDir);
        //git checkout
        if (GitUtil.checkout(revision.getCommitID(), revisionDir)) {
            return revisionDir;
        }
        return null;
    }

    private void checkRequiredDir() {
        File metaProjectsDir = new File(metaProjectsDirPath);

        if (metaProjectsDir.exists()) {
            if (!metaProjectsDir.isDirectory()) {
                metaProjectsDir.delete();
                metaProjectsDir.mkdirs();
            }
        } else {
            metaProjectsDir.mkdirs();
        }

        File cacheProjectsDir = new File(cacheProjectsDirPath);
        if (cacheProjectsDir.exists()) {
            if (!cacheProjectsDir.isDirectory()) {
                cacheProjectsDir.delete();
                cacheProjectsDir.mkdirs();
            }
        } else {
            cacheProjectsDir.mkdirs();
        }
    }

    public File getCacheProjectDir(String userToken, String regressionUuid, String revisionFlag, String filePath) {
        return new File(cacheProjectsDirPath + File.separator + userToken + File.separator +
                regressionUuid + File.separator + revisionFlag + File.separator + filePath);
    }

    public String getTestCasePath(String userToken, String regressionUuid, String revisionFlag, String testCase) throws FileNotFoundException {
        File file = new File(cacheProjectsDirPath + File.separator + userToken + File.separator +
                regressionUuid + File.separator + revisionFlag);
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(file);
        scanner.setIncludes(new String[]{"**\\" + testCase.split("#")[0].replace(".", "/") + ".java"});
        scanner.setCaseSensitive(true);
        scanner.scan();
        return scanner.getIncludedFiles()[0];

    }

    public File getRegressionDir(String regressionUuid, String userToken) {
        return new File(cacheProjectsDirPath + File.separator + userToken + File.separator + regressionUuid);
    }

    public File getRevisionDir(String regressionUuid, String userToken, String revisionFlag) {
        return new File(cacheProjectsDirPath + File.separator + userToken + File.separator +
                regressionUuid + File.separator + revisionFlag);
    }

    public String getRevisionCode(File revisionFile) {
//        方法1：
        String str = "";
        try {
            str = FileUtils.readFileToString(revisionFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(str);

        return str;

//         //方法2
//        StringBuffer stringBuffer = new StringBuffer();
//        BufferedReader bfReader = null;
//        String revisionCode = "";
//        int line = 1;
//        try {
////            BufferedReader bf = new BufferedReader(new FileReader(revisionFile));
//            if (revisionFile.exists() && revisionFile.isFile()) {
//                FileInputStream fileIn = new FileInputStream(revisionFile);
//                bfReader = new BufferedReader(new InputStreamReader(fileIn));
//                while ((revisionCode = bfReader.readLine()) != null) {
//                    stringBuffer.append("\r\n"+revisionCode);
//                    line++;
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (bfReader != null) {
//                try {
//                    bfReader.close();
//                }catch (IOException e){
//                    e.printStackTrace();
//                }
//            }
//        }
//        System.out.println(stringBuffer.toString());
//        return stringBuffer.toString();

    }

    public String getLineCode(String fileName, int readLine) {
        String line;
        try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {
            int i = 0;
            while ((line = br.readLine()) != null) {
                i++;
                if (i == readLine) {
                    return line;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void backupFile(File sourceFile) throws IOException {
        String fileName = sourceFile.getName();
        if (fileName.endsWith(".java")) {
            fileName = fileName.replace(".java", ".b");
        }
        File backFile = new File(sourceFile.getParent(), fileName);

        FileUtil.copyFileStream(sourceFile, backFile);
    }

    public void recoverFile(String sourceFile) {
        File backupFile = new File(sourceFile);
        String fileName = backupFile.getName();
        fileName = fileName.replace(".b", ".java");
        File destFile = new File(backupFile.getParent(), fileName);
        backupFile.renameTo(destFile);
    }

}
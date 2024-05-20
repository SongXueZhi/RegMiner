package org.regminer.commons.utils;

import org.apache.commons.io.FileUtils;
import org.regminer.commons.constant.Configurations;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileUtilx {


    // /home/sxz/document/mmmmm-ddahkdak989/123.java
    public static String getDirectoryFromPath(String path) {
        return path.contains("/") ? path.substring(0, path.lastIndexOf("/")) : "";
    }

    public static File getDirFromBfcAndBic(String bfc, String bic) {
        String shortBfc = bfc.length() > 7 ? bfc.substring(0, 7) : bfc;
        String shortBic = bic.length() > 7 ? bic.substring(0, 7) : bic;

        // 创建一个唯一的目录名
        String dirName =
                Configurations.cachePath + File.separator + Configurations.projectName + File.separator + shortBfc + File.separator + shortBic;

        // 创建一个File对象
        File directory = new File(dirName);

        // 如果目录不存在，则创建它
        if (directory.exists()) {
            try {
                FileUtils.forceDelete(directory);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        directory.mkdirs();
        return directory;
    }

    public static String readContentFromFile(File file) {
        String result = null;
        try {
            InputStream is = new FileInputStream(file);
            if (file.exists() && file.isFile()) {
                BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                StringBuffer sb2 = new StringBuffer();
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb2.append(line + "\n");
                }
                br.close();
                result = sb2.toString();
            }
        } catch (Exception e) {

        }
        return result;
    }

    public static Set<String> readSetFromFile(String path) {
        Set<String> result = new HashSet<>();
        File file = new File(path);
        try {
            InputStream is = new FileInputStream(file);
            if (file.exists() && file.isFile()) {
                BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                String line = null;
                while ((line = br.readLine()) != null) {
                    result.add(line);
                }
                br.close();
            }
        } catch (Exception e) {

        }
        return result;
    }

}

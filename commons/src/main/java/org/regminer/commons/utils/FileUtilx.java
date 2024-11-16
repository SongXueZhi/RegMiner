package org.regminer.commons.utils;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.regminer.commons.constant.Configurations;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class FileUtilx {

    private static final Logger LOGGER = LogManager.getLogger(FileUtilx.class);
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

        Path path = Path.of(dirName);

        try {
            if (Files.exists(path)){
                FileUtils.forceDelete(path.toFile());
            }
            Files.createDirectories(path);
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
        return path.toFile();
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

package org.regminer.common.utils;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileUtil {

    public static String getDirectoryFromPath(String path) {
        return path.contains("/") ? path.substring(0, path.lastIndexOf("/")) : "";
    }

    public static synchronized void apendResultToFile(String line, File file) {
        try {
            FileUtils.writeStringToFile(file, line + "\n", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getUUID() {
        return UUID.randomUUID().toString();

    }

    public static String readContentFromFile(String path) {
        File file = new File(path);
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

    public static List<String> readListFromFile(String path) {
        List<String> result = new ArrayList<>();
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

    public static void addImportStatement(String filePath, String packageName) throws IOException {
        Path path = Paths.get(filePath);
        List<String> lines = Files.readAllLines(path);

        String importStatement = "import " + packageName + ";";

        if (lines.contains(importStatement)) {
            return;
        }

        int packagePosition = -1;
        int importInsertPosition;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).startsWith("package ")) {
                packagePosition = i;
                break;
            }
        }

        if (packagePosition != -1) {
            importInsertPosition = packagePosition + 1;
        } else {
            importInsertPosition = 0;
        }
        lines.add(importInsertPosition, importStatement);

        Files.write(path, lines);
    }

    public void createdNewDirectory(String path) {

    }


}

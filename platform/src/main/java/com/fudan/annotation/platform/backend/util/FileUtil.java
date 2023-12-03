package com.fudan.annotation.platform.backend.util;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * description:
 *
 * @author Richy
 * create: 2022-06-30 20:19
 **/
public class FileUtil {
    public static boolean DeleteFileByPath(String filePath) {
        File file = new File(filePath);

        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                return file.delete();
            } else {
                return false;
            }

        }

    }

    public static void copyFileStream(File source, File dest) throws IOException {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(source);
            output = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
        } finally {
            input.close();
            output.close();

        }
    }

    public static void writeInFile(String filePath, String content) {
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(filePath))) {
            bufferedOutputStream.write(content.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void copyDirToTarget(String fileFullNameCurrent, String fileFullNameTarget) {
        try {
            File current = new File(fileFullNameCurrent);
            if (!current.exists() || !current.isDirectory()) {
                return;
            }

            File target = new File(fileFullNameTarget);
            if (target.exists()) {
                FileUtils.forceDelete(target);
            }
            FileUtils.forceMkdirParent(target);
            FileUtils.copyDirectory(current, target);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            e.printStackTrace();
        }
        return result;
    }

    public static void writeListToFile(String path, List<String> line) {
        File file = new File(path);
        try {
            FileOutputStream fos = new FileOutputStream(path, false);
            if (file.exists() && file.isFile()) {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
                for (String s : line) {
                    bw.write(s);
                    bw.newLine();
                    bw.flush();
                }
                bw.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
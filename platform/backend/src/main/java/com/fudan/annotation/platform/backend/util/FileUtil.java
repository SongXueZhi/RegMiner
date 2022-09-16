package com.fudan.annotation.platform.backend.util;

import java.io.*;

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

    public static void writeInFile(String filePath,String content) {
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(filePath))){
            bufferedOutputStream.write(content.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
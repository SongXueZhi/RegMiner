package org.regminer.miner.utils;

import org.apache.tools.ant.DirectoryScanner;

import java.io.File;

public class CodeUtil {


    public static String[] getJavaFiles(File meta) {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(meta);
        scanner.setIncludes(new String[]{"**/*.java"});
        scanner.setCaseSensitive(true);
        scanner.scan();
        String[] files = scanner.getIncludedFiles();
        return files;
    }


    public static int lengthOfLongestCommonSubstring(String s1, String s2) {
        if (s1 == null || s2 == null || s1.length() == 0 || s2.length() == 0) {
            return 0;
        }
        int start = 0;
        int maxLen = 0;
        int[][] table = new int[s1.length()][s2.length()];
        for (int i = 0; i < s1.length(); i++) {
            for (int j = 0; j < s2.length(); j++) {
                if (i == 0 || j == 0) {
                    if (s1.charAt(i) == s2.charAt(j)) {
                        table[i][j] = 1;
                    }
                    if (table[i][j] > maxLen) {
                        maxLen = table[i][j];
                        start = i;
                    }
                } else {
                    if (s1.charAt(i) == s2.charAt(j)) {
                        table[i][j] = table[i - 1][j - 1] + 1;
                    }
                    if (table[i][j] > maxLen) {
                        maxLen = table[i][j];
                        start = i + 1 - maxLen;
                    }
                }
            }
        }
        return s1.substring(start, start + maxLen).length();
    }
}

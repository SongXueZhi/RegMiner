package regminer.utils;

import org.apache.tools.ant.DirectoryScanner;

import java.io.File;

public class CodeUtil {


    public static String[] getJavaAndClassFiles(File meta) {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(meta);
        scanner.setIncludes(new String[]{"**/*.java", "**/*.class"});
        scanner.setCaseSensitive(true);
        scanner.scan();
        String[] files = scanner.getIncludedFiles();
        return files;
    }

    public static String[] getJavaFiles(File meta) {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(meta);
        scanner.setIncludes(new String[]{"**/*.java"});
        scanner.setCaseSensitive(true);
        scanner.scan();
        String[] files = scanner.getIncludedFiles();
        return files;
    }


}

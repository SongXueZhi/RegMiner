package regminer.utils;

import org.apache.tools.ant.DirectoryScanner;
import regminer.coverage.CodeCoverage;
import regminer.coverage.model.CoverMethod;
import regminer.coverage.model.CoverNode;
import regminer.exec.TestExecutor;
import regminer.model.Methodx;

import java.io.File;
import java.util.List;
import java.util.Set;

public class Stastic {

    static String projectPath = "/home/sxz/miner_space/fastjson";
    static TestExecutor executor = new TestExecutor();

    public static void main(String[] args) throws Exception {

        File projectDir = new File(projectPath);

        // get all test file
        File rootDir = new File(projectDir, "src/test/java");
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(rootDir);
        scanner.setIncludes(new String[]{"**/*.java"});
        scanner.setCaseSensitive(true);
        scanner.scan();
        String[] files = scanner.getIncludedFiles();


        int sum = 0;
        //TEST
        for (String filePath : files) {
            File testFile = new File(rootDir, filePath);
            String codeContent = FileUtilx.readContentFromFile(testFile);
            //filter non-test file
            if (!(codeContent.contains("@Test") || codeContent.contains("junit") || codeContent.contains("TestCase"))) {
                continue;
            }
            List<Methodx> methodxList = CompilationUtil.getAllMethod(codeContent);
            for (Methodx methodx : methodxList) {
                //filter test method
                if (methodx.getMethodDeclaration().toString().contains("@Test") || methodx.getMethodDeclaration().getName().toString().startsWith("test")) {
                            sum++;
                }
            }
        }
        System.out.println(sum);
    }
}

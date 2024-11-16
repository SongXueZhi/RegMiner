package org.regminer.ct;

import junit.framework.TestCase;
import org.apache.maven.model.Dependency;
import org.regminer.commons.tool.MavenDependencyProvider;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Jerry Zhang <zhangjian16@fudan.edu.cn>
 * @desc
 * @date 2023/12/13 14:16
 */

public class CtRefereesTest extends TestCase {

    public void testDetectProblematicDependencies() {
    }

    public void testDetectClassNameConflicts() {
        File file = new File( System.getProperty("user.dir") + "/src/test/resources/testFile/conflictPackage.log");
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
            byte[] content = new byte[(int) file.length()];
            inputStream.read(content);
            String msg = new String(content, StandardCharsets.UTF_8);

            Map<String, List<String>> result = CtReferees.detectClassNameConflicts(msg);

            String filename = "/Users/doughit/fdse/miner_space/cache/jackson-databind/12f82c6/12f82c6/src/test/java/com/fasterxml/jackson/failing/NodeContext2049Test.java";
            assertTrue("Expected file name not found in result", result.containsKey(filename));
            assertTrue("Expected package not found in result", result.get(filename).contains("com.fasterxml.jackson.databind.Module"));
            assertTrue("Expected package not found in result", result.get(filename).contains("java.lang.Module"));

        } catch (Exception e) {
            fail("Exception during test: " + e.getMessage());
        }
    }

    public void testFindMissingDependencies() {
        File file = new File( System.getProperty("user.dir") + "/src/test/resources/testFile/missing.log");
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
            byte[] content = new byte[(int) file.length()];
            inputStream.read(content);
            String msg = new String(content, StandardCharsets.UTF_8);

            Set<String> missingDependencies = CtReferees.findMissingDependencies(msg);

            for (String missingDependency : missingDependencies) {
                List<Dependency> dependencies = MavenDependencyProvider.getAllMavenDependencies(missingDependency);
                System.out.println(dependencies);
            }
        } catch (Exception e) {
            fail("Exception during test: " + e.getMessage());
        }
    }
}
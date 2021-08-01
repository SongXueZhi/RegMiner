package regminer.maven;


import org.junit.Test;

import java.io.File;

public class JacocoMavenManagerTest {

    @Test
    public void testAddJacocoFeatureToMaven() throws Exception {
        File file =new File("C:\\Users\\sxzdh\\Desktop\\bfc\\pom.xml");
        JacocoMavenManager jacocoMaven =new JacocoMavenManager();
        jacocoMaven.addJacocoFeatureToMaven(file);
    }
}
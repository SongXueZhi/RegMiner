package regminer.maven;


import org.junit.Test;

import java.io.File;

public class JacocoMavenTest {

    @Test
    public void testAddJacocoFeatureToMaven() throws Exception {
        File file =new File("C:\\Users\\sxzdh\\Desktop\\bfc\\pom.xml");
        JacocoMaven jacocoMaven =new JacocoMaven();
        jacocoMaven.addJacocoFeatureToMaven(file);
    }
}
package regminer.coverage;

import regminer.constant.Conf;
import regminer.maven.JacocoMaven;

import java.io.File;

public class CodeCoverage {
    final static String MAVEN_POM="pom.xml";

    public void addJacocoFeatureToProject() throws Exception {
        File pomFile = new File( Conf.META_PATH+ File.separator+MAVEN_POM);
        JacocoMaven jacocoMaven = new JacocoMaven();
        jacocoMaven.addJacocoFeatureToMaven(pomFile);
    }

}

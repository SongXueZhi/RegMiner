package com.fudan.annotation.platform.backend.core;

/**
 * @Author: sxz
 * @Date: 2023/08/01/16:23
 * @Description:
 */

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CodeCoverage {
    final static String MAVEN_POM = "pom.xml";
    final static String JACOCO_PATH = "site" + File.separator + "jacoco" + File.separator + "jacoco.xml";

    public void addJacocoFeatureToProject(File codePath) throws Exception {
        File pomFile = new File(codePath, MAVEN_POM);
        JacocoMavenManager jacocoMaven = new JacocoMavenManager();
        jacocoMaven.addJacocoFeatureToMaven(pomFile);
    }

    public Map<String, List<Integer>> parseJaCoCoReport(File codePath) {
        Map<String, List<Integer>> coveredLinesMap = new HashMap<>();

        try {
            String target = new MavenManager().getTargetDir(codePath);
            File inputFile = new File(codePath + File.separator + target + File.separator + JACOCO_PATH);
            // if not, return, there may jacoco plugin not work
            if (!inputFile.exists()) {
                return null;
            }
            SAXReader reader = new SAXReader();
            Document document = reader.read(inputFile);

            List<Node> packageNodes = document.selectNodes("//package");
            for (Node packageNode : packageNodes) {
                String packageName = ((Element) packageNode).attributeValue("name").replace('/', '.');
                List<Node> sourceFileNodes = ((Element) packageNode).selectNodes("sourcefile");
                for (Node sourceFileNode : sourceFileNodes) {
                    String fileName = ((Element) sourceFileNode).attributeValue("name");
                    String fullClassName = packageName + '.' + fileName.substring(0, fileName.lastIndexOf('.'));

                    List<Integer> coveredLines = new ArrayList<>();
                    List<Node> lineNodes = ((Element) sourceFileNode).selectNodes("line");
                    for (Node lineNode : lineNodes) {
                        int lineNumber = Integer.parseInt(((Element) lineNode).attributeValue("nr"));
                        int hits = Integer.parseInt(((Element) lineNode).attributeValue("ci"));
                        if (hits > 0) {
                            coveredLines.add(lineNumber);
                        }
                    }

                    if (!coveredLines.isEmpty()) {
                        coveredLinesMap.put(fullClassName, coveredLines);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return coveredLinesMap;
    }


//        public List<String> readJacocoReports(File codePath) {
//            try {
//                // Whether Jacoco report is created?
//                String target = new MavenManager().getTargetDir(codePath);
//                File jacocoXmlFile = new File(codePath + File.separator + target + File.separator + JACOCO_PATH);
//                // if not, return, there may jacoco plugin not work
//                if (!jacocoXmlFile.exists()) {
//                    return null;
//                }
//                // Get Package tag ,then Class tag ,then Method tag
//                SAXReader xmlReader = new SAXReader();
//                Document doc = xmlReader.read(jacocoXmlFile);
//                Element rootNode = doc.getRootElement();
//            } catch (Exception e) {
//                e.printStackTrace();
//                return null;
//            }
//            return  null;
//        }

}

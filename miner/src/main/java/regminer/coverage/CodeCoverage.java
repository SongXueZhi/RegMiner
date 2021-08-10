/*
 *
 *  * Copyright 2021 SongXueZhi
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package regminer.coverage;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import regminer.coverage.model.CoverClass;
import regminer.coverage.model.CoverMethod;
import regminer.coverage.model.CoverNode;
import regminer.coverage.model.CoverPackage;
import regminer.maven.JacocoMavenManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CodeCoverage {
    final static String MAVEN_POM = "pom.xml";
    final static String JACOCO_PATH = "target" + File.separator + "site" + File.separator + "jacoco" + File.separator + "jacoco.xml";

    public void addJacocoFeatureToProject(File codePath) throws Exception {
        File pomFile = new File(codePath, MAVEN_POM);
        JacocoMavenManager jacocoMaven = new JacocoMavenManager();
        jacocoMaven.addJacocoFeatureToMaven(pomFile);
    }


    public List<CoverNode> readJacocoReports(File codePath) {
        try {
            // Whether Jacoco report is created?
            File jacocoXmlFile = new File(codePath + File.separator + JACOCO_PATH);
            // if not, return, there may jacoco plugin not work
            if (!jacocoXmlFile.exists()) {
                return null;
            }
            // Get Package tag ,then Class tag ,then Method tag
            SAXReader xmlReader = new SAXReader();
            Document doc = xmlReader.read(jacocoXmlFile);
            Element rootNode = doc.getRootElement();
            List<Element> packageList = rootNode.elements("package");
            List<Element> classNodeList = getAllClassNode(packageList);
            List<Element> methodNodeList = getAllMethodNode(classNodeList);
            return getCoverNodeList(methodNodeList);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private List<Element> getAllClassNode(List<Element> packageList) {
        List<Element> allClassNode = new ArrayList<>();
        for (Element packageNode : packageList) {
            List<Element> classInPackage = packageNode.elements("class");
            allClassNode.addAll(classInPackage);
        }
        return allClassNode;
    }

    private List<Element> getAllMethodNode(List<Element> classNodeList) {
        List<Element> allMethodList = new ArrayList<>();
        for (Element classNode : classNodeList) {
            List<Element> methodNodeList = classNode.elements("method");
            for (Element methodNode : methodNodeList) {
                List<Element> counterList = methodNode.elements("counter");
                for (Element counterNode : counterList) {
                    if (counterNode.attributeValue("type").equals("METHOD")) {
                        String coverNumString = counterNode.attributeValue("covered");
                        int coverNum = Integer.parseInt(coverNumString);
                        if (coverNum > 0) {
                            allMethodList.add(methodNode);
                        }
                    }
                }
            }
        }
        return allMethodList;
    }

    private List<CoverNode> getCoverNodeList(List<Element> coverMethodList) {
        List<CoverNode> coverNodeList = new ArrayList<>();
        for (Element coverMethodNode : coverMethodList) {
            CoverMethod coverMethod = new CoverMethod();
            coverMethod.setName(coverMethodNode.attributeValue("name"));
            coverMethod.setLine(Integer.parseInt(coverMethodNode.attributeValue("line")));
            coverMethod.setSignature(coverMethodNode.attributeValue("desc"));
            if (coverMethod.getName().equals("<init>")) {
                continue;
            }
            if (coverMethod.getName().equals("<clinit>")) {
                continue;
            }
            Element classNode = coverMethodNode.getParent();
            CoverClass coverClass = new CoverClass();
            coverClass.setName(classNode.attributeValue("name"));
            coverClass.setFileName(classNode.attributeValue("sourcefilename"));

            Element packageNode = classNode.getParent();
            CoverPackage coverPackage = new CoverPackage();
            coverPackage.setName(packageNode.attributeValue("name"));

            coverNodeList.add(new CoverNode(coverPackage, coverClass, coverMethod));
        }
        return coverNodeList;
    }
}

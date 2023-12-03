package com.fudan.annotation.platform.backend.core;

/*
 * Copyright 2021 SongXueZhi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MavenManager {
    public final static String M2AFFIX = ".m2" + File.separator + "repository";
    private MavenXpp3Reader mavenReader = new MavenXpp3Reader();
    private MavenXpp3Writer mavenXpp3Writer = new MavenXpp3Writer();

    /**
     * Default find jar from "~/.m2/repository/"
     *
     * @param pom pom.xml absolutely path
     * @return
     * @throws Exception
     */
    public List<String> readAllDependency(File pom) throws Exception {
        Model pomModel = getPomModel(pom);
        List<Dependency> dependencies = pomModel.getDependencies();
        List<String> result = new ArrayList<>();
        String usrHomePath = getUserHomePath();
        for (Dependency dependency :
                dependencies) {
            StringBuilder sb = new StringBuilder(usrHomePath);
            sb.append(File.separator)
                    .append(M2AFFIX).append(File.separator)
                    .append(dependency.getGroupId().replace(".", File.separator)).append(File.separator)
                    .append(dependency.getArtifactId());
            String version = dependency.getVersion();
            if (!version.contains(".") || version.contains("$")) {
                File[] childList = new File(sb.toString()).listFiles();
                for (int i = 0; i < childList.length; i++) {
                    if (childList[i].isDirectory()) {
                        version = childList[i].getName();
                    }
                }
            }
            sb.append(File.separator).append(version).append(File.separator)
                    .append(dependency.getArtifactId()).append("-")
                    .append(version).append(".").append(dependency.getType());
            String tt = sb.toString();
            File file = new File(tt);
            try {
                if (!file.exists()) {
                    File[] cc = file.getParentFile().listFiles();
                    for (int j = 0; j < cc.length; j++) {
                        if (cc[j].getName().endsWith(".jar")) {
                            tt = cc[j].getAbsolutePath();
                            break;
                        }
                    }
                }
            } catch (Exception e) {

            }

            result.add(tt);
        }
        return result;
    }

    protected Model getPomModel(File pomFile) throws Exception {
        Model pomModel = mavenReader.read(new FileReader(pomFile));
        return pomModel;
    }

    protected void saveModel(File pomFile, Model pomModel) throws Exception {
        mavenXpp3Writer.write(new FileWriter(pomFile), pomModel);
    }

    protected String getUserHomePath() {
        return SystemUtils.getUserHome().toString();
    }

    public String getSrcDir(File codePath) throws Exception {
        Model pomModel = getPomModel(new File(codePath, "pom.xml"));
        List<String> modules = pomModel.getModules();
        String module = "";
        if (modules.size() == 1) { //handle single module
            module = modules.get(0) + File.separator;
            pomModel = getPomModel(new File(codePath, module + "pom.xml"));
        }
        String srcDir = pomModel.getBuild().getSourceDirectory();
        return module + (srcDir == null ? String.format("src%cmain%cjava", File.separatorChar, File.separatorChar) :
                replaceProperties(srcDir, pomModel));
    }

    private String replaceProperties(String s, Model pomModel) {
        Properties props = pomModel.getProperties();
        Pattern p = Pattern.compile("\\$\\{(.+)\\}");
        Matcher m = p.matcher(s);
        StringBuilder sb = new StringBuilder();
        int start = 0;
        while (m.find()) {
            sb.append(s.substring(start, m.start()));
            String prop = props.getProperty(m.group(1));
            if (prop != null) {
                sb.append(prop);
            }
            start = m.end();
        }
        sb.append(s.substring(start));
        return sb.toString();
    }

    public String getTargetDir(File codePath) throws Exception {
        Model pomModel = getPomModel(new File(codePath, "pom.xml"));
        List<String> modules = pomModel.getModules();
        String module = "";
        if (modules.size() == 1) { //handle single module
            module = modules.get(0) + File.separator;
            pomModel = getPomModel(new File(codePath, module + "pom.xml"));
        }
        String target = pomModel.getBuild().getDirectory();
        return module + (target == null ? "target" : replaceProperties(target, pomModel));
    }
}
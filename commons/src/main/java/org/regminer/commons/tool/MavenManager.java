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

package org.regminer.commons.tool;

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

public class MavenManager {
    public static final String M2AFFIX = ".m2" + File.separator + "repository";
    private final MavenXpp3Reader mavenReader = new MavenXpp3Reader();
    private final MavenXpp3Writer mavenXpp3Writer = new MavenXpp3Writer();

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
            String sb = usrHomePath + File.separator +
                    M2AFFIX + File.separator +
                    dependency.getGroupId().replace(".", File.separator) + File.separator +
                    dependency.getArtifactId() + File.separator +
                    dependency.getVersion() + File.separator +
                    dependency.getArtifactId() + "-" +
                    dependency.getVersion() + "." + dependency.getType();
            result.add(sb);
        }
        return result;
    }

    public Model getPomModel(File pomFile) throws Exception {
        Model pomModel = mavenReader.read(new FileReader(pomFile));
        return pomModel;
    }

    public void saveModel(File pomFile, Model pomModel) throws Exception {
        mavenXpp3Writer.write(new FileWriter(pomFile), pomModel);
    }

    protected String getUserHomePath() {
        return SystemUtils.getUserHome().toString();
    }
}

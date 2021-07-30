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

package regminer.maven;

import org.apache.maven.model.*;

import java.io.File;

public class JacocoMaven extends MavenManager {

    public void addJacocoFeatureToMaven(File pomFile) throws Exception {
     Model pomModel =getPomModel(pomFile);
     addJacocoDependency(pomModel);
     addJacocoPlugin(pomModel);
     saveModel(pomFile,pomModel);
    }

    private void addJacocoPlugin(Model pomModel) {

        Plugin plugin = new Plugin();
        plugin.setGroupId("org.jacoco");
        plugin.setArtifactId("jacoco-maven-plugin");
        plugin.setVersion("0.8.5");

        PluginExecution pluginExecution1 = new PluginExecution();
        pluginExecution1.addGoal("prepare-agent");

        PluginExecution pluginExecution2 = new PluginExecution();
        pluginExecution2.setId("report");
        pluginExecution2.setPhase("test");
        pluginExecution2.addGoal("report");

        plugin.addExecution(pluginExecution1);
        plugin.addExecution(pluginExecution2);

        Build build = pomModel.getBuild();
        if (build == null) {
            pomModel.setBuild(new Build());
            build = pomModel.getBuild();
        }
        build.addPlugin(plugin);
    }

    private void addJacocoDependency(Model pomModel) {
        Dependency dependency = new Dependency();
        dependency.setGroupId("org.jacoco");
        dependency.setArtifactId("jacoco-maven-plugin");
        dependency.setVersion("0.8.5");
        dependency.setType("maven-plugin");
        pomModel.getDependencies().add(dependency);
    }

}

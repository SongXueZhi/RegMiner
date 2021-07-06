///*
// * Copyright 2021 SongXueZhi
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package regminer.maven;
//
//import org.apache.maven.model.Dependency;
//import org.apache.maven.model.Model;
//import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
//import org.evosuite.shaded.org.apache.commons.lang3.SystemUtils;
//
//import java.io.File;
//import java.io.FileReader;
//import java.util.ArrayList;
//import java.util.List;
//
//public class MavenManager {
//    public final static String M2AFFIX = ".m2"+File.separator+"repository";
//
//    /**
//     * Default find jar from "~/.m2/repository/"
//     * @param pom pom.xml absolutely path
//     * @return
//     * @throws Exception
//     */
//    public  List<String> readAllDependency(File pom) throws Exception {
//        MavenXpp3Reader mavenReader = new MavenXpp3Reader();
//        Model pomModel = mavenReader.read(new FileReader(pom));
//        List<Dependency> dependencies = pomModel.getDependencies();
//        List<String> result = new ArrayList<>();
//        String usrHomePath = getUserHomePath();
//        for (Dependency dependency :
//                dependencies) {
//            StringBuilder sb = new StringBuilder(usrHomePath);
//            sb.append(File.separator)
//                    .append(M2AFFIX).append(File.separator)
//                    .append(dependency.getGroupId().replace(".", File.separator)).append(File.separator)
//                    .append(dependency.getArtifactId()).append(File.separator)
//                    .append(dependency.getVersion()).append(File.separator)
//                    .append(dependency.getArtifactId()).append("-")
//                    .append(dependency.getVersion()).append(".").append(dependency.getType());
//            result.add(sb.toString());
//        }
//        return  result;
//    }
//
//    private String getUserHomePath() {
//        return SystemUtils.getUserHome().toString();
//    }
//}

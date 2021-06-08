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

package regminer.maven;

import org.junit.Test;
import regminer.constant.Conf;
import regminer.testsuite.RegMinerTest;

import java.io.File;

public class MavenManagerTest extends RegMinerTest {
    MavenManager mvnManger = new MavenManager();

    @Test
    public void testReadAllDependency() throws Exception {
        mvnManger.readAllDependency(new File(Conf.META_PATH, "pom.xml"));

    }
}
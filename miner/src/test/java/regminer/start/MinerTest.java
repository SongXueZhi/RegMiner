/*
 * Copyright 2021  All contributor
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

package regminer.start;

import org.eclipse.jgit.api.Git;
import org.junit.Test;
import regminer.constant.Conf;
import regminer.git.provider.Provider;
import regminer.miner.PotentialBFCDetector;
import regminer.model.PotentialRFC;
import regminer.testsuite.RegMinerTest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MinerTest extends RegMinerTest {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        Miner.repo = new Provider().create(Provider.EXISITING).get(Conf.LOCAL_PROJECT_GIT);
        Miner.git = new Git(Miner.repo);

    }

    @Test
    public void testSearchSuccess() throws Exception {
        List<String> filter = new ArrayList<>();
        filter.add("70b9da621e77fec2b0f80c6230ff5bb4dfec33ef");
        //filter.add("11ea8be0626d0d8de285ca73b779b074437194e2");
        PotentialBFCDetector pBFCDetector = new PotentialBFCDetector(Miner.repo, Miner.git);
        Miner.pRFCs = null;
        Miner.pRFCs = (LinkedList<PotentialRFC>) pBFCDetector.detectPotentialBFC(filter);
        Miner.singleThreadHandle();
    }

    @Test
    public void testSearchFailed() throws Exception {
        List<String> filter = new ArrayList<>();
        filter.add("41b1477c7ca40914793b0a8dc86fa0e9a2089972");
        PotentialBFCDetector pBFCDetector = new PotentialBFCDetector(Miner.repo, Miner.git);
        Miner.pRFCs = null;
        Miner.pRFCs = (LinkedList<PotentialRFC>) pBFCDetector.detectPotentialBFC(filter);
        Miner.singleThreadHandle();
    }
}
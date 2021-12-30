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
import org.junit.Ignore;
import org.junit.Test;
import regminer.constant.Conf;
import regminer.git.provider.Provider;
import regminer.miner.PotentialBFCDetector;
import regminer.model.PotentialRFC;
import regminer.testsuite.RegMinerTest;
import regminer.utils.FileUtilx;

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
    @Ignore
    @Test
    public void regressionTest() throws Exception {
        List<String> filter = new ArrayList<>(FileUtilx.readListFromFile("resources/regression_for_test.csv"));
        PotentialBFCDetector pBFCDetector = new PotentialBFCDetector(Miner.repo, Miner.git);
        Miner.pRFCs = null;
        Miner.pRFCs = (LinkedList<PotentialRFC>) pBFCDetector.detectPotentialBFC(filter);
        Miner.singleThreadHandle();
    }
    @Ignore
    @Test
    public void testSearchSuccess() throws Exception {
        List<String> filter = new ArrayList<>();
        filter.add("69e1509843f5dd5ea3f91cb3dcd9fdb0fb100147");
        filter.add("296fbdb5c555c10321e56eebdb62b146b0606d66");
        PotentialBFCDetector pBFCDetector = new PotentialBFCDetector(Miner.repo, Miner.git);
        Miner.pRFCs = null;
        Miner.pRFCs = (LinkedList<PotentialRFC>) pBFCDetector.detectPotentialBFC(filter);
        Miner.singleThreadHandle();
    }

    //fastjson_fcp.csv
    @Ignore
    @Test
    public void testRegressionInFAlCePass() throws Exception {
        List<String> filter = new ArrayList<>(FileUtilx.readSetFromFile("resources/fastjson_fcp.csv"));
        PotentialBFCDetector pBFCDetector = new PotentialBFCDetector(Miner.repo, Miner.git);
        Miner.pRFCs = null;
        Miner.pRFCs = (LinkedList<PotentialRFC>) pBFCDetector.detectPotentialBFC(filter);
        Miner.singleThreadHandle();
    }

    @Ignore
    @Test
    public void testSearchFailed() throws Exception {
        List<String> filter = new ArrayList<>();
        filter.add("41b1477c7ca40914793b0a8dc86fa0e9a2089972");
        PotentialBFCDetector pBFCDetector = new PotentialBFCDetector(Miner.repo, Miner.git);
        Miner.pRFCs = null;
        Miner.pRFCs = (LinkedList<PotentialRFC>) pBFCDetector.detectPotentialBFC(filter);
        Miner.singleThreadHandle();
    }
/**
 *  success case
 * pRFC in total :1
 * 1.0%
 * 5ca2b3bd42add17fe2c4ff56d88ea5cfd457ae30开始执行测试约减
 * 迁移成功[com.alibaba.fastjson.serializer.issue3473.SerializeWriterJavaSqlDateTest#yyyy_MM_dd_HH_mm_ss_test:NONE;com.alibaba.fastjson.serializer.issue3473.SerializeWriterJavaSqlDateTest#yyyy_MM_dd_test:TESTSUCCESS]
 * bic:d9e496d21367d12353bd02d872de1eecc0419fe7
 * 迁移后编译失败
 * bic:7c1bed08103d3cc5404fb64250a09df577424edb
 * 迁移后编译失败
 * bic:b0765ed2719667aaede730ecc651e60e8f26aa20
 * 迁移后编译失败
 * bic:e18c40c0d613d2b72af1856dba92bfb2aaa37679
 * 迁移后编译失败
 * bic:199b68352bd358cabb388a7a2d883ca233a30899
 * 迁移后编译失败
 * bic:91e9164ce5217f3f1710882898cac511e1aae458
 * 迁移后编译失败
 * bic:c171c78738a58997fd9a6286faff5386ed8cc633
 * 迁移后编译失败
 * bic:7d441d0124e17cf3020925d6df3098cd06994a83
 * 迁移后编译失败
 * bic:49414fef58e815427cb6b01c85b9eacb111f03ea
 * 迁移后编译失败
 * bic:1d5b1d8d7284d9268adec85c5a79bc84177f401a
 * 迁移后编译失败
 * bic:6fa45d63da0bfe52a35ea84f6dc7b62dd508f195
 * 迁移后编译失败
 * bic:fc0860a5d0813231571f501d1021b4658bca6e3e
 * 迁移后编译失败
 * bic:76444e71a0b80966f5366c8080f7c9ea02389d87
 * 迁移后编译失败
 * bic:7a8f77ece75a7310f5f3d66642c7ba85e2765314
 * 迁移后编译失败
 * bic:0ffcd17389e26eba0873fcb34e5ae03c8bf3e230
 * 迁移后编译失败
 * bic:f1cafd432e540082dc4fdca0b090e1c2ea59edc5
 * 迁移后编译失败
 * bic:9b86a4684080319a63970fbc4d8b49c4e8113df5
 * 迁移后编译失败
 * bic:61446e6e40862b5b3c377a6470c55ff4578433ce
 * 迁移后编译失败
 * bic:071f1bbd5604909100a8e2cfdaf0fe8793d9f55c
 * 迁移后编译失败
 * bic:89b9f26cff335c2f5ebabdf9bf41ad49d572f050
 * 测试bic [com.alibaba.fastjson.serializer.issue3473.SerializeWriterJavaSqlDateTest#yyyy_MM_dd_HH_mm_ss_test:TESTSUCCESS]
 * PASS
 * bic:64810a8be892c4f67261b67f76f11309123f2cc4
 * 测试bic [com.alibaba.fastjson.serializer.issue3473.SerializeWriterJavaSqlDateTest#yyyy_MM_dd_HH_mm_ss_test:TESTSUCCESS]
 * PASS
 * bic:cd6441ae3ef465780e4e629b7a806bafbd112695
 * 迁移后编译失败
 * bic:acae9b2db44eba297758b683deef668c16ad32c7
 * 测试bic [com.alibaba.fastjson.serializer.issue3473.SerializeWriterJavaSqlDateTest#yyyy_MM_dd_HH_mm_ss_test:TESTSUCCESS]
 * PASS
 * bic:b89fa663074d5fdb0d41d229804cc293f9ce9e42
 * 测试bic [com.alibaba.fastjson.serializer.issue3473.SerializeWriterJavaSqlDateTest#yyyy_MM_dd_HH_mm_ss_test:TESTSUCCESS]
 * PASS
 * bic:33e894b450714e21cd24821bfbb720ccf4975d5a
 * 测试bic [com.alibaba.fastjson.serializer.issue3473.SerializeWriterJavaSqlDateTest#yyyy_MM_dd_HH_mm_ss_test:NONE]
 * FAL
 * bic:6e7b699df619fb41400115c362060b213c281233
 * 测试bic [com.alibaba.fastjson.serializer.issue3473.SerializeWriterJavaSqlDateTest#yyyy_MM_dd_HH_mm_ss_test:NONE]
 * FAL
 * bic:f0ecb025734453acd6dd5398ed87c54245705d9b
 * 测试bic [com.alibaba.fastjson.serializer.issue3473.SerializeWriterJavaSqlDateTest#yyyy_MM_dd_HH_mm_ss_test:NONE]
 * FAL
 * bic:7c05c6ffcb29213b6cb1d02f4907684726cc501b
 * 测试bic [com.alibaba.fastjson.serializer.issue3473.SerializeWriterJavaSqlDateTest#yyyy_MM_dd_HH_mm_ss_test:NONE]
 * FAL
 * bic:125016e45bdbcb363d74cf5c42b0600a085a3378
 * 测试bic [com.alibaba.fastjson.serializer.issue3473.SerializeWriterJavaSqlDateTest#yyyy_MM_dd_HH_mm_ss_test:NONE]
 * FAL
 * bic:69fb121cc09b8fdf6c4aa63fbb634c1e322641db
 * 测试bic [com.alibaba.fastjson.serializer.issue3473.SerializeWriterJavaSqlDateTest#yyyy_MM_dd_HH_mm_ss_test:NONE]
 * FAL
 * bic:5138af5dadea546870562803c483526f6303c792
 * 测试bic [com.alibaba.fastjson.serializer.issue3473.SerializeWriterJavaSqlDateTest#yyyy_MM_dd_HH_mm_ss_test:NONE]
 * FAL
 * bic:4bdfe34a0ec06446bc5963f92a7561a4263fdb0e
 * 测试bic [com.alibaba.fastjson.serializer.issue3473.SerializeWriterJavaSqlDateTest#yyyy_MM_dd_HH_mm_ss_test:NONE]
 * FAL
 * bic:e7caf58980389bc405eb5dce3b7003c11c62494e
 * 测试bic [com.alibaba.fastjson.serializer.issue3473.SerializeWriterJavaSqlDateTest#yyyy_MM_dd_HH_mm_ss_test:TESTSUCCESS]
 * PASS
 * bic:b06f7177665c8d62f1a4d12bd59900a542effea0
 * 测试bic [com.alibaba.fastjson.serializer.issue3473.SerializeWriterJavaSqlDateTest#yyyy_MM_dd_HH_mm_ss_test:TESTSUCCESS]
 * PASS
 * bic:f6ad27234dccea952372bd7caf83eeb79b91ccbc
 * 测试bic [com.alibaba.fastjson.serializer.issue3473.SerializeWriterJavaSqlDateTest#yyyy_MM_dd_HH_mm_ss_test:NONE]
 * FAL
 * bic:9a037716f33388a0580f2164cda4b7a56ad40d19
 * 测试bic [com.alibaba.fastjson.serializer.issue3473.SerializeWriterJavaSqlDateTest#yyyy_MM_dd_HH_mm_ss_test:NONE]
 * FAL
 * bic:d1a9afe9d0363e7697dd049e98413fd0caeec983
 * 测试bic [com.alibaba.fastjson.serializer.issue3473.SerializeWriterJavaSqlDateTest#yyyy_MM_dd_HH_mm_ss_test:TESTSUCCESS]
 * PASS
 * bic:8e985930057c21c95a3065ca9f2cad3b5e42d4ba
 * 测试bic [com.alibaba.fastjson.serializer.issue3473.SerializeWriterJavaSqlDateTest#yyyy_MM_dd_HH_mm_ss_test:TESTSUCCESS]
 * PASS
 * bic:d30069235d846b910d8371171aa855e4eea3a97b
 * 测试bic [com.alibaba.fastjson.serializer.issue3473.SerializeWriterJavaSqlDateTest#yyyy_MM_dd_HH_mm_ss_test:TESTSUCCESS]
 * PASS
 * bic:52652252e7095ecb11bfe8baddd2a087c04fa14e
 * 测试bic [com.alibaba.fastjson.serializer.issue3473.SerializeWriterJavaSqlDateTest#yyyy_MM_dd_HH_mm_ss_test:NONE]
 * FAL
 * 回归+1
 * 成功1.0个，共1.0个: 1.0
 */
/**
 * failed case
 * 总共分析了1条commit
 *
 * pRFC in total :1
 * 1.0%
 * 41b1477c7ca40914793b0a8dc86fa0e9a2089972开始执行测试约减
 * 迁移成功[com.alibaba.json.bvt.issue_2800.Issue2866#test_for_issue:NONE]
 * bic:e895a8a81882c520d853792a22c425e2c4b4c68d
 * 迁移后编译失败
 * bic:6fb46d84d47a9c90a79069d529e4e3492cc006cd
 * 迁移后编译失败
 * bic:e1a0857dc957c35f168675c0c79cde0a26ee30b1
 * 迁移后编译失败
 * bic:58e01a223b5be8c60227fcc29d7aa742969e3497
 * 迁移后编译失败
 * bic:453f9d87d3a557e5716c13005cdd7d31fbfd03cc
 * 迁移后编译失败
 * bic:a0839a2ec1942c6c1efd84479d87df0d9afd47ad
 * 迁移后编译失败
 * bic:bd9955d4a8e7159c3f303db2263d47e3a5a3c3ae
 * 迁移后编译失败
 * bic:5300025e16f21124badb8a8bfb6f0820a5cd1be5
 * 迁移后编译失败
 * bic:fd121663929791b1bbff95fc236e6dc68dde6bca
 * 迁移后编译失败
 * bic:4176c40ed7501df1be55a832b51fb06dd559b0ef
 * 迁移后编译失败
 * bic:53c196f19900362140770fd7faabf6357af210b3
 * 迁移后编译失败
 * bic:00f42fa735493366932e07430ba690dde0a700ad
 * 迁移后编译失败
 * bic:6eb60d2e8929199df1d61629e6a1d24647b1ba8e
 * 迁移后编译失败
 * bic:d98ce7c78b626437c42d28cee5e3abf736a988ad
 * 迁移后编译失败
 * bic:a440799107553fdc3f79801a231fb96522b09da9
 * 迁移后编译失败
 * bic:bf045c271ded363bad1ba626d6979a6be0d8273a
 * 迁移后编译失败
 * bic:d73f87c25a04bd0732ed4b9508fbc1b53301cffb
 * 迁移后编译失败
 * bic:7f46b463b9d2ac8ed829064616b2a63a6eb8eb5d
 * 迁移后编译失败
 * bic:8bb3e504401127940bc6b7a621cb3673f53b759a
 * 迁移后编译失败
 * bic:6ec974879e910d58ef4faa69b0a34251cb7c033f
 * 迁移后编译失败
 * bic:ea762aed466676a6fe73b02ada01f333c0c18cf1
 * 测试bic [com.alibaba.json.bvt.issue_2800.Issue2866#test_for_issue:NONE]
 * FAL
 * bic:54debd0460cbcb1afcecf1340c718b6114ee0005
 * 测试bic [com.alibaba.json.bvt.issue_2800.Issue2866#test_for_issue:NONE]
 * FAL
 * bic:39ccafb821276bec50e59e9cb7617627632bdb7c
 * 测试bic [com.alibaba.json.bvt.issue_2800.Issue2866#test_for_issue:NONE]
 * FAL
 * bic:4364f588a6d067aeff43b7debca5b3711f665e1c
 * 测试bic [com.alibaba.json.bvt.issue_2800.Issue2866#test_for_issue:NONE]
 * FAL
 * bic:1ad29ff769f5037f4cb53609cfd67c8a3fa4a736
 * 测试bic [com.alibaba.json.bvt.issue_2800.Issue2866#test_for_issue:NONE]
 * FAL
 * bic:f5b7a5fce0b61e7150bb27813135e1b1efb97b46
 * 测试bic [com.alibaba.json.bvt.issue_2800.Issue2866#test_for_issue:NONE]
 * FAL
 * bic:4e7d252c12df2d27e83742f0ba6d01e490323d74
 * 测试bic [com.alibaba.json.bvt.issue_2800.Issue2866#test_for_issue:NONE]
 * FAL
 * bic:1ddcf0c37345e2c733eba22a0ad0d5d007276b63
 * 测试bic [com.alibaba.json.bvt.issue_2800.Issue2866#test_for_issue:NONE]
 * FAL
 * bic:d03addc36595bcb99aece173927532fc7125bc15
 * 测试bic [com.alibaba.json.bvt.issue_2800.Issue2866#test_for_issue:NONE]
 * FAL
 * bic:d246284343742e9b5663e813ad171ca55eee7dc4
 * 迁移后编译失败
 * 查找失败
 * 成功1.0个，共1.0个: 1.0
 */
}
package org.regminer.miner.ct.api;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.lib.Repository;
import org.regminer.miner.common.model.TestCaseX;
import org.regminer.miner.common.tool.RepositoryProvider;
import org.regminer.miner.ct.domain.JDK;
import org.regminer.miner.ct.model.CompileResult;
import org.regminer.miner.ct.model.TestCaseResult;
import org.regminer.miner.ct.model.TestResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CtContextTest extends TestCase {
    //FIXME 4 TEST MORE FEATURE
    public void testCompileAndTest() {
        CtContext ctContext = new CtContext(new BaseCompileAndTest());
        ctContext.setProjectDir(new File("/Users/lsn/Desktop/regminer/regminer-x/regminerx-test/datas/commons-jexl"));
        //ctContext.setProjectDir(new File("../projects-for-test/fastjson-84835dd3cfb46939eb595742ea8d7d74918034bd"));
        CompileResult compileResult = ctContext.compile();
        Assert.assertEquals(CompileResult.CompileState.SUCCESS, compileResult.getState());
        List<TestCaseX> testCaseXES = new ArrayList<>();
        TestCaseX testCaseX = new TestCaseX();
        testCaseX.setPackageName("com.alibaba.json.bvt.parser.deser.list");
        testCaseX.setClassName("ListFieldTest");
        testCaseX.setMethodName("test_for_list");
        testCaseXES.add(testCaseX);
        TestResult testResult = ctContext.test(testCaseXES, compileResult.getEnvCommands());
        testResult.getCaseResultMap().forEach((key, value) -> {
            Assert.assertEquals(TestCaseResult.TestState.PASS, value.getState());
            Assert.assertNotNull(value.getTestCommands());
            Assert.assertFalse(value.getTestCommands().equalsIgnoreCase(""));
        });
        Assert.assertTrue(compileResult.getEnvCommands().sizes() == 2);
    }

    public void testAutoCompileAndTest() {
        CtContext ctContext = new CtContext(new AutoCompileAndTest());
        ctContext.setProjectDir(new File("/Users/lsn/Desktop/regminer/regminer-x/regminerx-test/datas/commons-jexl"));
//        ctContext.setProjectDir(new File("../projects-for-test/fastjson-84835dd3cfb46939eb595742ea8d7d74918034bd"));
        CompileResult compileResult = ctContext.compile();
        Assert.assertEquals(CompileResult.CompileState.SUCCESS, compileResult.getState());
        JDK[] jdks = {compileResult.getCompileWay().getJdk()};
        ctContext.setJdkSearchRange(jdks);
        List<TestCaseX> testCaseXES = new ArrayList<>();
        TestCaseX testCaseX = new TestCaseX();
        testCaseX.setPackageName("com.alibaba.json.bvt.parser.deser.list");
        testCaseX.setClassName("ListFieldTest");
        testCaseX.setMethodName("test_for_list");
        testCaseXES.add(testCaseX);
        TestResult testResult = ctContext.test(testCaseXES, compileResult.getEnvCommands());
        testResult.getCaseResultMap().forEach((key, value) -> {
            Assert.assertEquals(TestCaseResult.TestState.PASS, value.getState());
            Assert.assertNotNull(value.getTestCommands());
            Assert.assertFalse(value.getTestCommands().equalsIgnoreCase(""));
        });
        Assert.assertTrue(compileResult.getEnvCommands().sizes() == 2);
    }

    public void testAutoCompileForCommit() throws Exception {
        File projectDir = new File("/Users/lsn/Desktop/regminer/regminer-x/regminerx-test/datas/commons-jexl");
        Repository repository = RepositoryProvider.getRepoFromLocal(projectDir);
        Git git = new Git(repository);
        String commitID = "69dd3d765d5a7a7994d6604b6e7c50b5adc5f902";
        git.checkout().setName(commitID).setForced(true).call();
        CtContext ctContext = new CtContext(new AutoCompileAndTest());
        ctContext.setProjectDir(projectDir);
        CompileResult result = ctContext.compile();

        git.reset().setMode(ResetCommand.ResetType.HARD).call();
        if(result.getCompileWay() != null) {
            System.out.println(commitID + ":" + result.getState().name()
                    + ":" + result.getCompileWay().getCompiler().name() + ":" + result.getCompileWay().getJdk().name());
        }
        else {
            System.out.println(commitID + ":" + result.getState().name() + ":" + "CompileWay NULL");
        }

    }
}
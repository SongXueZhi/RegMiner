package org.regminer.ct.api;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.lib.Repository;
import org.junit.Ignore;
import org.regminer.common.model.RelatedTestCase;
import org.regminer.common.tool.RepositoryProvider;
import org.regminer.ct.domain.JDK;
import org.regminer.ct.model.CompileResult;
import org.regminer.ct.model.TestCaseResult;
import org.regminer.ct.model.TestResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Ignore("本地测试路径不存在")
public class CtContextTest extends TestCase {
    //FIXME 4 TEST MORE FEATURE
    public void testCompileAndTest() {
        CtContext ctContext = new CtContext(new BaseCompileAndTest());
        ctContext.setProjectDir(new File("/Users/lsn/Desktop/regminer/regminer-x/regminerx-test/datas/commons-jexl"));
        //ctContext.setProjectDir(new File("../projects-for-test/fastjson-84835dd3cfb46939eb595742ea8d7d74918034bd"));
        CompileResult compileResult = ctContext.compile();
        Assert.assertEquals(CompileResult.CompileState.SUCCESS, compileResult.getState());
        List<RelatedTestCase> testCaseXES = new ArrayList<>();
        RelatedTestCase testCaseX = new RelatedTestCase();
        testCaseX.setEnclosingClassName("com.alibaba.json.bvt.parser.deser.list.ListFieldTest");
        testCaseX.setMethodName("test_for_list");
        testCaseXES.add(testCaseX);
        TestResult testResult = ctContext.test(testCaseXES, compileResult.getCompileWay());
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
        List<RelatedTestCase> testCaseXES = new ArrayList<>();
        RelatedTestCase testCaseX = new RelatedTestCase();
        testCaseX.setEnclosingClassName("com.alibaba.json.bvt.parser.deser.list.ListFieldTest");
        testCaseX.setMethodName("test_for_list");
        testCaseXES.add(testCaseX);
        TestResult testResult = ctContext.test(testCaseXES, compileResult.getCompileWay());
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
        if (result.getCompileWay() != null) {
            System.out.println(commitID + ":" + result.getState().name()
                    + ":" + result.getCompileWay().getCompiler().name() + ":" + result.getCompileWay().getJdk().name());
        } else {
            System.out.println(commitID + ":" + result.getState().name() + ":" + "CompileWay NULL");
        }

    }
}
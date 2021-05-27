package miner.migrate;

import ast.JdtFieldRetriever;
import ast.JdtMethodRetriever;
import callgraph.MethodCaller;
import constant.Conf;
import callgraph.model.CallNode;
import constant.Constant;
import model.MigrateItem.MigrateFailureType;
import model.PotentialRFC;
import model.RelatedTestCase;
import model.TestFile;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.jetbrains.annotations.NotNull;
import utils.CodeUtil;
import utils.CompilationUtil;
import utils.FileUtilx;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author sxz
 */
public class TestCaseMigrater extends Migrater {
    public final static int PASS = 0;
    public final static int FAL = 1;
    public final static int CE = -1;
    public final static int UNRESOLVE = -2;


    public void migrate(@NotNull PotentialRFC pRFC, @NotNull Set<String> bicSet) {
        FileUtilx.log(pRFC.getCommit().getName() + " 开始迁移bic");
        for (String bic : bicSet) {
            try {
                migrate(pRFC, bic);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int migrate(@NotNull PotentialRFC pRFC, String bic) throws Exception {
        FileUtilx.log("bic:" + bic);
        File bicDirectory = checkout(pRFC.getCommit().getName(), bic, "bic");
        pRFC.fileMap.put(bic, bicDirectory);
//		// 第一次编译未copy时候编译尝试
//		if (!comiple(bicDirectory, false)) {
//			FileUtilx.log("本身编译失败");
//			return CE;
//		}
        // 第一次编译成功,则开始依赖图匹配和merge
        mergeTwoVersion_BaseLine(pRFC,bicDirectory);

        // 编译
        if (comiple(bicDirectory, true)) {
            int a = testSuite(bicDirectory, pRFC.getTestCaseFiles());
            return a;
        } else {
            FileUtilx.log("迁移后编译失败");
            return CE;
        }
    }

    public boolean comiple(File file, boolean record) throws Exception {
        exec.setDirectory(file);
        return exec.execBuildWithResult(Conf.compileLine, record);
    }

    public int testSuite(File file, @org.jetbrains.annotations.NotNull List<TestFile> testSuites) throws Exception {
        exec.setDirectory(file);
        StringJoiner sj = new StringJoiner(";", "[", "]");
        Iterator<TestFile> iterator = testSuites.iterator();
        boolean result1 = false;
        boolean result = false;
        while (iterator.hasNext()) {
            TestFile testSuite = iterator.next();
            int res = testBFCPMethod(testSuite, sj);
            if (res == PASS) {
                result1 = true;
            }
            if (res == FAL) {
                result = true;
            }
        }
        if (result1) {
            return PASS;
        } else if (result) {
            return FAL;
        } else {
            return UNRESOLVE;
        }
    }

    public int testBFCPMethod(@NotNull TestFile testSuite, StringJoiner sj) throws Exception {
        boolean result = false;
        boolean result1 = false;
        Map<String, RelatedTestCase> methodMap = testSuite.getTestMethodMap();
        for (Iterator<Map.Entry<String, RelatedTestCase>> it = methodMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, RelatedTestCase> entry = it.next();
            String testCase = testSuite.getQualityClassName() + Conf.methodClassLinkSymbolForTest
                    + entry.getKey().split("[(]")[0];
            MigrateFailureType type = exec.execTestWithResult(Conf.testLine + testCase);
            sj.add(testCase + ":" + type.getName());

            if (type == MigrateFailureType.NONE) {
                result = true;
            }
            if (type == MigrateFailureType.TESTSUCCESS) {
                result1 = true;
            }
        }
        FileUtilx.log("测试bic " + sj);
        if (result1) {
            FileUtilx.log("PASS");
            return PASS;
        }
        if (result) {
            FileUtilx.log("FAL");
            return FAL;
        }
        FileUtilx.log("UNRESOLVE");
        return UNRESOLVE;
    }


}

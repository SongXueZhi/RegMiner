package miner.migrate;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringJoiner;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import ast.FieldRetriever;
import ast.MemberRetriever;
import ast.MethodCaller;
import constant.Conf;
import model.CallNode;
import model.MigrateItem.MigrateFailureType;
import model.PotentialRFC;
import model.RelatedTestCase;
import model.TestFile;
import utils.CodeUtil;
import utils.CompilationUtil;
import utils.FileUtilx;

/**
 * 
 * @author sxz
 *
 */
public class TestCaseMigrater extends Migrater {
	public final static int PASS = 0;
	public final static int FAL = 1;
	public final static int CE = -1;
	public final static int UNRESOLVE = -2;

	public TestCaseMigrater() {
	}

	public void migrate(PotentialRFC pRFC, Set<String> bicSet) {
		FileUtilx.log(pRFC.getCommit().getName() + " 开始迁移bic");
		for (String bic : bicSet) {
			try {
				migrate(pRFC, bic);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public int migrate(PotentialRFC pRFC, String bic) throws Exception {
		FileUtilx.log("bic:" + bic);
		File bicDirectory = checkout(pRFC.getCommit().getName(), bic, "bic");
		pRFC.fileMap.put(bic, bicDirectory);
//		// 第一次编译未copy时候编译尝试
//		if (!comiple(bicDirectory, false)) {
//			FileUtilx.log("本身编译失败");
//			return CE;
//		}
		File bfcDir = pRFC.fileMap.get(pRFC.getCommit().getName());
		// 第一次编译成功,则开始依赖图匹配
		copyTestCase(pRFC, bicDirectory, bfcDir);
		copyTestRelates(pRFC, bicDirectory, bfcDir);
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

	public int testSuite(File file, List<TestFile> testSuites) throws Exception {
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

	public int testBFCPMethod(TestFile testSuite, StringJoiner sj) throws Exception {
		boolean result = false;
		boolean result1 = false;
		Map<String, RelatedTestCase> methodMap = testSuite.getTestMethodMap();
		for (Iterator<Map.Entry<String, RelatedTestCase>> it = methodMap.entrySet().iterator(); it.hasNext();) {
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

	public void copyTestRelates(PotentialRFC pRFC, File tDir, File bfcDir) {
		List<TestFile> testRelates = pRFC.getTestRelates();
		for (TestFile testRelate : testRelates) {
			// 测试文件是被删除则什么也不作。
			if (testRelate.getNewPath().contains("/dev/null")) {
				continue;
			}
			File file = new File(Conf.TMP_FILE + File.separator + pRFC.getCommit().getName() + File.separator
					+ testRelate.getNewPath());
			// 测试文件是被删除则什么也不作。

			String targetPath = testRelate.getNewPath();
			// 测试文件不是删除，则copy
			targetPath = FileUtilx.getDirectoryFromPath(targetPath);
			File file1 = new File(tDir.getAbsoluteFile() + File.separator + targetPath);
			if (!file1.exists()) {
				file1.mkdirs();
			}
			exec.exec("cp " + file.getAbsolutePath() + " " + file1.getAbsolutePath());

		}
	}

	/**
	 * 当前只迁移了tessuite，因此针对testrelated需要其他的策略
	 * 
	 * @param pRFC
	 * @param tDir
	 * @param bfcDir
	 */
	public void copyTestCase(PotentialRFC pRFC, File tDir, File bfcDir) {
		List<TestFile> tcs = pRFC.getTestCaseFiles();
		String[] tFiles = CodeUtil.getJavaFiles(tDir);
		String[] bfcFils = CodeUtil.getJavaFiles(bfcDir);
		for (TestFile tc : tcs) {
			String className = tc.getQualityClassName();
			// 现在BIC中寻找这个类
			String tpath = findJavaFile(className, tFiles);
			if (tpath == null) { // 如果类没有,就直接迁移过去
				String bfcPath = tc.getNewPath();
				String bfcClassPath = findClassFile(className, bfcFils);
				if (bfcClassPath == null) {
					continue;
				}
				// 把类粘过去并消除无关方法
				copyAndPruneNoUsedMethod(bfcPath, bfcClassPath, tDir, bfcDir, tc.getTestMethodMap());
			} else {
				// 只把方法
				String contentTo = FileUtilx.readContentFromFile(tDir.getAbsolutePath() + File.separator + tpath);
				CompilationUnit unitTo = CompilationUtil.parseCompliationUnit(contentTo);
				// TODO 往回查找老的版本该方法存在的最老版本
				String contenFrom = FileUtilx
						.readContentFromFile(bfcDir.getAbsolutePath() + File.separator + tc.getNewPath());
				CompilationUnit unitFrom = CompilationUtil.parseCompliationUnit(contenFrom);
				List<ImportDeclaration> imports = unitFrom.imports();
				Map<String, ImportDeclaration> needToCopyImport = new HashMap<>();
				for (Map.Entry<String, RelatedTestCase> entry : tc.getTestMethodMap().entrySet()) {
					// 注意此处的MemberRetriever的实现并不会访问内部类中的方法，所以内部类中的方法将不会被影响
					MethodDeclaration md = entry.getValue().getMethod().getMethodDeclaration();
					MemberRetriever mr = new MemberRetriever();
					unitTo.accept(mr);
					if (whetherMehothdExits(mr.getMemberList(), md)) {
						continue;
					}
					// 方法不存在,确定方法依赖的import
					for (ImportDeclaration impo : imports) {
						String str = impo.getName().toString();
						String pattern = str.substring(str.lastIndexOf(".") + 1, str.length());
						if (md.toString().contains(pattern)) {
							needToCopyImport.put(str, impo);
						}
					}
					// copy 方法
					ASTNode.copySubtree(unitTo.getAST(), md);
				}
				// copy所有方法依赖的import
				List<ImportDeclaration> importsTO = unitTo.imports();
				for (Map.Entry<String, ImportDeclaration> entry : needToCopyImport.entrySet()) {
					// 如果需要copy的import在Ci中不存在,则copy
					if (whetherImportExits(importsTO, entry.getValue())) {
						continue;
					}
					ASTNode.copySubtree(unitTo.getAST(), entry.getValue());
				}
				unitTo.recordModifications();
				Document document = new Document();
				TextEdit edits = unitTo.rewrite(document, null);
				try {
					edits.apply(document);
					FileUtils.write(new File(tDir, tpath), document.get());
				} catch (MalformedTreeException | BadLocationException | IOException e) {
					FileUtilx.log(e.getMessage() + "\n bfcdir: " + bfcDir + "\n tdir: " + tDir);
				}

			}
		}
	}

	// Ci没有建图的原因是因为,我无法从bcel构建Ci将测试用例迁过去的图
	public boolean whetherImportExits(List<ImportDeclaration> imports, ImportDeclaration im) {
		for (ImportDeclaration iim : imports) {
			if (iim.toString().equals(im.toString())) {
				return true;
			}
		}
		return false;
	}

	public boolean whetherMehothdExits(List<MethodDeclaration> mLsit, MethodDeclaration md) {
		for (MethodDeclaration mx : mLsit) {
			if (mx.getName().toString().equals(md.getName().toString())) {
				return true;
			}
		}
		return false;
	}

	public void copyAndPruneNoUsedMethod(String javaFilepath, String classFile, File tDir, File bfcDir,
			Map<String, RelatedTestCase> methodMap) {
		MethodCaller mcaller = new MethodCaller();
		String content = FileUtilx.readContentFromFile(bfcDir.getAbsolutePath() + File.separator + javaFilepath);
		CompilationUnit unit = CompilationUtil.parseCompliationUnit(content);
		JavaClass clazz = CodeUtil.lookupClassbyFile(bfcDir.getAbsolutePath() + File.separator + classFile);
		Method[] methods = clazz.getMethods();
		Set<String> methodsSet = new HashSet<>();
		// 获取每个方法,加每个方法在该类中的调用
		for (Map.Entry<String, RelatedTestCase> entry : methodMap.entrySet()) {
			Method method = CodeUtil.methodMatch(methods, entry.getValue().getMethod().getMethodDeclaration());
			CallNode node = mcaller.getMethodCall(method, clazz);
			// 将该方法本身加入
			methodsSet.add(node.getMethodName());
			for (CallNode child : node.childList) {
				// 如果是该类中被调用的方法,就加入
				if (child.getType() == CallNode.Type.method && child.getClassName().equals(clazz.getClassName())) {
					// 将孩子们加入
					methodsSet.add(child.getMethodName());
				}
			}
		}
		MemberRetriever mr = new MemberRetriever();
		unit.accept(mr);
		ASTRewrite rewriter = ASTRewrite.create(unit.getAST());
		for (Method m : methods) {
			String msig = CodeUtil.getMethodSig(m.getName(), m.getArgumentTypes());
			if (methodsSet.contains(msig)) {
				continue;
			}
			MethodDeclaration md = CodeUtil.methodMatch(mr.getMemberList(), m);
			if (md != null) {
				rewriter.remove(md, null);
			}

		}
		unit.recordModifications();
		Document doc = new Document(content);
		TextEdit edits = rewriter.rewriteAST(doc, null);
		try {
			edits.apply(doc);
			// 删除无关字段和import
			String code;
			code = pruneImport(pruneField(doc.get()));
			FileUtils.write(new File(tDir, javaFilepath), code);
		} catch (MalformedTreeException | BadLocationException | IOException e) {
			FileUtilx.log(e.getMessage() + "\n bfcdir: " + bfcDir + "\n tdir: " + tDir);
		}
	}

	public String pruneImport(String content) throws MalformedTreeException, BadLocationException {
		CompilationUnit unit = CompilationUtil.parseCompliationUnit(content);
		ASTRewrite rewriter = ASTRewrite.create(unit.getAST());
		// 删除无用的import
		List<ImportDeclaration> importNodeList = unit.imports();
		List<TypeDeclaration> types = unit.types();
		for (ImportDeclaration importDeclaration : importNodeList) {
			String str = importDeclaration.getName().toString();
			String pattern = str.substring(str.lastIndexOf(".") + 1, str.length());
			boolean flag = false;
			for (TypeDeclaration type : types) {
				if (type.toString().contains(pattern)) {
					flag = true;
				}
			}
			if (flag == false) {
				rewriter.remove(importDeclaration, null);
			}
		}
		unit.recordModifications();
		Document doc = new Document(content);
		TextEdit edits = rewriter.rewriteAST(doc, null);
		edits.apply(doc);
		return doc.get();
	}

	public String pruneField(String content) throws MalformedTreeException, BadLocationException {
		CompilationUnit unit = CompilationUtil.parseCompliationUnit(content);
		ASTRewrite rewriter = ASTRewrite.create(unit.getAST());
		List<TypeDeclaration> types = unit.types();
		// 与MethodRetriever不同FieldRetriever可以访问到内部类中的字段声明
		FieldRetriever fr = new FieldRetriever();
		unit.accept(fr);
		Map<FieldDeclaration, List<VariableDeclarationFragment>> filedMap = fr.fieldMap;
		for (Entry<FieldDeclaration, List<VariableDeclarationFragment>> entry : filedMap.entrySet()) {
			FieldDeclaration fd = entry.getKey();
			List<VariableDeclarationFragment> vflist = entry.getValue();
			int a = 0;
			for (VariableDeclarationFragment filed : vflist) {
				boolean flag = false;
				for (TypeDeclaration type : types) {
					if (type.toString().contains(filed.getName().toString())) {
						flag = true;
					}
				}
				if (flag == false) {
					a++;
					rewriter.remove(filed, null);
				}
			}
			if (a == vflist.size()) {
				rewriter.remove(fd, null);
			}
		}

		unit.recordModifications();
		Document doc = new Document(content);
		TextEdit edits = rewriter.rewriteAST(doc, null);
		edits.apply(doc);
		return doc.get();
	}

	public String findJavaFile(String className, String[] projectJavaFiles) {
		String path = className.replace(".", File.separator) + ".java";
		for (String file : projectJavaFiles) {
			if (file.contains(path)) {
				return file;
			}
		}
		return null;
	}

	public String findClassFile(String className, String[] projectJavaFiles) {
		String path = className.replace(".", File.separator) + ".class";
		for (String file : projectJavaFiles) {
			if (file.contains(path)) {
				return file;
			}
		}
		return null;
	}

	public void copyToTarget(PotentialRFC pRFC, File targerProjectDirectory) {
		// copy
		String targetPath = null;
		for (TestFile testFile : pRFC.getTestCaseFiles()) {
			File file = new File(Conf.TMP_FILE + File.separator + pRFC.getCommit().getName() + File.separator
					+ testFile.getNewPath());
			// 测试文件是被删除则什么也不作。
			if (testFile.getNewPath().contains("/dev/null")) {
				continue;
			}

			targetPath = testFile.getNewPath();
			// FileUtilx.log("迁移的文件有：" + targetPath);
			// 测试文件不是删除，则copy
			targetPath = FileUtilx.getDirectoryFromPath(targetPath);
			File file1 = new File(targerProjectDirectory.getAbsoluteFile() + File.separator + targetPath);
			if (!file1.exists()) {
				file1.mkdirs();
			}
			exec.exec("cp -rf " + file.getAbsolutePath() + " " + file1.getAbsolutePath());

		}
	}
}

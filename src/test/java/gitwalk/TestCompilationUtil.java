package gitwalk;

import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.text.edits.MalformedTreeException;
import org.junit.Before;
import org.junit.Test;

import ast.FieldRetriever;
import ast.ImportTrimmer;
import collector.migrate.TestCaseMigrater;
import utils.CompilationUtil;

public class TestCompilationUtil {
	String fileContent = "";

	@Before
	public void init() {
		String filePath = "/home/sxz/Desktop/Solution.java";
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(filePath); // 内容是：abc
			StringBuilder sb = new StringBuilder();
			int temp = 0;
			// 当temp等于-1时，表示已经到了文件结尾，停止读取
			while ((temp = fis.read()) != -1) {
				sb.append((char) temp);
			}
			fileContent = sb.toString();
		} catch (Exception exc) {

		}
	}

	@Test
	public void testGetAllImport() throws MalformedTreeException, IOException {
		ImportTrimmer itm = new ImportTrimmer();
//		ASTRewrite rewriter = itm.prune(CompilationUtil.parseCompliationUnit(fileContent));
//		Document doc = new Document(fileContent);
//		TextEdit edits = rewriter.rewriteAST(doc, null);
//		edits.apply(doc);
//		FileUtils.write(new File("Solution.java"), doc.get());
		CompilationUnit unit = CompilationUtil.parseCompliationUnit(fileContent);
		TypeDeclaration type = (TypeDeclaration) unit.types().get(0);
		FieldRetriever fr = new FieldRetriever();
		unit.accept(fr);

		MethodDeclaration[] m = type.getMethods();
		MethodDeclaration method = m[3];
		System.out.println(method.getBody().toString());
		itm.getMethodCallGraph(method, unit);
	}

//
//	// @Test
//	public void testGetMethodList() {
//
//		List<Methodx> methodList = CompilationUtil.getAllMethod(fileContent);
//		for (Methodx method : methodList) {
//			String name = method.getSignature();
//			int p = method.getStartLine();
//			int q = method.getStopLine();
//			System.out.println(name + " " + p + " --> " + q);
//		}
//	}
//
//	// @Test
//	public void testGetClassName() {
//		String ss = CompilationUtil.getQualityClassName(fileContent);
//		System.out.println(ss);
//	}
	@Test
	public void testPruneMethod() throws Exception {
		TestCaseMigrater tm = new TestCaseMigrater("");
	}
}

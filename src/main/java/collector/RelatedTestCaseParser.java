package collector;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.lib.Repository;

import constant.Conf;
import model.ChangedFile.Type;
import model.Methodx;
import model.PotentialRFC;
import model.RelatedTestCase;
import model.TestFile;
import utils.CompilationUtil;
import utils.GitUtil;

//获取每一个测试文件中的测试方法（暂时不用），并且过滤测试文件是否真实
//如果不包含junit或者@test则移除
//过滤完成后，如果若有测试文件都被移除，则pRFC移除
public class RelatedTestCaseParser {
	private Repository repo;

	public RelatedTestCaseParser(Repository repo) {
		this.repo = repo;
	}

	// 现在每个测试文件被分为测试相关和测试文件。
	public void parseTestCases(PotentialRFC pRFC) throws Exception {
		Iterator<TestFile> iterator = pRFC.getTestCaseFiles().iterator();
		while (iterator.hasNext()) {
			TestFile file = iterator.next();
			String code = GitUtil.getContextWithFile(repo, pRFC.getCommit(), file.getNewPath());
			FileUtils.writeStringToFile(new File(
					Conf.TMP_FILE + File.separator + pRFC.getCommit().getName() + File.separator + file.getNewPath()),
					code);
			if (!isTestSuite(code)) {
				file.setType(Type.TEST_RELATE);
			} else {
				file.setType(Type.TEST_SUITE);
				file.setQualityClassName(CompilationUtil.getQualityClassName(code));
				Map<String, RelatedTestCase> methodMap = parse(file, code);
				file.setTestMethodMap(methodMap);
			}
		}
	}

	private Map<String, RelatedTestCase> parse(TestFile file, String code) throws Exception {
		List<Edit> editList = file.getEditList();
//		cleanEmpty(editList);
		List<Methodx> methodList = CompilationUtil.getAllMethod(code);
		Map<String, RelatedTestCase> testCaseMap = new HashMap<>();
		getRelatedTestCase(editList, methodList, testCaseMap);
		return testCaseMap;
	}

	private void getRelatedTestCase(List<Edit> editList, List<Methodx> methodList,
			Map<String, RelatedTestCase> testCaseMap) {
		for (Edit edit : editList) {
			matchAll(edit, methodList, testCaseMap);
		}
	}

	private void matchAll(Edit edit, List<Methodx> methods, Map<String, RelatedTestCase> testCaseMap) {
		for (Methodx method : methods) {
			match(edit, method, testCaseMap);
		}
	}

	//
	private void match(Edit edit, Methodx method, Map<String, RelatedTestCase> testCaseMap) {
		int editStart = edit.getBeginB();
		int editEnd = edit.getEndB();

		int methodStart = method.getStartLine();
		int methodStop = method.getStopLine();

		if (editStart <= methodStart && editEnd >= methodStop || editStart >= methodStart && editEnd <= methodStop
				|| editEnd >= methodStart && editEnd <= methodStop
				|| editStart >= methodStart && editStart <= methodStop) {
			String name = method.getSignature();
			if (!testCaseMap.containsKey(name)) {
				RelatedTestCase testCase = new RelatedTestCase();
				// 暂时不设定方法的类型
				// testCase.setType(RelatedTestCase.Type.Created);
				testCase.setMethod(method);
				testCaseMap.put(name, testCase);
			}
		}

	}

	private boolean isTestSuite(String code) {
		if (code.contains("junit") || code.contains("@Test")) {
			return true;
		}
		return false;
	}

}

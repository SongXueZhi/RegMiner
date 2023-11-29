package org.regminer.common.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestFile extends ChangedFile {
	public Type type;
	private Map<String, RelatedTestCase> testMethodMap = new HashMap<>();
	private String qualityClassName;

	
	public TestFile(String newPath) {
		super(newPath);
	}

	public String getQualityClassName() {
		return qualityClassName;
	}

	public void setQualityClassName(String qualityClassName) {
		this.qualityClassName = qualityClassName;
	}

	public Map<String, RelatedTestCase> getTestMethodMap() {
		return testMethodMap;
	}

	public void setTestMethodMap(Map<String, RelatedTestCase> testMethodMap) {
		this.testMethodMap = testMethodMap;
	}

	public List<TestCaseX> toTestCaseXList() {
		List<TestCaseX> testCaseXList = new ArrayList<>();
		for (Map.Entry<String, RelatedTestCase> entry : testMethodMap.entrySet()) {
			RelatedTestCase relatedTestCase = entry.getValue();
			Methodx method = relatedTestCase.getMethod(); // 假设 Methodx 包含所需信息

			// 创建并配置 TestCaseX 对象
			TestCaseX testCaseX = new TestCaseX();
			testCaseX.setFilePath(this.getNewPath()); // 假设 TestFile 的 newPath 与 TestCaseX 的 filePath 相关
			testCaseX.setPackageName(this.qualityClassName.substring(0,this.qualityClassName.lastIndexOf("."))); //
			// 假设 Methodx 有这些方法
			testCaseX.setClassName(this.qualityClassName);
			testCaseX.setMethodName(method.getSimpleName());
			testCaseXList.add(testCaseX);
		}
		return testCaseXList;
	}

}

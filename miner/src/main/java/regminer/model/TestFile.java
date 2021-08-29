package regminer.model;

import java.util.HashMap;
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

}

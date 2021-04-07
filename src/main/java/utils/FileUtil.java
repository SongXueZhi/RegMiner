package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class FileUtil {

	public void createdNewDirectory(String path) {

	}

	// /home/sxz/document/mmmmm-ddahkdak989/123.java
	public static String getDirectoryFromPath(String path) {
		return path.substring(0, path.lastIndexOf("/"));
	}

	@SuppressWarnings("deprecation")
	public static void log(String line, String path, String header) throws Exception {
		File file = new File(path);
		if (!file.isDirectory()) {
			file.delete();
			file.mkdirs();
		}
		File file1 = new File(file.getAbsoluteFile() + File.separator + header + "_" + ".txt");
		FileUtils.writeStringToFile(file1, line, true);
	}

	public static String getUUID() {
		return UUID.randomUUID().toString();

	}

	public static String readContentFromFile(String path) {
		File file = new File(path);
		String result = null;
		try {
			InputStream is = new FileInputStream(file);
			if (file.exists() && file.isFile()) {
				BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
				StringBuffer sb2 = new StringBuffer();
				String line = null;
				while ((line = br.readLine()) != null) {
					sb2.append(line + "\n");
				}
				br.close();
				result = sb2.toString();
			}
		} catch (Exception e) {

		}
		return result;
	}

	public static void parseJson(String jsonText) {
		HashMap<String, String> map = new HashMap<String, String>();
		JSONArray jarr = JSONArray.parseArray(jsonText);
		for (Iterator<?> iterator = jarr.iterator(); iterator.hasNext();) {
			JSONObject job = (JSONObject) iterator.next();
			if (map.containsKey(job.get(job))) {
				continue;
			} else {

			}
		}

	}

	public static void judgeObj(JSONObject job) {
//		if (job.get(getUUID())) {
//
//		}

	}

	public static void main(String[] args) {
		String content = readContentFromFile("/home/sxz/data/github1.json");
		parseJson(content);
	}
}

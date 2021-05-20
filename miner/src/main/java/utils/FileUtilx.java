package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import constant.Conf;

public class FileUtilx {

	public void createdNewDirectory(String path) {

	}

	// /home/sxz/document/mmmmm-ddahkdak989/123.java
	public static String getDirectoryFromPath(String path) {
		return path.substring(0, path.lastIndexOf("/"));
	}

	@SuppressWarnings("deprecation")
	public static synchronized void apendResult(String line) {
		File file = new File(Conf.RESULT_Path);
		try {
			FileUtils.writeStringToFile(file, line, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public static synchronized void log(String block) {
		File file = new File(Conf.PROJECT_PATH + File.separator + "log" + Thread.currentThread().getName());
		try {
			FileUtils.writeStringToFile(file, block + "\n", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	public static Set<String> readSetFromFile(String path) {
		Set<String> result = new HashSet<>();
		File file = new File(path);
		try {
			InputStream is = new FileInputStream(file);
			if (file.exists() && file.isFile()) {
				BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
				String line = null;
				while ((line = br.readLine()) != null) {
					result.add(line);
				}
				br.close();
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

	public static void main(String[] args) {
		String content = readContentFromFile("/home/sxz/data/github1.json");
		parseJson(content);
	}
}

package org.regminer.common.tool;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.model.Dependency;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jerry Zhang <zhangjian16@fudan.edu.cn>
 * @desc
 * @date 2023/12/14 17:10
 */

public class MavenDependencyProvider {

    private static Logger logger = LogManager.getLogger(MavenDependencyProvider.class);
    public static List<Dependency> getAllMavenDependencies(String packageName) {
        String urlStr = "https://search.maven.org/solrsearch/select?q=" + packageName + "&rows=20&wt=json";
        return getDependenciesFromMaven(urlStr)
                .stream()
                .filter(dependency -> packageName.startsWith(dependency.getGroupId()))
                .collect(Collectors.toList());
    }

    private static List<Dependency> getDependenciesFromMaven(String urlStr) {
        List<Dependency> dependencies = new ArrayList<>();
        try {
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // 解析 JSON 响应以获取所有 dependencies
                dependencies = parseResponseForAllDependencies(response.toString());
            } else {
                logger.warn("GET request not worked");
            }
        } catch (Exception e) {
            logger.error("get dependencies from maven failed!");
        }
        return dependencies;
    }

    private static List<Dependency> parseResponseForAllDependencies(String jsonResponse) {
        List<Dependency> dependencies = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONObject response = jsonObject.getJSONObject("response");
        JSONArray docs = response.getJSONArray("docs");

        for (int i = 0; i < docs.length(); i++) {
            JSONObject doc = docs.getJSONObject(i);
            String groupId = doc.optString("g");
            String artifactId = doc.optString("a");
            String version = doc.optString("latestVersion");

            if (!groupId.isEmpty() && !artifactId.isEmpty()) {
                Dependency dependency = new Dependency();
                dependency.setGroupId(groupId);
                dependency.setArtifactId(artifactId);
                dependency.setVersion(version);
                dependencies.add(dependency);
            }
        }

        return dependencies;
    }
}

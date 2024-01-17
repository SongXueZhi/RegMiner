package org.regminer.common.tool;

import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.idea.IdeaProject;

import java.io.File;

/**
 * @author Jerry Zhang <zhangjian16@fudan.edu.cn>
 * @desc
 * @date 2024/1/12 10:34
 */

public class GradleManager {
    public IdeaProject getGradleModel(File projectDir) throws Exception {
        try (ProjectConnection connection = GradleConnector.newConnector()
                .forProjectDirectory(projectDir)
                .connect()) {
            // 在这里执行操作，例如获取项目信息
            return connection.getModel(IdeaProject.class);
        } catch (Exception e) {
            return null;
        }
    }
}

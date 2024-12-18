package org.regminer.miner;

import org.apache.commons.io.FileUtils;
import org.regminer.commons.constant.Configurations;
import org.regminer.commons.model.ProjectEntity;
import org.regminer.commons.sql.ProjectDao;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @Author: sxz
 * @Date: 2022/06/17/19:33
 * @Description:
 */
public class ProjectManager {
    ProjectDao projectDao = new ProjectDao();

    public ProjectEntity addProject(String projectFullName) throws IOException {
        String[] items = projectFullName.split("/");
        ProjectEntity projectEntity = projectDao.checkProjectByName(projectFullName);
        if (projectEntity == null) {
            projectEntity = new ProjectEntity();
            projectEntity.setProjectID(UUID.randomUUID().toString());
            projectEntity.setOrganization(items[0]);
            projectEntity.setProject_name(items[1]);
            projectDao.storageProject(projectEntity);
            File file = new File(Configurations.rootDir,
                    "meta_projects" + File.separator + projectEntity.getProjectID());
            if (!file.exists()) {
                file.mkdirs();
            }
            File codeMeta = new File(Configurations.metaPath);
            FileUtils.copyDirectory(codeMeta, file);
        }
        return projectEntity;
    }
}

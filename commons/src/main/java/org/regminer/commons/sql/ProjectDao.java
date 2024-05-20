package org.regminer.commons.sql;

import org.regminer.commons.constant.Configurations;
import org.regminer.commons.model.ProjectEntity;

/**
 * @Author: sxz
 * @Date: 2022/06/17/19:39
 * @Description:
 */
public class ProjectDao {

    public ProjectEntity checkProjectByName(String name) {
        String organizeName = name.split("/")[0];
        String sql = "select project_uuid,organization,project_name from project where project_name" +
                " ='" +
                Configurations.projectName + "' " + "and organization ='" +
                organizeName + "'";
        return MysqlManager.getProject(sql);
    }

    public void storageProject(ProjectEntity projectEntity) {
        String organization = projectEntity.getOrganization();
        String projectName = projectEntity.getProject_name();
        //INSERT IGNORE INTO regressions_with_gap (project_name,bug_id,bfc,buggy,bic,work,testcase) VALUES
        String sql = "insert IGNORE into project (project_uuid,organization,project_name,url) " +
                "VALUES ('" + projectEntity.getProjectID() + "','" + organization +
                "','" + projectName + "','" + "https://github.com/" + organization +
                "/" + projectName + ".git')";
        MysqlManager.executeUpdate(sql);
    }
}

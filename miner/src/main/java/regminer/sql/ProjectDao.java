package regminer.sql;

import regminer.model.ProjectEntity;
import regminer.start.ConfigLoader;

/**
 * @Author: sxz
 * @Date: 2022/06/17/19:39
 * @Description:
 */
public class ProjectDao {

    public ProjectEntity checkProjectByName(String name) {
        String organizeName = name.split("/")[0];
        String sql = new StringBuilder("select project_uuid,organization,project_name from project where project_name ='")
                .append(ConfigLoader.projectName).append("' ").append("and organization ='")
                .append(organizeName).append("'").toString();
        return MysqlManager.getProject(sql);
    }
    public void storageProject(ProjectEntity projectEntity){
        String organization = projectEntity.getOrganization();
        String projectName = projectEntity.getProject_name();
        //INSERT IGNORE INTO regressions_with_gap (project_name,bug_id,bfc,buggy,bic,work,testcase) VALUES
        String sql = new StringBuilder("insert IGNORE into project (project_uuid,organization,project_name,url) ")
                .append("VALUES ('").append(projectEntity.getProjectID()).append("','").append(organization)
                .append("','").append(projectName).append("','").append("https://github.com/").append(organization)
                .append("/").append(projectName).append(".git')").toString();
        MysqlManager.executeUpdate(sql);
    }
}

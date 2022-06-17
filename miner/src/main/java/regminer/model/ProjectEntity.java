package regminer.model;

/**
 * @Author: sxz
 * @Date: 2022/06/17/19:40
 * @Description:
 */
public class ProjectEntity {
    private String projectID;
    private String organization;
    private String project_name;

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }
}

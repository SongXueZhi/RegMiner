package regminer.miner;

import org.apache.commons.io.FileUtils;
import regminer.constant.Conf;
import regminer.model.ProjectEntity;
import regminer.sql.ProjectDao;

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
        ProjectEntity projectEntity =projectDao.checkProjectByName(projectFullName);
        if (projectEntity  == null) {
         projectEntity = new ProjectEntity();
         projectEntity.setProjectID(UUID.randomUUID().toString());
         projectEntity.setOrganization(items[0]);
         projectEntity.setProject_name(items[1]);
         projectDao.storageProject(projectEntity);
         File file  = new File(Conf.ROOT_DIR,
                 new StringBuilder("meta_projects").append(File.separator).append(projectEntity.getProjectID()).toString());
         if (!file.exists()){
             file.mkdirs();
         }
         File codeMeta = new File(Conf.META_PATH);
            FileUtils.copyDirectory(codeMeta, file);
        }
        return projectEntity;
    }
}

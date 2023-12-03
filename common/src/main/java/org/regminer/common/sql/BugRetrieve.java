package org.regminer.common.sql;

import org.regminer.common.constant.ConfigLoader;

import java.util.Set;

public class BugRetrieve {

    public Set<String> getRegressionsFromDB() {
        String sql = "select bfc from regression where project_full_name ='" + ConfigLoader.projectName + "'";
        Set<String> result = MysqlManager.executeSql(sql);
        return result;
    }
}

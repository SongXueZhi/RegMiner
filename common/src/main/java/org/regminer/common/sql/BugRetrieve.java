package org.regminer.common.sql;

import org.regminer.common.constant.Configurations;

import java.util.Set;

public class BugRetrieve {

    public Set<String> getRegressionsFromDB() {
        String sql = "select bfc from regression where project_full_name ='" + Configurations.projectName + "'";
        Set<String> result = MysqlManager.executeSql(sql);
        return result;
    }

    public Set<String> getBFCsFromDB() {
        String sql = "select bfc from bfcs where project_name ='" + Configurations.projectName + "'";
        Set<String> result = MysqlManager.executeSql(sql);
        return result;
    }
}

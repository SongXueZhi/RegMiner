package org.regminer.common.sql;

import org.regminer.miner.start.ConfigLoader;

import java.util.Set;

public class BugRetrieve {

    public Set<String> getRegressionsFromDB() {
        String sql = "select bfc from regression where project_full_name ='" + ConfigLoader.projectFullName + "'";
        Set<String> result = MysqlManager.executeSql(sql);
        return  result;
    }
}

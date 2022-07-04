package regminer.sql;

import regminer.constant.Conf;
import regminer.start.ConfigLoader;

import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

public class BugRetrieve {

    public Set<String> getRegressionsFromDB() {
        String sql = "select bfc from regression where project_full_name ='" + ConfigLoader.projectFullName + "'";
        Set<String> result = MysqlManager.executeSql(sql);
        return  result;
    }
}

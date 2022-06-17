package regminer.sql;

import regminer.constant.Conf;

import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

public class BugRetrieve {

    public Set<String> getRegressionsFromDB() {
        String sql = "select bfc from regressions where project_name ='" + Conf.PROJRCT_NAME + "'";
        Set<String> result = MysqlManager.executeSql(sql);
        return  result;
    }
}

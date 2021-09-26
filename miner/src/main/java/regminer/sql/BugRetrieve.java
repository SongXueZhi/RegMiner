package regminer.sql;

import regminer.constant.Conf;

import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

public class BugRetrieve {

    public Set<String> getRegressionsFromDB() {
        String sql = "select bfc from regressions where project_name ='" + Conf.PROJRCT_NAME + "'";
        Set<String> result = MysqlManager.executeSql(sql);
        result.addAll(getRegressionsWithGapFromDB());
        return  result;
    }
    public Set<String> getRegressionsWithGapFromDB() {
        String sql = "select bfc from regressions_with_gap where project_name ='" + Conf.PROJRCT_NAME + "'";
        return MysqlManager.executeSql(sql);
    }
}

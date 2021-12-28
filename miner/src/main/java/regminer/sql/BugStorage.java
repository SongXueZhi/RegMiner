/*
 *
 *  * Copyright 2021 SongXueZhi
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package regminer.sql;

import regminer.constant.Conf;
import regminer.model.Regression;
import regminer.model.RegressionWithGap;

public class BugStorage {
    public  void saveBug(Regression regression) {
        if (regression instanceof RegressionWithGap){
            saveRegressionWithGap((RegressionWithGap) regression);
            return;
        }
        String sql = "INSERT IGNORE INTO regressions (project_name,bug_id,bfc,buggy,bic,work,testcase) VALUES "+
                "('"+ Conf.PROJRCT_NAME+"','"+regression.getBugId()+"','"+regression.getBfcId()+"','"+regression.getBuggyId()+"','"+regression.getBicId()+"','"+regression.getWorkId()+"','"+regression.getTestCase()+"')";
        MysqlManager.executeUpdate(sql);
    }
    public void  saveRegressionWithGap(RegressionWithGap regression){
        String sql = "INSERT IGNORE INTO regressions_with_gap (project_name,bug_id,bfc,buggy,bic,work,testcase) VALUES "+
                "('"+ Conf.PROJRCT_NAME+"','"+regression.getBugId()+"','"+regression.getBfcId()+"','"+regression.getBuggyId()+"','"+regression.getBicId()+"','"+regression.getWorkId()+"','"+regression.getTestCase()+"')";
        MysqlManager.executeUpdate(sql);
    }
}

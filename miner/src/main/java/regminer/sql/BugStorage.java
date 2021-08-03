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

import regminer.model.Regression;

import java.sql.Statement;

public class BugStorage {
    public  void saveBug(Regression regression) {
        String sql = "INSERT IGNORE INTO regressions (bug_id,bfc,buggy,bic,work,bfc_path,buggy_path,bic_path,work_path,testcase) VALUES "+
                "('"+regression.getBugId()+"','"+regression.getBfcId()+"',+'"+regression.getBuggyId()+"','"+regression.getBicId()+"',+'"+regression.getWorkId()+"','"+regression.getBfcDirPath()+"',+'"+regression.getBuggyDirPath()+"'," +
                "'"+regression.getBicDirPath()+"','"+regression.getWorkDirPath()+"','"+regression.getTestCase()+"')";
        MysqlManager.executeSql(sql);
    }
}

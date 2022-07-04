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

import regminer.model.ProjectEntity;
import regminer.model.Regression;

public class BugStorage {
    public  void saveBug(Regression regression) {
        ProjectEntity projectEntity = regression.getProjectEntity();
        String sql = "INSERT IGNORE INTO regression (regression_uuid,project_uuid,project_full_name," +
                "bug_id,bfc,buggy,bic," +
                "work,testcase," +
                "with_gap) VALUES "+
                "('"+regression.getRegressionUUID()+"','"+projectEntity.getProjectID()+"','"
                +projectEntity.getOrganization()+"/"+projectEntity.getProject_name()+"','"
                +regression.getBugId()+"','"+regression.getBfcId()+"','"
                +regression.getBuggyId()+"','"+regression.getBicId()+"','"+regression.getWorkId()+"','"
                +regression.getTestCase()+"','"+regression.getWithGap()+"')";
        MysqlManager.executeUpdate(sql);
    }
}

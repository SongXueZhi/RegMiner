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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

public class MysqlManager {

    public static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    public static  String URL;
    public static  String NAME;
    public static  String PWD;
    private static Connection conn = null;
    private static Statement statement = null;

    private static void getConn() throws Exception {
        if (conn != null) {
            return;
        }
        Class.forName(DRIVER);
        conn = DriverManager.getConnection(URL, NAME, PWD);

    }

    public static void getStatement() throws Exception {
        if (conn == null) {
            getConn();
        }
        if (statement != null) {
            return;
        }
        statement = conn.createStatement();
    }

    public static void closed() {
        try {
            if (statement != null) {
                statement.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {

        } finally {
            try {
                if (statement != null) {
                    statement.close();
                    statement = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (Exception e) {
            }
        }
    }

    public static void executeUpdate(String sql) {
        try {
            getStatement();
            statement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            closed();
        }
    }
    public static ProjectEntity getProject(String sql){
        ProjectEntity projectEntity = null;
        try {
            getStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()){
                projectEntity = new ProjectEntity();
                projectEntity.setProjectID(rs.getString("project_uuid"));
                projectEntity.setOrganization(rs.getString("organization"));
                projectEntity.setProject_name(rs.getString("project_name"));
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  projectEntity;
    }

    public static Set<String>  executeSql(String sql) {
        Set<String> result = new HashSet<>();
        try {
            getStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()){
                result.add(rs.getString("bfc"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            closed();
        }
        return result;
    }
}

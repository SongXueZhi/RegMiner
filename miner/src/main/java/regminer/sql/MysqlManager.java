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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class MysqlManager {

    public static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String URL = "jdbc:mysql://10.177.21.183:3306/regression?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF8";
    public static final String NAME = "root";
    public static final String PWD = "123456";
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

    public static void executeSql(String sql) {
        try {
            getStatement();
            statement.executeQuery(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            closed();
        }
    }
}

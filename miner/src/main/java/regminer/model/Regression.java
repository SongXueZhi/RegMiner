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

package regminer.model;

public class Regression {
    String bugId;
    String bfcId;
    String buggyId;
    String bicId;
    String workId;
    String bfcDirPath;
    String buggyDirPath;
    String bicDirPath;
    String workDirPath;
    String testCase;

    public String getBugId() {
        return bugId;
    }

    public void setBugId(String bugId) {
        this.bugId = bugId;
    }

    public String getBfcId() {
        return bfcId;
    }

    public void setBfcId(String bfcId) {
        this.bfcId = bfcId;
    }

    public String getBuggyId() {
        return buggyId;
    }

    public void setBuggyId(String buggyId) {
        this.buggyId = buggyId;
    }

    public String getBicId() {
        return bicId;
    }

    public void setBicId(String bicId) {
        this.bicId = bicId;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public String getBfcDirPath() {
        return bfcDirPath;
    }

    public void setBfcDirPath(String bfcDirPath) {
        this.bfcDirPath = bfcDirPath;
    }

    public String getBuggyDirPath() {
        return buggyDirPath;
    }

    public void setBuggyDirPath(String buggyDirPath) {
        this.buggyDirPath = buggyDirPath;
    }

    public String getBicDirPath() {
        return bicDirPath;
    }

    public void setBicDirPath(String bicDirPath) {
        this.bicDirPath = bicDirPath;
    }

    public String getWorkDirPath() {
        return workDirPath;
    }

    public void setWorkDirPath(String workDirPath) {
        this.workDirPath = workDirPath;
    }

    public String getTestCase() {
        return testCase;
    }

    public void setTestCase(String testCase) {
        this.testCase = testCase;
    }

    /**
     *
     * @param bugId
     * @param bfcId
     * @param buggyId
     * @param bicId
     * @param workId
     * @param bfcDirPath
     * @param buggyDirPath
     * @param bicDirPath
     * @param workDirPath
     * @param testCase
     */
    public Regression(String bugId, String bfcId, String buggyId, String bicId, String workId, String bfcDirPath, String buggyDirPath, String bicDirPath, String workDirPath, String testCase) {
        this.bugId = bugId;
        this.bfcId = bfcId;
        this.buggyId = buggyId;
        this.bicId = bicId;
        this.workId = workId;
        this.bfcDirPath = bfcDirPath;
        this.buggyDirPath = buggyDirPath;
        this.bicDirPath = bicDirPath;
        this.workDirPath = workDirPath;
        this.testCase = testCase;
    }
}

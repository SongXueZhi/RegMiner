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

package org.regminer.common.utils;

import org.apache.commons.io.FileUtils;
import org.regminer.common.exec.Executor;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GitTracker {

    // Now we track method change history just in the time that after of bfcdetect and before TestcaseDetermine
    public boolean addJavaAttibuteToGit(File bfcdir) {
        //add .gitattributes file to bfc
        File gitConfigFile = new File(bfcdir, ".gitattributes");
        try {
            if(gitConfigFile.exists()){
                gitConfigFile.deleteOnExit();
            }
            gitConfigFile.createNewFile();
            FileUtils.writeStringToFile(gitConfigFile,"*.java\tdiff=java\n");
        } catch (IOException e) {
            return false;
        }
        return true;
    }


    /**
     *  Don't use this feature in Search process
     * @param Method
     * @param file_path
     * @param bfcDir
     * @return
     */
    public int trackFunctionByGitBlogL(String Method, String file_path, File bfcDir) {
        String[] commitHistoryList =
                new Executor(OSUtils.getOSType()).exec("git log -L:" + Method + ":" + file_path +
                " --pretty" +
                "=format:%h -s").getMessage().split("/n");
        return new HashSet<>(List.of(commitHistoryList)).size();
    }
}

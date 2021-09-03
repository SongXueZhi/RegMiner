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

package regminer.miner;

import org.apache.commons.io.FileUtils;
import regminer.exec.TestExecutor;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class GitTracker {
    TestExecutor testExecutor = new TestExecutor();
    // Now we track method change history just in the time that after of bfcdetect and before TestcaseDetermine
    public boolean addJavaAttibuteToGit(File bfcdir) {
        //add .gitattributes file to bfc
        File gitConfigFile = new File(bfcdir, ".gitattributes");
        try {
            if (gitConfigFile.exists()) {
                gitConfigFile.deleteOnExit();
            }
            gitConfigFile.createNewFile();
            FileUtils.writeStringToFile(gitConfigFile, "*.java\tdiff=java\n");
        } catch (IOException e) {
            return false;
        }
        return true;
    }


    /**
     * Don't use this feature in Search process
     *
     * @param Method
     * @param file_path
     * @param bfcDir
     * @return
     */
    public int trackFunctionByGitBlogL(String Method, String file_path, File bfcDir) {
        testExecutor.setDirectory(bfcDir);
        Set<String> commitHistoryList = testExecutor.execWithSetResult("git log -L:" + Method + ":" + file_path + " --pretty=format:%h -s");
        return commitHistoryList.size();
    }


    public int trackCodeBlockByLogl(int start, int end, String file_path, File bfcDir, boolean isMethod) {
        testExecutor.setDirectory(bfcDir);
        String commitHistoryContent = testExecutor.exec("git log -L " + start + "," + end + ":" + file_path);
        String[] ss = commitHistoryContent.split("commit ");
        List<String> commitList = new LinkedList<>();
        for (String s :ss){
            if(s.equals("\n")){
                continue;
            }
            commitList.add(s);
        }

        Iterator<String> it = commitList.iterator();
        while (it.hasNext()) {
            String content = it.next();
            String[] lines = content.split("@@");
            if (lines.length<2){
                it.remove();
                continue;
            }
            lines =lines[2].split("\n");
            int sum =0;
            for (int i =0;i<lines.length;i++){
                if (lines[i].equals("")){
                    continue;
                }
                sum++;
            }
            if ((end + 1 - start) * 4 <sum ) {
                it.remove();
                continue;
            }
            if (isMethod && (end + 1 - start)*4<sum){
                it.remove();
            }
        }
        int freq = commitList.size();
        if (freq == 0) {
            freq = freq + 1;
        }
        return freq;
    }
    public int trackhunkByLogl(int start, int end, String file_path, File bfcDir) {
        testExecutor.setDirectory(bfcDir);
        Set<String> commitHistoryList = testExecutor.execWithSetResult("git log -L " + start + "," + end + ":" + file_path+ " --pretty=format:%h -s");
        return commitHistoryList.size();
    }
}

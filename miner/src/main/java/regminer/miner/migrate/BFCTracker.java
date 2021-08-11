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

package regminer.miner.migrate;

import regminer.coverage.model.CoverNode;
import regminer.git.GitTracker;
import regminer.utils.CodeUtil;


import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BFCTracker {
    private final static double PROB_UNIT = 0.005;
    private double notRegProb = 1 - PROB_UNIT;
    GitTracker gitTracker = new GitTracker();

    public HashMap<String, Integer> handleTasks(List<CoverNode> coverNodes, File bfcDir) {
        HashMap<String, Integer> methodFrequencyMap = new HashMap<>();
        List<String> javaFiles = Arrays.asList(CodeUtil.getJavaFiles(bfcDir));
        gitTracker.addJavaAttibuteToGit(bfcDir);
        for (CoverNode coverNode : coverNodes) {
            String methodName = coverNode.getCoverMethod().getName();
            String packageAndClassPath = coverNode.getCoverPackage().getName() + File.separator + coverNode.getCoverClass().getFileName();
            String classPath = getJavaFilePath(javaFiles, packageAndClassPath);
            int frequency = 0;
            if (classPath != null) {
                frequency = trackBFC(bfcDir, classPath, methodName);
            }
            methodFrequencyMap.put(packageAndClassPath + "#" + methodName, frequency);
        }
        return methodFrequencyMap;
    }

    public double regressionProbCalculate(HashMap<String, Integer> methodFrequencyMap) {
        int sum = 0;
        for (Map.Entry<String, Integer> entry : methodFrequencyMap.entrySet()) {
            int frequency = entry.getValue();
            if(frequency > 0 ){
                frequency =frequency-1;
            }
            sum += frequency;
        }
        double methodNotRegressionProb = Math.pow(notRegProb, sum);
        return 1 - methodNotRegressionProb;
    }

    private int trackBFC(File bfcDir, String classFilePath, String methodName) {
        try {
            return gitTracker.trackFunctionByGitBlogL(methodName, classFilePath, bfcDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private String getJavaFilePath(List<String> javaFiles, String packageAndClassPath) {
        for (String filePath : javaFiles) {
            if (filePath.contains(packageAndClassPath)) {
                return filePath;
            }
        }
        return null;
    }
}

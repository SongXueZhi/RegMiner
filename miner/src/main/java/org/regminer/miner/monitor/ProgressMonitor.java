package org.regminer.miner.monitor;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.regminer.common.constant.Configurations;
import org.regminer.common.constant.Constant;
import org.regminer.common.model.PotentialBFC;
import org.regminer.common.sql.BugRetrieve;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author sxz
 * 加入断点
 */
public class ProgressMonitor {

    private static MinerStateManager minerStateManager;

    static {
        minerStateManager = new MinerStateManager("state");
    }

    public static void updateState(String lastProcessedCommitId) {
        minerStateManager.updateState(lastProcessedCommitId);
    }
    public static void retrieveDone(Set<String> doneTaskList ){
        if (Configurations.taskName.equals(Constant.BFC_TASK)) {
            doneTaskList.addAll(new BugRetrieve().getBFCsFromDB());
        } else if (Configurations.taskName.equals(Constant.BIC_TASK)) {
            Set<String> regressionsInSql = new BugRetrieve().getRegressionsFromDB();
            doneTaskList.addAll(regressionsInSql);
        }
    }

    public static void readStateAndSkip(Set<String> doneTaskList ) {
        Path path = Paths.get("skip.in");
        //check whether miner need skip some commits
        if (Files.exists(path)) {
            try {
                doneTaskList.addAll(Files.readAllLines(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
            minerStateManager.readState();
    }

    public static void rePlan(List<PotentialBFC> pRFCs) {
        Set<String> doneTaskList = new HashSet<>();
        readStateAndSkip(doneTaskList);
        retrieveDone(doneTaskList);

        Iterator<PotentialBFC> iterator = pRFCs.iterator();
        String state = minerStateManager.readState();
        boolean isSkip = !StringUtils.isEmpty(state);

        while (iterator.hasNext()) {
            PotentialBFC pRfc = iterator.next();
            if(pRfc.getCommit().getName().equals(state)){
                isSkip = false;
            }
            if (isSkip || doneTaskList.contains(pRfc.getCommit().getName())) {
                iterator.remove();
            }
        }
    }

    @SuppressWarnings("deprecation")
    public static void addDone(String name) {
        try {
            FileUtils.writeStringToFile(new File(Configurations.projectPath + File.separator + "progress.details"),
                    name + "\n",
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

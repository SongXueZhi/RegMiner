package org.regminer.miner.monitor;

import org.apache.commons.lang3.StringUtils;
import org.regminer.commons.constant.Configurations;
import org.regminer.commons.constant.Constant;
import org.regminer.commons.model.PotentialBFC;
import org.regminer.commons.sql.BugRetrieve;

import java.io.IOException;
import java.nio.file.Files;
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
    private static final MinerStateManager minerStateManager;

    static {
        minerStateManager = new MinerStateManager("state");
    }

    public static void updateState(String lastProcessedCommitId) {
        minerStateManager.updateState(lastProcessedCommitId);
    }

    public static void retrieveDone(Set<String> doneTaskList) {
        if (Configurations.taskName.equals(Constant.BFC_TASK)) {
            doneTaskList.addAll(new BugRetrieve().getBFCsFromDB());
        } else {
            Set<String> regressionsInSql = new BugRetrieve().getRegressionsFromDB();
            doneTaskList.addAll(regressionsInSql);
        }
    }

    public static void readStateAndSkip(Set<String> doneTaskList) {
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
        try {
            if (!Configurations.open_monitor) {
                return;
            }
            Set<String> doneTaskList = new HashSet<>();
            readStateAndSkip(doneTaskList);
            retrieveDone(doneTaskList);

            Iterator<PotentialBFC> iterator = pRFCs.iterator();
            String state = minerStateManager.readState();
            boolean isSkip = !StringUtils.isEmpty(state);

            while (iterator.hasNext()) {
                PotentialBFC pRfc = iterator.next();
                if (pRfc.getCommit().getName().equals(state)) {
                    isSkip = false;
                }
                if (isSkip || doneTaskList.contains(pRfc.getCommit().getName())) {
                    iterator.remove();
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}

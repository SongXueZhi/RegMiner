package org.regminer.miner.monitor;

import org.apache.commons.io.FileUtils;
import org.regminer.common.constant.Configurations;
import org.regminer.miner.model.PotentialBFC;
import org.regminer.miner.sql.BugRetrieve;
import org.regminer.common.utils.FileUtilx;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author sxz
 * 加入断点
 */
public class ProgressMonitor {

    public static Set<String> doneTaskList;

    public static void load() {
        doneTaskList = FileUtilx.readSetFromFile(Configurations.PROJECT_PATH + File.separator + "progress.details");
        Set<String> regressionsInSql = new BugRetrieve().getRegressionsFromDB();
        doneTaskList.addAll(regressionsInSql);

    }

    public static void rePlan(List<PotentialBFC> pRFCs) {
        FileUtilx.log("Completed: " + doneTaskList.size());
        Iterator<PotentialBFC> iterator = pRFCs.iterator();
        while (iterator.hasNext()) {
            PotentialBFC pRfc = iterator.next();
            if (doneTaskList.contains(pRfc.getCommit().getName())) {
                iterator.remove();
            }
        }
        FileUtilx.log("The remaining: " + pRFCs.size());
    }

    @SuppressWarnings("deprecation")
    public static void addDone(String name) {
        try {
            FileUtils.writeStringToFile(new File(Configurations.PROJECT_PATH + File.separator + "progress.details"), name + "\n",
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

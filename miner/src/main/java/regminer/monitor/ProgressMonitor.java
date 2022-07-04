package regminer.monitor;

import org.apache.commons.io.FileUtils;
import regminer.constant.Conf;
import regminer.model.PotentialRFC;
import regminer.sql.BugRetrieve;
import regminer.start.ConfigLoader;
import regminer.utils.FileUtilx;

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
        doneTaskList = FileUtilx.readSetFromFile(Conf.PROJECT_PATH + File.separator + "progress.details");
        if (Conf.sql_enable) {
            Set<String> regressionsInSql = new BugRetrieve().getRegressionsFromDB();
            doneTaskList.addAll(regressionsInSql);
        }
    }

    public static void rePlan(List<PotentialRFC> pRFCs) {
        FileUtilx.log("Completed: " + doneTaskList.size());
        Iterator<PotentialRFC> iterator = pRFCs.iterator();
        while (iterator.hasNext()) {
            PotentialRFC pRfc = iterator.next();
            if (doneTaskList.contains(pRfc.getCommit().getName())) {
                iterator.remove();
            }
        }
        FileUtilx.log("The remaining: " + pRFCs.size());
    }

    @SuppressWarnings("deprecation")
    public static void addDone(String name) {
        try {
            FileUtils.writeStringToFile(new File(Conf.PROJECT_PATH + File.separator + "progress.details"), name + "\n",
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

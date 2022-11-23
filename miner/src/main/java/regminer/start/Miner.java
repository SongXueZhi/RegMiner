package regminer.start;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import regminer.constant.Conf;
import regminer.git.provider.Provider;
import regminer.miner.PotentialBFCDetector;
import regminer.miner.ProjectManager;
import regminer.miner.RelatedTestCaseParser;
import regminer.miner.migrate.BFCEvaluator;
import regminer.miner.migrate.BICFinder;
import regminer.model.PotentialRFC;
import regminer.model.ProjectEntity;
import regminer.model.Regression;
import regminer.monitor.ProgressMonitor;
import regminer.sql.BugStorage;
import regminer.utils.FileUtilx;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author sxz
 * 方法入口
 */
public class Miner {

    public static List<PotentialRFC> pRFCs;
    public static Set<String> setResult = new HashSet<>();
    public static String taskType = "regression";
    static BugStorage bugStorage = new BugStorage();
    static ProjectManager projectManager = new ProjectManager();
    static RelatedTestCaseParser rTCParser = new RelatedTestCaseParser();
    static BICFinder finder = new BICFinder();

    public static void start() throws Exception {
        ConfigLoader.refresh();//加载配置
        if (ConfigLoader.organizeName.equals("")) {
            FileUtilx.log("Incorrectly formatted project name, please set project name in " +
                    "{organization}/{project_name}");
            return;
        }
//        ProgressMonitor.load(); // 加载断点
        System.out.println(Conf.LOCAL_PROJECT_GIT);
        long s1 = System.currentTimeMillis();
        try (Repository repo = new Provider().create(Provider.EXISITING).get(Conf.LOCAL_PROJECT_GIT);
             Git git = new Git(repo)) {
            // 检测满足条件的BFC
            PotentialBFCDetector pBFCDetector = new PotentialBFCDetector(repo, git);
            pRFCs = Collections.synchronizedList(pBFCDetector.detectPotentialBFC());
            ProgressMonitor.rePlan(pRFCs);
            ProjectEntity projectEntity = projectManager.addProject(ConfigLoader.projectFullName);
            BFCEvaluator tm = new BFCEvaluator(repo);
            float j = (float) pRFCs.size();
            System.out.println("origin bfc number " + j);
            if (Miner.taskType.equals("bug")){
                bugTask(projectEntity,tm);
            }else{
                regressionTask(projectEntity,tm);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        long s2 = System.currentTimeMillis();
        System.out.println(s2 - s1);
    }

    public static void bugTask(ProjectEntity projectEntity, BFCEvaluator tm) throws Exception {
        ConcurrentLinkedQueue<PotentialRFC> linkedQueue = new ConcurrentLinkedQueue<>();
        Thread thread1 = new Thread(() -> tm.evoluteBFCList(pRFCs, linkedQueue));
        thread1.setName("bfc");
        thread1.start();
        thread1.join(); //comment this line and new thread to do prodd task
        //TODO @ liu shuning
    }

    public static void regressionTask(ProjectEntity projectEntity, BFCEvaluator tm) throws Exception {

        ConcurrentLinkedQueue<PotentialRFC> linkedQueue = new ConcurrentLinkedQueue<>();

        Thread thread1 = new Thread(() -> tm.evoluteBFCList(pRFCs, linkedQueue));
        thread1.setName("bfc");
        thread1.start();
        Thread thread2 = new Thread(() -> {
            while (pRFCs.size() > 0 || linkedQueue.size() > 0) {
                if (linkedQueue.size() > 0) {
                    PotentialRFC pRfc = linkedQueue.poll();
                    FileUtilx.log("queue size:" + linkedQueue.size());
                    Regression regression = finder.searchBIC(pRfc);
                    if (regression == null) {
                        ProgressMonitor.addDone(pRfc.getCommit().getName());
                        continue;
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append(regression.getBugId()).append(",").append(regression.getBfcId())
                            .append(",").append(regression.getBuggyId())
                            .append(",").append(regression.getBicId())
                            .append(",").append(regression.getWorkId())
                            .append(",").append(regression.getTestCase())
                            .append(",").append(regression.getWithGap());
                    String regressionLog = sb.toString();
                    if (!setResult.contains(regressionLog)) {
                        FileUtilx.apendResult(regressionLog);
                    }
                    setResult.add(regressionLog);
                    if (Conf.sql_enable) {
                        regression.setProjectEntity(projectEntity);
                        bugStorage.saveBug(regression);
                    }
                    ProgressMonitor.addDone(pRfc.getCommit().getName());
                }
            }
        });
        thread2.setName("rfc");
        thread2.start();
        thread2.join();
    }
}

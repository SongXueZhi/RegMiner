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

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author sxz
 * 方法入口
 */
public class Miner {
    public static Repository repo = null;
    public static Git git = null;
    public static List<PotentialRFC> pRFCs;
    public static Set<String> setResult = new HashSet<>();
    static BugStorage bugStorage = new BugStorage();

    public static void main(String[] args) throws Exception {
        ConfigLoader.refresh();//加载配置
        ProgressMonitor.load(); // 加载断点
        System.out.println(Conf.LOCAL_PROJECT_GIT);
        repo = new Provider().create(Provider.EXISITING).get(Conf.LOCAL_PROJECT_GIT);
        git = new Git(repo);
        try {
            // 检测满足条件的BFC
            PotentialBFCDetector pBFCDetector = new PotentialBFCDetector(repo, git);
            pRFCs = Collections.synchronizedList(pBFCDetector.detectPotentialBFC());
            ProgressMonitor.rePlan(pRFCs);
            // 开始每一个bfc所对应的bic，查找任务。
            singleThreadHandle(); // 单线程处理模式
            //mutilThreadHandle();// 多线程模式
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void singleThreadHandle() throws Exception {
        ProjectManager projectManager = new ProjectManager();
        if (ConfigLoader.organizeName.equals("")){
            FileUtilx.log("Incorrectly formatted project name, please set project name in {organization}/{project_name}");
            return;
        }
       ProjectEntity projectEntity = projectManager.addProject(ConfigLoader.projectFullName);

        long s1 = System.currentTimeMillis();

        // 工具类准备,1)测试方法查找 2)测试用例确定 3)BIC查找
        RelatedTestCaseParser rTCParser = new RelatedTestCaseParser();
        BFCEvaluator tm = new BFCEvaluator(repo);
        BICFinder finder = new BICFinder();
        // 声明一些辅助变量
        float i = 0;
        float j = (float) pRFCs.size();
        System.out.println("origin bfc number " + j);

        ConcurrentLinkedQueue<PotentialRFC> linkedQueue = new ConcurrentLinkedQueue<>();

       Thread thread1 =  new Thread(() -> tm.evoluteBFCList(pRFCs, linkedQueue));
       thread1.setName("bfc");
       thread1.start();

        Thread thread2 = new Thread(() -> {
            while (pRFCs.size() > 0 || linkedQueue.size() > 0) {
                if (linkedQueue.size() > 0) {
                    PotentialRFC pRfc = linkedQueue.poll();
                    FileUtilx.log("queue size:"+linkedQueue.size());
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
        long s2 = System.currentTimeMillis();
        System.out.println(s2 - s1);
    }
}

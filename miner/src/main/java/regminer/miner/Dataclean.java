package regminer.miner;


import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import regminer.constant.Conf;
import regminer.exec.TestExecutor;
import regminer.finalize.SycFileCleanup;
import regminer.git.provider.Provider;
import regminer.miner.PotentialBFCDetector;
import regminer.miner.migrate.Migrator;
import regminer.model.Methodx;
import regminer.model.NormalFile;
import regminer.model.PotentialRFC;
import regminer.start.ConfigLoader;
import regminer.utils.FileUtilx;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Dataclean  extends Migrator {

    static Repository repo;
    static Git git;

    public static void main(String[] args) throws Exception {
        TestExecutor executor = new TestExecutor();
        Dataclean dc = new Dataclean();
        dc.prepare();
//        //get All rfc in fastjson
//        File file = new File("resource/fastjson_rfc");
//        String sql = "select * from regressions where project_name = 'fastjson'";
//        Set<String> rfcList = MysqlManager.executeSql(sql);

        File file = new File("resource/logmain");
        File log = new File("resource/fastjson_bfc");
        List<String> filter = new ArrayList<>();
        String content = FileUtilx.readContentFromFile(file);
        String[] source = content.split("%");

        for (int i = 1; i < source.length - 1; i++) {
            if (source[i].contains("search fal") && !source[i].contains("pass")) {
                String originbfc = source[i].split(" Start")[0].trim().replace(" ", "").replace("\n", "");
                filter.add(originbfc);
            }
        }
        GitTracker gitTracker = new GitTracker();
        BFCFixPatchParser parser = new BFCFixPatchParser();
        PotentialBFCDetector potentialRFC = new PotentialBFCDetector(repo, git);
        List<PotentialRFC> potentialRFCS = potentialRFC.detectPotentialBFC(filter);
        double i = 0.0;
        for (PotentialRFC pRFC : potentialRFCS) {
            String name = pRFC.getCommit().getName();
            File bfcDir = dc.checkout(name,name,"bfc");
            pRFC.fileMap.put(name,bfcDir);
            gitTracker.addJavaAttibuteToGit(bfcDir);
            parser.equipFixPatchToRFC(pRFC);
            boolean f =false;
            int a=0;
            for (NormalFile normalFile : pRFC.getNormalJavaFiles()) {
                for (Methodx methodx : normalFile.editMethodxes) {
                     a = gitTracker.trackhunkByLogl(methodx.getStopLine(),methodx.getStartLine(),normalFile.getNewPath(),bfcDir);
                    if (a > 7){
                        f =true;
                        break;
                    }
                }
                if (a>7){
                    f =true;
                    break;
                }
            }
            if (!f){
                FileUtilx.apendResultToFile(name,log);
            }
            System.out.println((++i)/potentialRFCS.size());
            executor.exec("rm -rf "+Conf.CACHE_PATH+File.separator+name);
        }

    }
    public void emptyCache(String bfcID) {
        File bfcFile = new File(Conf.CACHE_PATH + File.separator + bfcID);
        new SycFileCleanup().cleanDirectory(bfcFile);
    }
    public void prepare() throws Exception {
        ConfigLoader.refresh();
        repo = new Provider().create(Provider.EXISITING).get(Conf.LOCAL_PROJECT_GIT);
        git = new Git(repo);
    }


}

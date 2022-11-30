package regminer.miner.dd;

import regminer.miner.migrate.TestCaseMigrator;
import regminer.model.HunkEntity;
import regminer.model.TestFile;
import regminer.utils.FileUtilx;

import java.io.File;
import java.util.*;

import static java.lang.Math.abs;
import static java.lang.Math.min;

/**
 * @Author: sxz
 * @Date: 2022/11/23/21:24
 * @Description:
 */
public class DD {
    String path;
    String bfcPath;
    List<HunkEntity> hunkEntities;
    List<TestFile> testSuites;

    public DD(String path, String bfcPath, List<HunkEntity> hunkEntities, List<TestFile> testSuites) {
        this.path = path;
        this.bfcPath = bfcPath;
        this.hunkEntities = hunkEntities;
        this.testSuites = testSuites;
    }

    public List<HunkEntity> run() throws Exception {
        hunkEntities.removeIf(hunkEntity -> hunkEntity.getNewPath().contains("test"));
        hunkEntities.removeIf(hunkEntity -> hunkEntity.getOldPath().contains("test"));

        System.out.println("hunkEntities: " + hunkEntities);
        FileUtilx.log("hunkEntities: " + hunkEntities);

        int time = 0;
        String tmpPath = path.replace("meta","tmp");
        FileUtilx.copyDirToTarget(path,tmpPath);
        assert (codeReduceTest(tmpPath, bfcPath, hunkEntities) == 0);
        List<HunkEntity> retseq = hunkEntities;
        List<Integer> retIdx = new ArrayList<>();
        List<Double> p = new ArrayList<>();
        for(int i = 0; i < hunkEntities.size(); i++){
            retIdx.add(i);
            p.add(0.1);
        }
        while (!testDone(p)){
            List<Integer> delIdx = sample(p);
            if(delIdx.size() == 0){
                break;
            }
            time = time + 1;
            List<Integer> idx2test = getIdx2test(retIdx,delIdx);
            List<HunkEntity> seq2test = new ArrayList<>();
            for (int idxelm: idx2test){
                seq2test.add(hunkEntities.get(idxelm));
            }
            FileUtilx.copyDirToTarget(path,tmpPath);
            int result = codeReduceTest(tmpPath, bfcPath, seq2test);
            System.out.println("dd test " + time + ": " + result + ": " + idx2test);
            FileUtilx.log("dd test " + time + ": " + result + ": " + idx2test);

            if(result == 0){
                for(int set0 = 0; set0 < p.size(); set0++){
                    if(!idx2test.contains(set0)){
                        p.set(set0,0.0);
                    }
                }
                retseq = seq2test;
                retIdx = idx2test;
            }else {
                List<Double> pTmp = new ArrayList<>(p);
                for(int setd = 0; setd < p.size(); setd++){
                    if(delIdx.contains(setd) && (p.get(setd) != 0) && (p.get(setd) != 1)){
                        double delta = (computRatio(delIdx,pTmp) - 1) * pTmp.get(setd);
                        p.set(setd,pTmp.get(setd) + delta);
                    }
                }
            }
            System.out.println(p);
            FileUtilx.log("p: " + p);
        }
        return retseq;
    }

    public static List<String> getRelatedFile(List<HunkEntity> hunkEntities){
        List<String> filePath = new ArrayList<>();
        for (HunkEntity hunk: hunkEntities) {
            filePath.add(hunk.getNewPath());
        }
        return filePath;
    }

    public int codeReduceTest(String path, String bfcPath, List<HunkEntity> hunkEntities) throws Exception {
        BFCFix BFCFix = new BFCFix(path, bfcPath, hunkEntities);
        BFCFix.fix(path, bfcPath, hunkEntities);
        TestCaseMigrator testMigrator = new TestCaseMigrator();
        int result = testMigrator.testBFCSingleMethod(new File(path), testSuites);
        return result;
    }

    public List<Integer> getIdx2test(List<Integer> inp1, List<Integer> inp2){
        List<Integer> result = new ArrayList<>();
        for(Integer elm: inp1){
            if(!inp2.contains(elm)){
                result.add(elm);
            }
        }
        return result;
    }

    public double computRatio(List<Integer> deleteconfig, List<Double> p){
        double res = 0;
        double tmplog = 1;
        for(int delc: deleteconfig){
            if(p.get(delc) > 0 && p.get(delc) < 1){
                tmplog *= (1 - p.get(delc));
            }
        }
        res = 1 / (1 - tmplog);
        return res;
    }

    private List<Integer> sortToIndex(List<Double> p){
        List<Integer> idxlist = new ArrayList<>();
        Map<Integer,Double> pidxMap = new HashMap<>();
        for(int j = 0; j < p.size();j ++){
            pidxMap.put(j,p.get(j));
        }
        List<Map.Entry<Integer,Double>> entrys=new ArrayList<>(pidxMap.entrySet());
        entrys.sort(new Comparator<Map.Entry>() {
            public int compare(Map.Entry o1, Map.Entry o2) {
                return (int) ((double) o1.getValue()*100000 - (double) o2.getValue()*100000);
            }
        });
        for(Map.Entry<Integer,Double> entry:entrys){
            idxlist.add(entry.getKey());
        }
        return idxlist;
    }

    public List<Integer> sample(List<Double> p){
        List<Integer> delset = new ArrayList<>();
        List<Integer> idxlist = sortToIndex(p);
        int k = 0;
        double tmp = 1;
        double last = -9999;
        int i = 0;
        while (i < p.size()){
            if (p.get(idxlist.get(i)) == 0) {
                k = k + 1;
                i = i + 1;
                continue;
            }
            if(!(p.get(idxlist.get(i))<1)){
                break;
            }
            for(int j = k; j < i+1; j++){
                tmp *= (1-p.get(idxlist.get(j)));
            }
            tmp *= (i - k + 1);
            if(tmp < last){
                break;
            }
            last = tmp;
            tmp = 1;
            i = i + 1;
        }
        while (i > k){
            i = i - 1;
            delset.add(idxlist.get(i));
        }
        return delset;
    }

    public boolean testDone(List<Double> p){
        for( double prob :  p){
            if(abs(prob-1.0)>1e-6 && min(prob,1)<1.0){
                return false;
            }
        }
        return true;
    }

}

package com.fudan.annotation.platform.backend.core;

import com.fudan.annotation.platform.backend.entity.*;
import com.fudan.annotation.platform.backend.util.CodeUtil;
import com.fudan.annotation.platform.backend.util.FileUtil;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.abs;
import static java.lang.Math.min;

@Component
public class ProbDD {
    // 传参ProjectName
    static BufferedWriter bw;

    static {
        try {
            bw = new BufferedWriter(new FileWriter("detail", true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public DeltaDebugResult runDeltaDebugging(Regression regressionTest, File projectDir, List<Revision> revisionList, List<Integer> stepRange, List<Double> cProb, List<Integer> cProbLeftIdx2Test) throws IOException {
////        Regression regressionTest = regressionMapper.getRegressionInfo(regressionUuid);
////        File projectDir = sourceCodeManager.getProjectDir(regressionTest.getProjectFullName());
////        String ricDir = projectDir.toString() + regressionUuid + "_ric";
//        File ricDir = revisionList.stream().filter(revision -> revision.getRevisionName().equals("bic")).collect(Collectors.toList()).get(0).getLocalCodeDir();
//
//        // get hunks
//        List<HunkEntity> hunks = GitUtil.getHunksBetweenCommits(projectDir, regressionTest.getBic(), regressionTest.getWork());
//        long startTime = System.currentTimeMillis();
//        DeltaDebugResult deltaDebugResult = ProbDD(ricDir.toString(), hunks, regressionTest.getTestcase(), stepRange, cProb, cProbLeftIdx2Test);
//        // save to database?
//        // MysqlManager.insertCC("bic", regressionId, "ProbDD", ccHunks);
//        return deltaDebugResult;
//    }

    public static DeltaDebugResult probDD(String path, List<HunkEntity> hunkEntities, String testCase, List<Integer> stepRange, List<Double> firstCProb, List<Integer> cProbLeftIdx2Test) throws IOException {
        hunkEntities.removeIf(hunkEntity -> hunkEntity.getNewPath().contains("test"));
        hunkEntities.removeIf(hunkEntity -> hunkEntity.getOldPath().contains("test"));

//        List<String> relatedFile = getRelatedFile(hunkEntities);
        System.out.println("原hunk的数量是: " + hunkEntities.size());
        bw.append("\n原hunk的数量是: " + hunkEntities.size());
        bw.append("\n" + hunkEntities);
        bw.append("\n -------开始ProbDD---------");
        String tmpPath = path.replace("bic", "tmp");
        FileUtil.copyDirToTarget(path, tmpPath);
        assert Objects.equals(codeReduceTest(tmpPath, hunkEntities, testCase), "PASS");

        DeltaDebugResult deltaDebugResult = new DeltaDebugResult();
        DDStepResult ddStepResult = new DDStepResult();
        List<DDStepResult> DDstepList = new ArrayList<>();
//        List<HunkEntity> retseq = hunkEntities;
        List<Integer> retIdx = new ArrayList<>();
        List<Double> initCProb = new ArrayList<>();
        for (int i = 0; i < hunkEntities.size(); i++) {
            retIdx.add(i);
            initCProb.add(0.1);
        }
        deltaDebugResult.setAllHunkEntities(hunkEntities);
//        ddStepResult.setCcHunks(hunkEntities);
        ddStepResult.setCProbLeftIdx2Test(retIdx);
        Integer runTimes = 0;
        if (firstCProb != null && cProbLeftIdx2Test != null) {
            ddStepResult.setCProb(firstCProb);
            ddStepResult.setCProbLeftIdx2Test(cProbLeftIdx2Test);

            if (stepRange.get(1) != null) {
                runTimes = stepRange.get(1) - stepRange.get(0);
                for (int i = 0; i < runTimes; i++) {
                    int stepNum = stepRange.get(0) + 1 + i;
                    ddStepResult.setStepNum(stepNum);
                    ddStepResult = runDDTestByStep(ddStepResult, hunkEntities, path, tmpPath, testCase);
                    DDStepResult data = CodeUtil.clone(ddStepResult);
                    DDstepList.add(data);
                    if (testDone(ddStepResult.getCProb())) {
                        break;
                    }
                }
            } else {
                while (!testDone(ddStepResult.getCProb())) {
                    runTimes = runTimes + 1;
                    ddStepResult.setStepNum(stepRange.get(0) + runTimes);
                    ddStepResult = runDDTestByStep(ddStepResult, hunkEntities, path, tmpPath, testCase);
                    DDStepResult data = CodeUtil.clone(ddStepResult);
                    DDstepList.add(data);
                }
            }
        } else {
            ddStepResult.setCProb(initCProb);
            ddStepResult.setStepNum(runTimes);
            ddStepResult.setStepTestResult("FAIL");
            DDStepResult initData = CodeUtil.clone((ddStepResult));
            DDstepList.add(initData);
            while (!testDone(ddStepResult.getCProb())) {
                runTimes = runTimes + 1;
                ddStepResult.setStepNum(runTimes);
                ddStepResult = runDDTestByStep(ddStepResult, hunkEntities, path, tmpPath, testCase);
                DDStepResult data = CodeUtil.clone(ddStepResult);
                DDstepList.add(data);
//                retseq = DDTestResult.getCcHunks();
//                retIdx = DDTestResult.getCProbIdx();
//                cProb = DDTestResult.getCProb();
//            List<Integer> delIdx = sample(p);
//            if (delIdx.size() == 0) {
//                break;
//            }
//            time = time + 1;
//            List<Integer> idx2test = getIdx2test(retIdx, delIdx);
//            List<HunkEntity> seq2test = new ArrayList<>();
//            for (int idxelm : idx2test) {
//                seq2test.add(hunkEntities.get(idxelm));
//            }
//            FileUtil.copyDirToTarget(path, tmpPath);
////            copyRelatedFile(path,tmpPath,relatedFile);
//            System.out.print(time);
//            bw.append("\n" + time);
//            System.out.println(idx2test);
//            bw.append(" revert: " + idx2test);
//            if (Objects.equals(codeReduceTest(tmpPath + time, seq2test, testCase), "PASS")) {
//                for (int set0 = 0; set0 < p.size(); set0++) {
//                    if (!idx2test.contains(set0)) {
//                        p.set(set0, 0.0);
//                    }
//                }
//                retseq = seq2test;
//                retIdx = idx2test;
//            } else {
//                List<Double> pTmp = new ArrayList<>(p);
//                for (int setd = 0; setd < p.size(); setd++) {
//                    if (delIdx.contains(setd) && (p.get(setd) != 0) && (p.get(setd) != 1)) {
//                        double delta = (computeRatio(delIdx, pTmp) - 1) * pTmp.get(setd);
//                        p.set(setd, pTmp.get(setd) + delta);
//                    }
//                }
//            }
//            bw.append("\np: " + p);
            }
        }
        deltaDebugResult.setStepInfo(DDstepList);
        System.out.println("循环次数: " + runTimes);
        bw.append("\n循环次数: " + runTimes);
        return deltaDebugResult;
    }

    public static DDStepResult runDDTestByStep(DDStepResult lastResult,
//                                               List<Double> lastStepResult,
//                                               List<HunkEntity> ccHunkEntities,
//                                               List<Integer> retIdx,
                                               List<HunkEntity> hunkEntities,
//                                               Integer stepNum,
                                               String path,
                                               String tmpPath,
                                               String testCase) throws IOException {
//        DDStepResult result = new DDStepResult();
//        List<HunkEntity> hunkSeq = ccHunkEntities;
        List<Integer> delIdx = sample(lastResult.getCProb());
        if (delIdx.size() == 0) {
            return lastResult;
        }
        List<Integer> idx2test = getIdx2test(lastResult.getCProbLeftIdx2Test(), delIdx);
        List<HunkEntity> seq2test = new ArrayList<>();
        for (int idxelm : idx2test) {
            seq2test.add(hunkEntities.get(idxelm));
//            seq2test.add(ccHunkEntities.get(idxelm));
        }
        FileUtil.copyDirToTarget(path, tmpPath);
//        copyRelatedFile(path,tmpPath,relatedFile);
        System.out.print("\n" + lastResult.getStepNum());
        bw.append("\n" + lastResult.getStepNum());
        System.out.println(idx2test);
        bw.append(" revert: " + idx2test);

        lastResult.setCProbTestedInx(idx2test);
        String testResult = codeReduceTest(tmpPath, seq2test, testCase);
        if (Objects.equals(testResult, "PASS")) {
            lastResult.setStepTestResult("PASS");
            for (int set0 = 0; set0 < lastResult.getCProb().size(); set0++) {
                if (!idx2test.contains(set0)) {
                    lastResult.getCProb().set(set0, 0.0);
//                    lastStepResult.set(set0, 0.0);
                }
            }
//            lastResult.setCcHunks(seq2test);
            lastResult.setCProbLeftIdx2Test(idx2test);
//            hunkSeq = seq2test;
//            retIdx = idx2test;
        } else {
            lastResult.setStepTestResult(testResult);
            List<Double> pTmp = new ArrayList<>(lastResult.getCProb());
            for (int setd = 0; setd < lastResult.getCProb().size(); setd++) {
                if (delIdx.contains(setd) && (lastResult.getCProb().get(setd) != 0) && (lastResult.getCProb().get(setd) != 1)) {
                    double delta = (computeRatio(delIdx, pTmp) - 1) * pTmp.get(setd);
                    lastResult.getCProb().set(setd, pTmp.get(setd) + delta);
                }
            }
        }
        bw.append("\np: " + lastResult.getCProb());
        System.out.println("cProb: " + lastResult.getCProb());
//        result.setCcHunks(hunkSeq);
//        result.setCProb(lastStepResult);
//        result.setCProbIdx(retIdx);
        return lastResult;
    }

    public static List<String> getRelatedFile(List<HunkEntity> hunkEntities) {
        List<String> filePath = new ArrayList<>();
        for (HunkEntity hunk : hunkEntities) {
            filePath.add(hunk.getNewPath());
        }
        return filePath;
    }

    public static String codeReduceTest(String path, List<HunkEntity> hunkEntities, String testCase) throws IOException {
        String result = "UNRESOLVED";
        revert(path, hunkEntities);
        Executor executor = new Executor();
        executor.setDirectory(new File(path));
        String compileResult = executor.ddexec("mvn clean compile test-compile").replaceAll("\n", "");
        if (compileResult.contains("BUILD SUCCESS")) {
            String testResult = executor.ddexec("mvn test -Dtest=" + testCase).replaceAll("\n", "");
//            System.out.println(testResult);
            if (testResult.contains("BUILD FAILURE")) {
                result = "FAIL";
            } else if (testResult.contains("BUILD SUCCESS")) {
                result = "PASS";
            }
        } else {
            result = "CE";
        }
        System.out.println(result);
//        System.out.println("\nrevert: " + hunkEntities);
        bw.append("  " + result);
        return result;
    }

    public static boolean testDone(List<Double> p) {
        for (double prob : p) {
            if (abs(prob - 1.0) > 1e-6 && min(prob, 1) < 1.0 && prob != 0.0) {
                return false;
            }
        }
        return true;
    }

    public static List<Integer> sample(List<Double> p) {
        List<Integer> delset = new ArrayList<>();
        List<Integer> idxlist = sortToIndex(p);
        int k = 0;
        double tmp = 1;
        double last = -9999;
        int i = 0;
        while (i < p.size()) {
            if (p.get(idxlist.get(i)) == 0) {
                k = k + 1;
                i = i + 1;
                continue;
            }
            if (!(p.get(idxlist.get(i)) < 1)) {
                break;
            }
            for (int j = k; j < i + 1; j++) {
                tmp *= (1 - p.get(idxlist.get(j)));
            }
            tmp *= (i - k + 1);
            if (tmp < last) {
                break;
            }
            last = tmp;
            tmp = 1;
            i = i + 1;
        }
        while (i > k) {
            i = i - 1;
            delset.add(idxlist.get(i));
        }
        return delset;
    }

    public static List<Integer> getIdx2test(List<Integer> inp1, List<Integer> inp2) {
        List<Integer> result = new ArrayList<>();
        for (Integer elm : inp1) {
            if (!inp2.contains(elm)) {
                result.add(elm);
            }
        }
        return result;
    }

    public static double computeRatio(List<Integer> deleteConfig, List<Double> p) {
        double res = 0;
        double tmplog = 1;
        for (int delc : deleteConfig) {
            if (p.get(delc) > 0 && p.get(delc) < 1) {
                tmplog *= (1 - p.get(delc));
            }
        }
        res = 1 / (1 - tmplog);
        return res;
    }

    //返回的是从小到大的索引值List
    //相当于python中的argsort()
    private static List<Integer> sortToIndex(List<Double> p) {
        List<Integer> idxlist = new ArrayList<>();
        Map<Integer, Double> pidxMap = new HashMap<>();
        for (int j = 0; j < p.size(); j++) {
            pidxMap.put(j, p.get(j));
        }
        List<Map.Entry<Integer, Double>> entrys = new ArrayList<>(pidxMap.entrySet());
        entrys.sort(new Comparator<Map.Entry>() {
            public int compare(Map.Entry o1, Map.Entry o2) {
                return (int) ((double) o1.getValue() * 100000 - (double) o2.getValue() * 100000);
            }
        });
        for (Map.Entry<Integer, Double> entry : entrys) {
            idxlist.add(entry.getKey());
        }
        return idxlist;
    }

    // Revert.java 文件直接ctrl+c/v的 因为没有其他相似的
    public static void revert(String path, List<HunkEntity> hunkEntities) {
        try {
            Map<String, List<HunkEntity>> stringListMap = hunkEntities.stream().collect(Collectors.groupingBy(HunkEntity::getNewPath));
            for (Map.Entry<String, List<HunkEntity>> entry : stringListMap.entrySet()) {
                revertFile(path, entry.getValue());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static List<String> revertFile(String tmpPath, List<HunkEntity> hunkEntities) {
        String workPath = tmpPath.substring(0, tmpPath.lastIndexOf(File.separator)) + File.separator + "work";
        HunkEntity tmpHunk = hunkEntities.get(0);
        if (!Objects.equals(tmpHunk.getNewPath(), tmpHunk.getOldPath())) {
            String fileFullOldPath = workPath + File.separator + tmpHunk.getOldPath();
            String fileFullNewPath = tmpPath + File.separator + tmpHunk.getOldPath();
            FileUtil.copyDirToTarget(fileFullOldPath, fileFullNewPath);
        }
        List<String> line = FileUtil.readListFromFile(tmpPath + File.separator + tmpHunk.getNewPath());
        hunkEntities.sort(new Comparator<HunkEntity>() {
            @Override
            public int compare(HunkEntity p1, HunkEntity p2) {
                return p2.getBeginA() - p1.getBeginA();
            }
        });
        for (HunkEntity hunkEntity : hunkEntities) {
            String type = hunkEntity.getType();
            switch (type) {
                case "DELETE":
                    List<String> newLine = getLinesFromWorkVersion(workPath, hunkEntity);
                    line.addAll(hunkEntity.getBeginB(), newLine);
                    break;
                case "INSERT":
                    line.subList(hunkEntity.getBeginB(), hunkEntity.getEndB()).clear();
                    break;
                case "REPLACE":
                    line.subList(hunkEntity.getBeginB(), hunkEntity.getEndB()).clear();
                    List<String> replaceLine = getLinesFromWorkVersion(workPath, hunkEntity);
                    line.addAll(hunkEntity.getBeginB(), replaceLine);
                    break;
                case "EMPTY":
                    break;
            }
        }
        //oldPath是一个空文件的情况
        if (!Objects.equals(tmpHunk.getOldPath(), "/dev/null")) {
            FileUtil.writeListToFile(tmpPath + File.separator + tmpHunk.getOldPath(), line);
        }
        return line;
    }

    public static List<String> getLinesFromWorkVersion(String workPath, HunkEntity hunk) {
        List<String> line = FileUtil.readListFromFile(workPath + File.separator + hunk.getOldPath());
        List<String> result = line.subList(hunk.getBeginA(), hunk.getEndA());
        return result;
    }
}

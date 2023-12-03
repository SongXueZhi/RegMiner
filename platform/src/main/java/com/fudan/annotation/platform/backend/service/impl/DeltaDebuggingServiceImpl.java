package com.fudan.annotation.platform.backend.service.impl;

import com.fudan.annotation.platform.backend.core.Migrator;
import com.fudan.annotation.platform.backend.core.ProbDD;
import com.fudan.annotation.platform.backend.core.SourceCodeManager;
import com.fudan.annotation.platform.backend.dao.RegressionMapper;
import com.fudan.annotation.platform.backend.entity.*;
import com.fudan.annotation.platform.backend.service.DeltaDebuggingService;
import com.fudan.annotation.platform.backend.util.FileUtil;
import com.fudan.annotation.platform.backend.util.GitUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.fudan.annotation.platform.backend.core.ProbDD.*;


@Service
@Slf4j
public class DeltaDebuggingServiceImpl implements DeltaDebuggingService {
    static BufferedWriter bw;

    static {
        try {
            bw = new BufferedWriter(new FileWriter("detail", true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    private RegressionMapper regressionMapper;
    @Autowired
    private ProbDD probDD;
    @Autowired
    private SourceCodeManager sourceCodeManager;
    @Autowired
    private Migrator migrator;

    public static DDStepResult RunProbDDOnce(String path, List<HunkEntity> hunkEntities, String testCase,
                                             List<Integer> stepRange, List<Double> firstCProb,
                                             List<Integer> leftIdx2Test, List<Integer> stepTestedInx) throws IOException {
        hunkEntities.removeIf(hunkEntity -> hunkEntity.getNewPath().contains("test"));
        hunkEntities.removeIf(hunkEntity -> hunkEntity.getOldPath().contains("test"));

        bw.append("\n -------开始ProbDD人为选择的Step和Hunk---------");
        String tmpPath = path.replace("bic", "tmp");
        FileUtil.copyDirToTarget(path, tmpPath);
        assert Objects.equals(codeReduceTest(tmpPath, hunkEntities, testCase), "PASS");

        DDStepResult ddStepResult = new DDStepResult();
        List<DDStepResult> DDStepList = new ArrayList<>();
        List<Integer> retIdx = new ArrayList<>();

        ddStepResult.setLeftIdx2Test(retIdx);
        ddStepResult.setCProb(firstCProb);
        ddStepResult.setLeftIdx2Test(leftIdx2Test);
        ddStepResult.setStepNum(stepRange.get(0) + 1);

        ddStepResult = runDDTestWithHunkIdx(ddStepResult, hunkEntities, path, tmpPath, testCase, stepTestedInx);
        System.out.println(ddStepResult);
        return ddStepResult;
    }

    public static DDStepResult runDDTestWithHunkIdx(DDStepResult lastResult,
                                                    List<HunkEntity> hunkEntities,
                                                    String path,
                                                    String tmpPath,
                                                    String testCase,
                                                    List<Integer> stepTestedInx) throws IOException {
        List<Integer> idx2test = stepTestedInx;
        List<Integer> delIdx = getIdx2test(lastResult.getLeftIdx2Test(), idx2test);
        List<HunkEntity> seq2test = new ArrayList<>();
        for (int idxelm : idx2test) {
            seq2test.add(hunkEntities.get(idxelm));
        }
        FileUtil.copyDirToTarget(path, tmpPath);
        System.out.print("\n" + lastResult.getStepNum());
        bw.append("\n" + lastResult.getStepNum());
        System.out.println(idx2test);
        bw.append(" revert: " + idx2test);

        lastResult.setStepTestedInx(idx2test);
        String testResult = codeReduceTest(tmpPath, seq2test, testCase);
        if (Objects.equals(testResult, "PASS")) {
            lastResult.setStepTestResult("PASS");
            for (int set0 = 0; set0 < lastResult.getCProb().size(); set0++) {
                if (!idx2test.contains(set0)) {
                    lastResult.getCProb().set(set0, 0.0);
                }
            }
            lastResult.setLeftIdx2Test(idx2test);
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
        return lastResult;
    }

    @Override
    public DeltaDebugResult getRunProbDD(String regressionUuid, String revisionName, String userToken,
                                         List<Integer> stepRange) throws IOException {
        Regression regressionTest = regressionMapper.getRegressionInfo(regressionUuid);
        // get projectFile
        File projectDir = sourceCodeManager.getProjectDir(regressionTest.getProjectUuid(), regressionUuid, userToken);
        // checkout
        checkoutDD(regressionTest, projectDir, userToken);
//        List<Revision> revisionList =  checkoutBugCode(regressionTest.getRegressionUuid, projectDir, userToken);
        File bugDir =
                new File(SourceCodeManager.cacheProjectsDirPath + File.separator + userToken + File.separator + regressionUuid + File.separator + revisionName);

        List<HunkEntity> hunks = GitUtil.getHunksBetweenCommits(projectDir, regressionTest.getBic(),
                regressionTest.getWork());
//        long startTime = System.currentTimeMillis();
        return probDD.probDD(bugDir.toString(), hunks, regressionTest.getTestcase(), stepRange, null, null, null);
//        return probDD.runDeltaDebugging(regressionTest, projectDir, revisionList, stepRange, cProb,
//        cProbLeftIdx2Test);
    }

    @Override
    public DeltaDebugResult postRunProbDDbyStep(String regressionUuid, String revisionName, String userToken,
                                                List<Integer> stepRange, List<Double> cProb,
                                                List<Integer> leftIdx2Test, List<Integer> stepTestedInx) throws IOException {
        Regression regressionTest = regressionMapper.getRegressionInfo(regressionUuid);
        File projectDir = sourceCodeManager.getProjectDir(regressionTest.getProjectUuid(), regressionUuid, userToken);
        checkoutDD(regressionTest, projectDir, userToken);

        File bugDir =
                new File(SourceCodeManager.cacheProjectsDirPath + File.separator + userToken + File.separator + regressionUuid + File.separator + revisionName);
        List<HunkEntity> hunks = GitUtil.getHunksBetweenCommits(projectDir, regressionTest.getBic(),
                regressionTest.getWork());

        DDStepResult lastStepResult = RunProbDDOnce(bugDir.toString(), hunks, regressionTest.getTestcase(), stepRange
                , cProb, leftIdx2Test, stepTestedInx);
        stepRange.set(0, stepRange.get(0) + 1);

        return probDD.probDD(bugDir.toString(), hunks, regressionTest.getTestcase(), stepRange,
                lastStepResult.getCProb(), lastStepResult.getLeftIdx2Test(), lastStepResult);
    }

    public void checkoutDD(Regression regressionTest, File projectDir, String userToken) {
        File bugDir =
                new File(SourceCodeManager.cacheProjectsDirPath + File.separator + userToken + File.separator + regressionTest.getRegressionUuid());
        if (bugDir.exists()) {
            return;
        }
        List<Revision> targetCodeVersions = new ArrayList<>(4);
        Revision rfc = new Revision("bfc", regressionTest.getBfc());
        targetCodeVersions.add(rfc);
        targetCodeVersions.add(new Revision("buggy", regressionTest.getBuggy()));
        targetCodeVersions.add(new Revision("bic", regressionTest.getBic()));
        targetCodeVersions.add(new Revision("work", regressionTest.getWork()));

        targetCodeVersions.forEach(revision -> {
            revision.setLocalCodeDir(sourceCodeManager.checkout(revision, projectDir,
                    regressionTest.getRegressionUuid(), userToken));
        });
        targetCodeVersions.remove(0);
        migrator.migrateTestAndDependency(rfc, targetCodeVersions, regressionTest.getTestcase());
    }
}

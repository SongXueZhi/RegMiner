package com.fudan.annotation.platform.backend.controller;

import com.fudan.annotation.platform.backend.entity.DeltaDebugResult;
import com.fudan.annotation.platform.backend.entity.RunDDStepInput;
import com.fudan.annotation.platform.backend.service.DeltaDebuggingService;
import com.fudan.annotation.platform.backend.vo.ResponseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * description: delta debugging controller
 *
 * @author sunyujie create: 2022-11-02 11:10
 */
@RestController
@ResponseBody
@RequestMapping(value = {"/dd"})
public class DeltaDebuggingController {
    private DeltaDebuggingService deltaDebuggingService;

    @Autowired
    public void setDeltaDebuggingService(DeltaDebuggingService deltaDebuggingService) {
        this.deltaDebuggingService = deltaDebuggingService;
    }

    @GetMapping(value = "/runDD")
    public ResponseBean<DeltaDebugResult> getDDResults(
            @RequestParam(name = "regression_uuid") String regressionUuid,
            @RequestParam(name = "revision_name") String revisionName,
            @RequestParam String userToken
    ) {
        List<Integer> stepRange = new ArrayList<>();
        stepRange.add(0);
        stepRange.add(null);
        try {
            DeltaDebugResult result = deltaDebuggingService.getRunProbDD(regressionUuid, revisionName, userToken,
                    stepRange);
            return new ResponseBean<>(200, "Run complete, return critical change hunks", result);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseBean<>(401, "Run failed: " + e.getMessage(), null);
        }
    }

    @PostMapping(value = "/runDDStep")
    public ResponseBean<DeltaDebugResult> postRunDDByStep(@RequestBody RunDDStepInput runDDStepInput) {
        List<Integer> stepRange = new ArrayList<>();
        if (runDDStepInput.getEndStep() != null) {
            stepRange.add(runDDStepInput.getStartStep());
            stepRange.add(runDDStepInput.getEndStep());
        } else {
            stepRange.add(runDDStepInput.getStartStep());
            stepRange.add(null);
        }
        System.out.println(runDDStepInput);
        try {
            DeltaDebugResult result = deltaDebuggingService.postRunProbDDbyStep(
                    runDDStepInput.getRegressionUuid(),
                    runDDStepInput.getRevisionName(),
                    runDDStepInput.getUserToken(),
                    stepRange,
                    runDDStepInput.getCprob(),
                    runDDStepInput.getLeftIdx2Test(),
                    runDDStepInput.getTestedHunkIdx());
            return new ResponseBean<>(200, "Run complete, return critical change hunks", result);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseBean<>(401, "Run failed: " + e.getMessage(), null);
        }
    }
}

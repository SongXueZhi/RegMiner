package com.fudan.annotation.platform.backend.controller;

import com.fudan.annotation.platform.backend.entity.DeltaDebugResult;
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
            @RequestParam(name = "start_step") Integer startStep,
            @RequestParam(name = "end_step", required = false) Integer endStep,
            @RequestParam String userToken,
            @RequestParam(required = false) List<Double> cProb,
            @RequestParam(name = "cProb_left_idx_to_test", required = false) List<Integer> cProbLeftIdx2Test
//            @RequestBody(required = false) RunDDStepInput stepInput
    ) {
        List<Integer> stepRange = new ArrayList<>();
        if (endStep != null) {
            stepRange.add(startStep);
            stepRange.add(endStep);
        } else {
            stepRange.add(startStep);
            stepRange.add(null);
        }
//        System.out.println(stepInput.getCProb());
//        System.out.println(stepInput.getCProbLeftIdx2Test());
        System.out.println(cProb);
        System.out.println(cProbLeftIdx2Test);
        try {
            DeltaDebugResult result = deltaDebuggingService.getDeltaDebuggingResults(regressionUuid, revisionName, userToken, stepRange, cProb, cProbLeftIdx2Test);
            return new ResponseBean<>(200, "Run complete, return critical change hunks", result);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseBean<>(401, "Run failed: " + e.getMessage(), null);
        }
    }

//    @GetMapping(value = "/DDSteps")
//    public ResponseBean<> getDDByStep(@RequestParam(required = false) int steps) {
//        try {
//            DDStep result = deltaDebuggingService.runDeltaDebuggingByStep()
//        } catch (Exception e) {
//            return new ResponseBean<>(401, "Run failed: " + e.getMessage(), );
//        }
//    }
}

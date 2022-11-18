package com.fudan.annotation.platform.backend.controller;

import com.fudan.annotation.platform.backend.entity.HunkEntity;
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
    public ResponseBean<List<HunkEntity>> getDDResults(
            @RequestParam(name = "regression_uuid") String regressionUuid,
            @RequestParam(name = "c_prob", required = false) List<Double> cProb,
            @RequestParam(name = "start_step") Integer startStep,
            @RequestParam(name = "end_step", required = false) Integer endStep,
            @RequestParam String userToken
            ) {
        List<Integer> stepRange = new ArrayList<>();
        if (endStep != null) {
            stepRange.add(startStep);
            stepRange.add(endStep);
        } else {
            stepRange.add(startStep);
            stepRange.add(null);
        }
        System.out.println(stepRange);
        try {
            List<HunkEntity> ccHunks = deltaDebuggingService.getDeltaDebuggingResults(regressionUuid, userToken, stepRange, cProb);
            return new ResponseBean<>(200, "Run complete, return critical change hunks", ccHunks);
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

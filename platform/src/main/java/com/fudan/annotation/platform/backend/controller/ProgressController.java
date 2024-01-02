package com.fudan.annotation.platform.backend.controller;

import com.fudan.annotation.platform.backend.entity.ProgressInfo;
import com.fudan.annotation.platform.backend.entity.RegressionDetail;
import com.fudan.annotation.platform.backend.entity.SearchDetails;
import com.fudan.annotation.platform.backend.service.ProgressService;
import com.fudan.annotation.platform.backend.service.RegressionService;
import com.fudan.annotation.platform.backend.vo.ResponseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: sxz
 * @Date: 2022/05/31/23:05
 * @Description:
 */
@RestController
@ResponseBody
@RequestMapping(value = "/progress")
public class ProgressController {
    private ProgressService progressService;
    private RegressionService regressionService;

    @GetMapping(value = "/info")
    public ResponseBean<ProgressInfo> getProgressInfo() {
        try {
            ProgressInfo progressInfo = progressService.getProgressInfo();
            progressInfo.setRegressionNum(regressionService.getRegressions(-1,null, null,
                    progressInfo.getCurrentProjectName(), null, null, null, null,null,null).size());
            return new ResponseBean<>(200, "get progress info success", progressInfo);
        } catch (Exception e) {
            return new ResponseBean<>(401, "get failed :" + e.getMessage(), null);
        }
    }

    @GetMapping(value = "/searchDetails")
    public ResponseBean<SearchDetails> getSearchDetails(@RequestParam(name = "bfc") String bfc,
                                                        @RequestParam(name = "project_name") String projectName) {
        try {
            if (projectName.contains("/")) {
                projectName = projectName.split("/")[1];
            }
            SearchDetails searchDetails = progressService.getSearchDetails(projectName, bfc);
            return new ResponseBean<>(200, "get progress info success", searchDetails);
        } catch (Exception e) {
            return new ResponseBean<>(401, "get failed :" + e.getMessage(), null);
        }
    }


    @Autowired
    public void setProgressService(ProgressService progressService) {
        this.progressService = progressService;
    }

    @Autowired
    public void setRegressionService(RegressionService regressionService) {
        this.regressionService = regressionService;
    }
}

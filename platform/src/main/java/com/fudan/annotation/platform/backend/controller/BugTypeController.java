package com.fudan.annotation.platform.backend.controller;

import com.fudan.annotation.platform.backend.entity.BugToTypeItems;
import com.fudan.annotation.platform.backend.entity.BugTypes;
import com.fudan.annotation.platform.backend.entity.CreateBugToType;
import com.fudan.annotation.platform.backend.entity.CreateBugType;
import com.fudan.annotation.platform.backend.service.BugTypeService;
import com.fudan.annotation.platform.backend.vo.ResponseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * description: bug type controller
 *
 * @author sunyujie
 * create: 2023-06-28 15:54
 */
@RestController
@ResponseBody
@RequestMapping(value = {"/bugType"})
public class BugTypeController {
    private BugTypeService bugTypeService;

    @Autowired
    public void setBugTypeService(BugTypeService bugTypeService) {
        this.bugTypeService = bugTypeService;
    }

    @GetMapping(value = "/all")
    public ResponseBean<List<BugTypes>> getAllBugTypes(
            @RequestParam(name = "bug_type_name", required = false) String bugTypeName
    ) {
        try {
            List<BugTypes> bugTypeList = bugTypeService.getAllBugTypes(bugTypeName);
            return new ResponseBean<>(200, "get all bug types success", bugTypeList);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseBean<>(401, "get failed: " + e.getMessage(), null);
        }
    }

    @PostMapping(value = "/create")
    public ResponseBean createNewBugType(
            @RequestBody CreateBugType newBugType) {
        try {
            bugTypeService.createNewBugType(newBugType);
            return new ResponseBean<>(200, "create bug type success", null);
        } catch (Exception e) {
            return new ResponseBean<>(401, "create bug type failed: " + e.getMessage(), null);
        }
    }

    @DeleteMapping(value = "/delete")
    public ResponseBean deleteBugTypeById(
            @RequestParam(name = "bug_type_id") int bugTypeId) {
        try {
            bugTypeService.deleteBugTypeById(bugTypeId);
            return new ResponseBean<>(200, "delete bug type success", null);
        } catch (Exception e) {
            return new ResponseBean<>(401, "delete bug type failed: " + e.getMessage(), null);
        }
    }

    @GetMapping(value = "/regression/bugTypeDetail")
    public ResponseBean<List<BugToTypeItems>> getBugToTypeByRegressionUuid(
            @RequestParam(name = "regression_uuid") String regressionUuid) {
        try {
            List<BugToTypeItems> regressionBugType = bugTypeService.getBugToTypeByRegressionUuid(regressionUuid);
            return new ResponseBean<>(200, "get data success", regressionBugType);
        } catch (Exception e) {
            return new ResponseBean<>(401, "get data failed: " + e.getMessage(), null);
        }
    }

    @PostMapping(value = "/agree")
    public ResponseBean postAgreeBugType(
            @RequestParam(name = "regression_uuid") String regressionUuid,
            @RequestParam(name = "bug_type_id") int bugTypeId) {
        try {
            bugTypeService.postAgreeBugType(regressionUuid, bugTypeId);
            return new ResponseBean<>(200, "bug type agreed", null);
        } catch (Exception e) {
            return new ResponseBean<>(401, "agreed failed: " + e.getMessage(), null);
        }
    }

    @PostMapping(value = "/disagree")
    public ResponseBean postDisagreeBugType(
            @RequestParam(name = "regression_uuid") String regressionUuid,
            @RequestParam(name = "bug_type_id") int bugTypeId) {
        try {
            bugTypeService.postDisagreeBugType(regressionUuid, bugTypeId);
            return new ResponseBean<>(200, "bug type disagreed", null);
        } catch (Exception e) {
            return new ResponseBean<>(401, "disagreed failed: " + e.getMessage(), null);
        }
    }

    @PostMapping(value = "/tagBugType")
    public ResponseBean createNewBugToType(
            @RequestBody CreateBugToType newBugToType) {
        try {
            bugTypeService.createBugTypeToRegression(newBugToType);
            return new ResponseBean<>(200, "create bug type success", null);
        } catch (Exception e) {
            return new ResponseBean<>(401, "create bug type failed: " + e.getMessage(), null);
        }
    }

}

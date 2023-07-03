package com.fudan.annotation.platform.backend.controller;

import com.fudan.annotation.platform.backend.entity.BugTypes;
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
    public ResponseBean<List<BugTypes>> getAllBugTypes() {
        try {
            List<BugTypes> bugTypeList = bugTypeService.getAllBugTypes();
            return new ResponseBean<>(200, "get all bug types success", bugTypeList);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseBean<>(401, "get failed :" + e.getMessage(), null);
        }
    }

    @PostMapping(value = "/create")
    public ResponseBean createNewBugType(
            @RequestBody CreateBugType newBugType) {
        try {
            bugTypeService.createNewBugType(newBugType);
            return new ResponseBean<>(200, "create bug type success", null);
        } catch (Exception e) {
            return new ResponseBean<>(401, "create bug type failed :" + e.getMessage(), null);
        }
    }

    @DeleteMapping(value = "/delete")
    public ResponseBean deleteBugTypeById(
            @RequestParam("bug_type_id") int bugTypeId) {
        try {
            bugTypeService.deleteBugTypeById(bugTypeId);
            return new ResponseBean<>(200, "delete bug type success", null);
        } catch (Exception e) {
            return new ResponseBean<>(401, "delete bug type failed :" + e.getMessage(), null);
        }
    }

}

package com.fudan.annotation.platform.backend.controller;

import com.fudan.annotation.platform.backend.entity.BugTypes;
import com.fudan.annotation.platform.backend.service.BugTypeService;
import com.fudan.annotation.platform.backend.vo.ResponseBean;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * description: bug type controller
 *
 * @author David create: 2023-06-28 15:54
 */
@RestController
@ResponseBody
@RequestMapping(value = {"/bugType"})
public class BugTypeController {
    private BugTypeService bugTypeService;

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

//    @PostMapping(value = "/register")
//    public ResponseBean register(
//            @RequestBody AccountReg account) {
//        try {
//            accountService.insertUser(account);
//            return new ResponseBean<>(200, "register success", null);
//        } catch (Exception e) {
//            return new ResponseBean<>(401, "register failed :" + e.getMessage(), null);
//        }
//    }
//
//    @DeleteMapping(value = "/delete")
//    public ResponseBean deleteUser(
//            @RequestParam("account_id") int accountId) {
//        try {
//            accountService.deleteUser(accountId);
//            return new ResponseBean<>(200, "delete account success", null);
//        } catch (Exception e) {
//            return new ResponseBean<>(401, "delete account failed :" + e.getMessage(), null);
//        }
//    }

}

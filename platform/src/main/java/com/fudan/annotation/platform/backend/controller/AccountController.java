package com.fudan.annotation.platform.backend.controller;

import com.fudan.annotation.platform.backend.entity.Account;
import com.fudan.annotation.platform.backend.entity.AccountReg;
import com.fudan.annotation.platform.backend.entity.AccountVO;
import com.fudan.annotation.platform.backend.entity.LoginInfo;
import com.fudan.annotation.platform.backend.service.AccountService;
import com.fudan.annotation.platform.backend.vo.ResponseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;

/**
 * description: account controller
 *
 * @author Richy create: 2021-12-10 15:54
 */
@RestController
@ResponseBody
@RequestMapping(value = {"/account"})
public class AccountController {
    private AccountService accountService;

    @GetMapping(value = "/all")
    public ResponseBean<List<Account>> getAllUsers(
            @RequestParam(name = "account_id", required = false) Integer accountId,
            @RequestParam(name = "account_name", required = false) String accountName,
            @RequestParam(name = "role", required = false) String role) {
        try {

            List<Account> accountInfo = accountService.getUser(accountId, accountName, role);
            return new ResponseBean<>(200, "get account info success", accountInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseBean<>(401, "get failed :" + e.getMessage(), null);
        }
    }

    @PostMapping(value = "/register")
    public ResponseBean register(
            @RequestBody AccountReg account) {
        try {
            accountService.insertUser(account);
            return new ResponseBean<>(200, "register success", null);
        } catch (Exception e) {
            return new ResponseBean<>(401, "register failed :" + e.getMessage(), null);
        }
    }

    @DeleteMapping(value = "/delete")
    public ResponseBean deleteUser(
            @RequestParam("account_id") int accountId) {
        try {
            accountService.deleteUser(accountId);
            return new ResponseBean<>(200, "delete account success", null);
        } catch (Exception e) {
            return new ResponseBean<>(401, "delete account failed :" + e.getMessage(), null);
        }
    }

    @PutMapping(value = "/reset/password")
    public ResponseBean resetPassword(
            @RequestParam("account_name") String accountName,
            @RequestParam("password") String password) {
        try {
            accountService.resetPassword(accountName, password);
            return new ResponseBean<>(200, "reset success", null);
        } catch (Exception e) {
            return new ResponseBean<>(401, "reset failed :" + e.getMessage(), null);
        }
    }

    @PostMapping(value = "/login")
    public ResponseBean<AccountVO> login(
            @RequestBody LoginInfo loginInfo) {
        try {
            AccountVO userInfo = accountService.login(loginInfo);
            return new ResponseBean<>(200, "login success", userInfo);
        } catch (Exception e) {
            return new ResponseBean<>(401, "login failed :" + e.getMessage(), null);
        }
    }

//    @GetMapping(value = "/currentUser")
//    public ResponseBean<?> getCurrentUser() {
//        AccountVO userVO = AccountVO.builder().uuid("test").username("admin").build();
//        return ResponseBean.success(ResponseCodeMsg.SUCCESS, userVO);
//    }

//    @GetMapping(value = "/outLogin")
//    public ResponseBean outLogin(HttpSession session) {
//        session.invalidate(); // 使Session变成无效，及用户退出
//        return new ResponseBean<>(200, "logout success", null);
//    }

    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }
}

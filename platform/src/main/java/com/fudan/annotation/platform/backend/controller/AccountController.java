package com.fudan.annotation.platform.backend.controller;

import com.fudan.annotation.platform.backend.entity.Account;
import com.fudan.annotation.platform.backend.entity.AccountVO;
import com.fudan.annotation.platform.backend.service.AccountService;
import com.fudan.annotation.platform.backend.vo.ResponseBean;
import com.fudan.annotation.platform.backend.enums.ResponseCodeMsg;
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
      @RequestParam(name = "uuid", required = false) String uuid,
      @RequestParam(name = "account_name", required = false) String accountName,
      @RequestParam(name = "account_right", required = false) Integer accountRight) {
    List<Account> accountInfos = accountService.getUser(uuid, accountName, accountRight);
    try {
      return new ResponseBean<>(200, "get account info success", accountInfos);
    } catch (Exception e) {
      return new ResponseBean<>(401, "get failed :" + e.getMessage(), null);
    }
  }

  @PostMapping(value = "/register")
  public ResponseBean register(@RequestBody Account account) {
    try {
      accountService.insertUser(account);
      return new ResponseBean<>(200, "register success", null);
    } catch (Exception e) {
      return new ResponseBean<>(401, "register failed :" + e.getMessage(), null);
    }
  }

  @DeleteMapping(value = "/delete")
  public ResponseBean deleteUser(@RequestParam("uuid") String uuid) {
    try {
      accountService.deleteUser(uuid);
      return new ResponseBean<>(200, "delete account success", null);
    } catch (Exception e) {
      return new ResponseBean<>(401, "delete account failed :" + e.getMessage(), null);
    }
  }

  @PutMapping(value = "/reset/password")
  public ResponseBean resetPassword(
      @RequestParam("uuid") String uuid, @RequestParam("password") String password) {
    try {
      accountService.resetPassword(uuid, password);
      return new ResponseBean<>(200, "reset success", null);
    } catch (Exception e) {
      return new ResponseBean<>(401, "reset failed :" + e.getMessage(), null);
    }
  }

  @PostMapping(value = "/login")
  public ResponseBean<AccountVO> login() {
    AccountVO userVO =
        AccountVO.builder().uuid(UUID.randomUUID().toString()).username("admin").build();
    return ResponseBean.success(ResponseCodeMsg.SUCCESS, userVO);
  }

  @GetMapping(value = "/currentUser")
  public ResponseBean<?> getCurrentUser() {
    AccountVO userVO = AccountVO.builder().uuid("test").username("admin").build();
    return ResponseBean.success(ResponseCodeMsg.SUCCESS, userVO);
  }

  @GetMapping(value = "/outLogin")
  public ResponseBean outLogin(HttpSession session) {
    session.invalidate(); // 使Session变成无效，及用户退出
    return new ResponseBean<>(200, "logout success", null);
  }

  @Autowired
  public void setAccountService(AccountService accountService) {
    this.accountService = accountService;
  }
}

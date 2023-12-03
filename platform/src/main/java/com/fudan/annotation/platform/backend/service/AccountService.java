package com.fudan.annotation.platform.backend.service;

import com.fudan.annotation.platform.backend.entity.Account;
import com.fudan.annotation.platform.backend.entity.AccountReg;
import com.fudan.annotation.platform.backend.entity.AccountVO;
import com.fudan.annotation.platform.backend.entity.LoginInfo;

import java.util.List;

public interface AccountService {

    /**
     * description 获取所有用户
     *
     * @param accountId   用户的ID
     * @param accountName 用户姓名
     */
    List<Account> getUser(Integer accountId, String accountName, String role);

    /**
     * description 插入新用户
     *
     * @param account 用户信息
     */
    void insertUser(AccountReg account);

    /**
     * description 删除用户
     *
     * @param accountId 用户的ID
     */
    void deleteUser(int accountId);

    /**
     * description 重置密码
     *
     * @param accountName 用户的新密码
     * @param password    用户权限
     */
    void resetPassword(String accountName, String password);

    /**
     * description 登录
     *
     * @param loginInfo 用户登录信息
     */
    AccountVO login(LoginInfo loginInfo);
}

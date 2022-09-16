package com.fudan.annotation.platform.backend.service;

import com.fudan.annotation.platform.backend.entity.Account;
import com.fudan.annotation.platform.backend.entity.AccountVO;
import com.fudan.annotation.platform.backend.entity.LoginInfo;

import java.util.List;

public interface AccountService {

    /**
     * description 获取所有用户
     *
     * @param  uuid 用户的ID
     * @param  accountName 用户姓名
     * @param  accountRight 用户权限
     */
    List<Account> getUser(String uuid, String accountName, Integer accountRight);

    /**
     * description 插入新用户
     *
     * @param  account 用户信息
     */
    void insertUser(Account account);

    /**
     * description 删除用户
     *
     * @param  uuid 用户的ID
     */
    void deleteUser(String uuid);

    /**
     * description 重置密码
     *
     * @param  uuid 用户的ID
     * @param  password 用户的新密码
     */
    void resetPassword(String uuid, String password);

    /**
     * description 登录
     *
     * @param  loginInfo 用户登录信息
     */
    AccountVO login(LoginInfo loginInfo);
}

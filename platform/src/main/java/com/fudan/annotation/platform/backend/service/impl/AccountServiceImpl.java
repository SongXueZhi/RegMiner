package com.fudan.annotation.platform.backend.service.impl;

import com.fudan.annotation.platform.backend.dao.AccountMapper;
import com.fudan.annotation.platform.backend.entity.Account;
import com.fudan.annotation.platform.backend.entity.AccountReg;
import com.fudan.annotation.platform.backend.entity.AccountVO;
import com.fudan.annotation.platform.backend.entity.LoginInfo;
import com.fudan.annotation.platform.backend.service.AccountService;
import com.fudan.annotation.platform.backend.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * description:
 *
 * @author sunyujie
 * create: 2021-12-10 16:04
 **/

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {
    private AccountMapper accountMapper;

    @Override
    public List<Account> getUser(Integer accountId, String accountName, String role) {
        return accountMapper.getUserByParam(accountId, accountName, role);
    }

    @Override
    public void insertUser(AccountReg account) {
        if (account.getAccountName() == null || account.getPassword() == null || account.getRole() == null) {
            throw new RuntimeException("param loss");
        }
        account.setPassword(MD5Util.md5(account.getAccountName() + account.getPassword()));
        accountMapper.insert(account);
    }

    @Override
    public void deleteUser(int accountId) {
        accountMapper.deleteByPrimaryKey(accountId);
    }

    @Override
    public void resetPassword(String accountName, String password) {
        //MD5加密密码
        String encodePassword = MD5Util.md5(accountName + password);
        accountMapper.resetPassword(accountName, encodePassword);
    }

    @Override
    public AccountVO login(LoginInfo loginInfo) {
        //MD5加密密码
        loginInfo.setPassword(MD5Util.md5(loginInfo.getAccountName() + loginInfo.getPassword()));
        AccountVO account = accountMapper.login(loginInfo);
        if (account != null) {
            String userToken = MD5Util.md5(MD5Util.md5(loginInfo.getAccountName() + loginInfo.getPassword()));
            return new AccountVO(account.getAccountId(), account.getAccountName(), account.getEmail(),
                    account.getAvatar(), account.getRole(), userToken);
        } else {
            throw new RuntimeException("No such user");
        }
    }

    @Autowired
    public void setAccountMapper(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }
}
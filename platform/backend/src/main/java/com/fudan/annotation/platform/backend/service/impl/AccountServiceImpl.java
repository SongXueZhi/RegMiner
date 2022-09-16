package com.fudan.annotation.platform.backend.service.impl;

import com.fudan.annotation.platform.backend.dao.AccountMapper;
import com.fudan.annotation.platform.backend.entity.Account;
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
 * @author Richy
 * create: 2021-12-10 16:04
 **/

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {
    private AccountMapper accountMapper;

    @Override
    public List<Account> getUser(String uuid, String accountName, Integer accountRight) {
        return accountMapper.getUserByParam(uuid, accountName, accountRight);
    }

    @Override
    public void insertUser(Account account) {
        if(account.getAccountName() == null || account.getPassword() == null){
            throw new RuntimeException("param loss");
        }
        account.setUuid(UUID.randomUUID().toString());
        account.setPassword(MD5Util.md5(account.getAccountName() + account.getPassword()));
        account.setAccountRight(1);
        accountMapper.insert(account);
    }

    @Override
    public void deleteUser(String uuid) {
        accountMapper.deleteByPrimaryKey(uuid);
    }

    @Override
    public void resetPassword(String uuid, String password) {
        String accountName = accountMapper.getAccountName(uuid);
        //MD5加密密码
        String encodePassword = MD5Util.md5(accountName + password);
        accountMapper.resetPassword(uuid, encodePassword);
    }

    @Override
    public AccountVO login(LoginInfo loginInfo) {
        //MD5加密密码
        String encodePassword = MD5Util.md5(loginInfo.getUsername() + loginInfo.getPassword());
        loginInfo.setPassword(encodePassword);
        Account account = accountMapper.login(loginInfo);
        if(account != null){
            String userToken = MD5Util.md5(encodePassword);
            return new AccountVO(account.getUuid(), account.getAccountName(), account.getPassword(), account.getAccountRight(), userToken);
        }
        return null;
    }

    @Autowired
    public void setAccountMapper(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }


}
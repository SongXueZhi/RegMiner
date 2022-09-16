package com.fudan.annotation.platform.backend.dao;

import com.fudan.annotation.platform.backend.entity.Account;
import com.fudan.annotation.platform.backend.entity.LoginInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface AccountMapper {

    int deleteByPrimaryKey(String uuid);

    int insert(Account account);

    int insertSelective(Account record);

    List<Account> getUserByParam(String uuid, String accountName, Integer accountRight);

    int updateByPrimaryKeySelective(Account record);

    int resetPassword(String uuid, String password);

    Account login(LoginInfo loginInfo);

    String getAccountName(String uuid);
}
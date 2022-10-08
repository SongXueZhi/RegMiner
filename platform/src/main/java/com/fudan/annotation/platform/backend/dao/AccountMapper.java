package com.fudan.annotation.platform.backend.dao;

import com.fudan.annotation.platform.backend.entity.Account;
import com.fudan.annotation.platform.backend.entity.AccountReg;
import com.fudan.annotation.platform.backend.entity.AccountVO;
import com.fudan.annotation.platform.backend.entity.LoginInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface AccountMapper {

    int deleteByPrimaryKey(int accountId);

    int insert(AccountReg account);

    int resetPassword(String accountName, String password);

    List<Account> getUserByParam(Integer accountId, String accountName, String role);

    AccountVO login(LoginInfo loginInfo);

    //    int insertSelective(Account record);
    //    int updateByPrimaryKeySelective(Account record);
}
package com.lw.thewar.dao;

import com.lw.thewar.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User selectUserLogin(@Param("telphone") String telphone, @Param("password") String password);

    int selectAccountExist(@Param("loginWay") Integer loginWay,@Param("account") String account);

    int updatePasswordByTelphone(@Param("telphone") String telphone,@Param("password") String password);

    int updateStatusById(@Param("status")Integer status,@Param("userId")Integer userId);

    int updateAuthsStatus(Integer userid);

    Integer selectUseridByhxId(String hxId);
}
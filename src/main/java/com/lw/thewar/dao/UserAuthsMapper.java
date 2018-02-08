package com.lw.thewar.dao;

import com.lw.thewar.pojo.UserAuths;
import com.lw.thewar.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserAuthsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserAuths record);

    int insertSelective(UserAuths record);

    UserAuths selectByPrimaryKey(Integer id);

    UserAuths selectByUserId(Integer userId);

    int updateByPrimaryKeySelective(UserAuths record);

    int updateByPrimaryKey(UserAuths record);


    int updateByUserIdSelective(UserAuths record);

    int updatePhotoById(@Param("fileName") String fileName, @Param("userid") Integer userd);

    int updateAlbumById(@Param("albumStr") String albumStr, @Param("userid") Integer userd);

    String selectUserPhoneById(Integer userId);

    String selectNameByUserId(Integer userId);

    String selectHeadPhotoByUserId(Integer userId);

    List<SearchLikeFriendInfo> selectUserIdByLikeName(String name);

    SearchFriendInfo selectUserIdByUserId(Integer userId);

    List<FriendListInfo> selectFriendListInfobyHxUserNameList(List<String> hxUserNameList);

    GroupUsers selectNameGenderHead(Integer userId);

    MyAuth selectMyAuthByUserID(Integer userId);

    String selectAlbumByUserId(Integer userId);

    Map<String,String> selectNamePhotoByUserId(Integer userId);
}
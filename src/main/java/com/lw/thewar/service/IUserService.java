package com.lw.thewar.service;

import com.lw.thewar.common.ServerResponse;
import com.lw.thewar.pojo.UserAuths;

import java.io.IOException;

public interface IUserService {

    public ServerResponse selectLogin(String telphone, String password);

    public Boolean selectAccountExist(Integer loginWay,String account);

    public String selectplugins(Integer plugins_id);

    public ServerResponse registered_checkTelphone(String telphone);

    public ServerResponse insert_registered_checkCode(String code,String telphone,String password) throws IOException;

    public ServerResponse forgetPassword(String telphone);

    public ServerResponse updatePassword(String telphone,String code,String password);

    public ServerResponse updatePerfectUserAuths(UserAuths userAuths,Integer userId,String hxUsername) throws IOException;

    public ServerResponse select_hxname(String hxusername);

    public ServerResponse selectUserHeadPhoto(String hxusername);

    public ServerResponse selectGroupHeadPhoto(String groupId);

    public ServerResponse searchFriendInfoList(String name);

    public ServerResponse searchByUserId(Integer myUserId,Integer userId) throws IOException;

    public ServerResponse createChatGroup(Integer userId,
                                          String groupname,
                                          String desc,
                                          boolean isPublic,
                                          Integer maxusers,
                                          boolean isApproval,
                                          String hxUserName) throws IOException;

    public ServerResponse getChatGroupInfo(Integer userId,Long groupId,String hxUserName) throws IOException;

    public ServerResponse getInfoByhxId(Integer myUserId,String hxId,String myHxUserName) throws IOException;

    public ServerResponse addFriend(String hxUserName,Integer userId,String myhxUserName) throws IOException;

    public ServerResponse delFriend(String hxUserName,Integer userId,String myhxUserName) throws IOException;

    public ServerResponse getFriendList(Integer userId,Long groupId,String myHxUserName) throws IOException;

    public ServerResponse applyAddChatGroup(Integer userId,Long groupId,String myHxUserName) throws IOException;

    public ServerResponse inviteAddChatGroup(Integer userId,Long groupId,String userList,String myHxUserName) throws IOException;

    public ServerResponse exitGroup(Integer userId,Long groupId) throws IOException;

    public ServerResponse delGroup(Integer userId,Long groupId,String hxUserName) throws IOException;

    public ServerResponse getJoinGroups(Integer userId,String myHxUserName) throws IOException;

    public ServerResponse getGroupUsers(String myHxId,Long groupId) throws IOException;

    public ServerResponse getMyAuth(Integer userId);

    public ServerResponse delAlbum(Integer userId,Integer index) throws IOException;

    public ServerResponse getNamePhotoByHXId(String hxId);

}

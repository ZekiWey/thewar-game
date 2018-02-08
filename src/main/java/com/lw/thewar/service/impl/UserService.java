package com.lw.thewar.service.impl;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lw.thewar.common.*;
import com.lw.thewar.dao.ChatGroupMapper;
import com.lw.thewar.dao.PluginsMapper;
import com.lw.thewar.dao.UserAuthsMapper;
import com.lw.thewar.dao.UserMapper;
import com.lw.thewar.pojo.ChatGroup;
import com.lw.thewar.pojo.Plugins;
import com.lw.thewar.pojo.User;
import com.lw.thewar.pojo.UserAuths;
import com.lw.thewar.service.IUserService;
import com.lw.thewar.util.FTPUtil;
import com.lw.thewar.util.HX_RestUtil;
import com.lw.thewar.util.JsonUtil;
import com.lw.thewar.util.PropertiesUtil;
import com.lw.thewar.vo.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.Collator;
import java.util.*;

@Service("iUserService")
public class UserService implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PluginsMapper pluginsMapper;
    @Autowired
    private UserAuthsMapper userAuthsMapper;
    @Autowired
    private ChatGroupMapper chatGroupMapper;


    //验证手机登录
    public ServerResponse selectLogin(String telphone, String password){

        if(selectAccountExist(Const.LoginWay.PHONE_LOGIN.getCode(),telphone)){
            User user = userMapper.selectUserLogin(telphone,password);
            if(user == null){
                return ServerResponse.createByErrorMessage("密码输入错误");
            }
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("该账号还未注册");
    }


    public String selectplugins(Integer plugins_id){
        return pluginsMapper.selectPlugins(plugins_id);
    }



    //判断账号是否被注册
    public Boolean selectAccountExist(Integer loginWay,String account){
        return userMapper.selectAccountExist(loginWay,account) > 0;
    }

    //判断手机号是否被注册，如果未被注册，就发送验证码到该手机号码上
    public ServerResponse registered_checkTelphone(String telphone){
        if(selectAccountExist(Const.LoginWay.PHONE_LOGIN.getCode(),telphone)){
            return ServerResponse.createByErrorMessage("该手机号已经被注册，请更换手机号再试");
        }
        String code = sendSmsCode(telphone);

        if(code == null){
            return ServerResponse.createByErrorMessage("验证码获取失败，请重试");
        }

        CodeCache.setKey(CodeCache.TOKEN_PREFIX+telphone,code);

        return ServerResponse.createBySuccessMsg("验证码已经发送");
    }

    //验证输入的验证码是否正确并注册
    public ServerResponse insert_registered_checkCode(String code,String telphone,String password) throws IOException {

        if(code.equals(CodeCache.getKey(CodeCache.TOKEN_PREFIX+telphone))){
            String hxUserName = getHXUserName();
            if(!HX_RestUtil.regist_user(hxUserName)){
                return ServerResponse.createByErrorMessage("网络异常，请重试");
            }
            Plugins userPlugins = new Plugins();
            userPlugins.setHxmapAccount(hxUserName);
            int result = pluginsMapper.insert(userPlugins);
            if(result <= 0){
                return ServerResponse.createByErrorMessage("注册失败");
            }
            User user = new User();
            user.setStatus(Const.AccountLoginInfo.MEMBER.getCode());
            user.setIdentifiter(telphone);
            user.setIdentityType(Const.LoginWay.PHONE_LOGIN.getCode());
            user.setPassword(password);
            user.setPluginsId(userPlugins.getId());
            user.setAuthsStatus(0);
            result =userMapper.insert(user);

            if(result <= 0){
                return ServerResponse.createByErrorMessage("注册失败");
            }

            UserAuths userAuths = new UserAuths();
            userAuths.setUserId(user.getId());
            userAuths.setName("请输入昵称");
            userAuths.setGender(0);
            userAuths.setProvinces("北京");
            userAuths.setCity("北京");
            userAuths.setHeadImage("default.jpg");
            userAuths.setBirthday(new Date());
            userAuthsMapper.insert(userAuths);
            if(result > 0){
                return ServerResponse.createBySuccessMsg("注册成功");
            }

        }

        return ServerResponse.createByErrorMessage("验证码已过期或者不正确");
    }


    //验证手机号并发送找回密码的验证码
    public ServerResponse forgetPassword(String telphone){

        if(telphone == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        if(!selectAccountExist(Const.LoginWay.PHONE_LOGIN.getCode(),telphone)){
            return ServerResponse.createByErrorMessage("该手机号未被注册");
        }

        String code = sendSmsCode(telphone);

        if(code == null){
            return ServerResponse.createByErrorMessage("验证码获取失败，请重试");
        }

        CodeCache.setKey(CodeCache.TOKEN_PREFIX+telphone,code);

        return ServerResponse.createBySuccess("验证码已经发送");

    }

    public ServerResponse updatePassword(String telphone,String code,String password){
        if (code.equals(CodeCache.getKey(CodeCache.TOKEN_PREFIX+telphone))){

            int result = userMapper.updatePasswordByTelphone(telphone, password);
            if(result > 0){
                return ServerResponse.createBySuccessMsg("密码重置成功，请重新登录");
            }
            return ServerResponse.createByErrorMessage("密码重置失败，请重试");
        }

        return ServerResponse.createByErrorMessage("验证码输入错误");

    }

    //完善用户资料
    public ServerResponse updatePerfectUserAuths(UserAuths userAuths,Integer userId,String hxUsername) throws IOException {
        userAuths.setUserId(userId);
        int result = userAuthsMapper.updateByUserIdSelective(userAuths);
        if(result < 0){
            return ServerResponse.createByErrorMessage("完善信息失败,请重试");
        }
        if(userAuths.getJhId() != null){
            result = userMapper.updateStatusById(Const.AccountLoginInfo.JX_MEMBER.getCode(),userAuths.getUserId());
            if(result < 0){
                return ServerResponse.createByErrorMessage("完善信息失败,请重试");
            }
        }
        result = userMapper.updateAuthsStatus(userAuths.getUserId());
        if(result < 0){
            return ServerResponse.createBySuccessMsg("完善信息失败,请重试");
        }
        if(HX_RestUtil.updateName(userAuths.getName(),hxUsername)){
            return ServerResponse.createBySuccessMsg("完善信息成功");
        }
        return ServerResponse.createByErrorMessage("完善信息失败,请重试");
    }

    public ServerResponse getMyAuth(Integer userId){
        if(null == userId){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        MyAuth myAuth = userAuthsMapper.selectMyAuthByUserID(userId);
        if(null == myAuth){
            return ServerResponse.createByErrorMessage("没有该用户");
        }
        myAuth.setHeadImage(PropertiesUtil.getProperty("ftp.servxer.http.prefi") + myAuth.getHeadImage());

        if("null".equals(myAuth.getAlbum())){
            return ServerResponse.createBySuccess("个人资料如下",myAuth);
        }
        myAuth.setAlbumlist(Arrays.asList(myAuth.getAlbum().split(",")));
        for (int i = 0; i < myAuth.getAlbumlist().size(); i++) {
            myAuth.getAlbumlist().set(i,PropertiesUtil.getProperty("ftp.servxer.http.prefi")+myAuth.getAlbumlist().get(i));
        }
        myAuth.setAlbum("real");
        return ServerResponse.createBySuccess("个人资料如下",myAuth);
    }

    public ServerResponse selectUserHeadPhoto(String hxusername){
        Integer userId = userMapper.selectUseridByhxId(hxusername);

        if(null == userId){
            return ServerResponse.createByErrorMessage("没有找到该用户");
        }

        String headPhoto = userAuthsMapper.selectHeadPhotoByUserId(userId);

        if(null == headPhoto){
            return ServerResponse.createByErrorMessage("没有找到该用户");
        }

        return ServerResponse.createBySuccess("用户头像已经找到", PropertiesUtil.getProperty("ftp.servxer.http.prefi") + PropertiesUtil.getProperty("ftp.servxer.img.thumbnail") + headPhoto);

    }
    public ServerResponse selectGroupHeadPhoto(String groupId){
        if(null == groupId){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Long lgroupId = Long.valueOf(groupId);
        String groupHeadPhoto = chatGroupMapper.selectHeadImageByGroupId(lgroupId);
        if(null == groupHeadPhoto){
            return ServerResponse.createByErrorMessage("未找到头像");
        }
        return ServerResponse.createBySuccess("找到头像",PropertiesUtil.getProperty("ftp.servxer.http.prefi")+groupHeadPhoto);
    }

    public ServerResponse select_hxname(String hxusername){
        Integer userId = userMapper.selectUseridByhxId(hxusername);

        if(null == userId){
            return ServerResponse.createByErrorMessage("没有找到该用户");
        }

        String name = userAuthsMapper.selectNameByUserId(userId);

        if(null == name){
            return ServerResponse.createByErrorMessage("没有找到该用户");
        }

        return ServerResponse.createBySuccess("用户昵称已经找到",name);

    }
    public ServerResponse getNamePhotoByHXId(String hxId){
        Integer userId = userMapper.selectUseridByhxId(hxId);

        if(null == userId){
            return ServerResponse.createByErrorMessage("没有找到该用户");
        }
        Map<String,String> map = userAuthsMapper.selectNamePhotoByUserId(userId);
        if(null == map){
            return ServerResponse.createByErrorMessage("没有找到该用户信息");
        }
        map.put("headImage",PropertiesUtil.getProperty("ftp.servxer.http.prefi") + PropertiesUtil.getProperty("ftp.servxer.img.thumbnail") + map.get("head_image"));
        map.remove("head_image");
        return ServerResponse.createBySuccess("找到信息",map);

    }

    public ServerResponse searchFriendInfoList(String name){
        if(null == name || name.trim().equals("") ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        name = new StringBuffer().append('%').append(name).append('%').toString();

        List<SearchLikeFriendInfo> seachLikeNameAuths = userAuthsMapper.selectUserIdByLikeName(name);

        if(CollectionUtils.isEmpty(seachLikeNameAuths)){
            return ServerResponse.createByErrorMessage("暂无搜索结果");
        }

        return ServerResponse.createBySuccess("找到以下数据",seachLikeNameAuths);


    }

    public ServerResponse searchByUserId(Integer myUserId,Integer userId) throws IOException {
        if(null == userId){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        SearchFriendInfo searchlFriendInfo = userAuthsMapper.selectUserIdByUserId(userId);

        if(searchlFriendInfo == null){
            return ServerResponse.createByErrorMessage("暂无搜索结果");
        }

        searchlFriendInfo.setUserId(userId);
        String hxUserName = pluginsMapper.selectHXUsernameByUserId(myUserId);
        if(hxUserName == null){
            return ServerResponse.createByErrorMessage("网络异常,请重试");
        }
        searchlFriendInfo.setFriend(HX_RestUtil.isFriend(hxUserName,searchlFriendInfo.getHxmapAccount()));
        return ServerResponse.createBySuccess("找到以下数据",searchlFriendInfo);

    }

    public ServerResponse createChatGroup(Integer userId,
                                          String groupname,
                                          String desc,
                                          boolean isPublic,
                                          Integer maxusers,
                                          boolean isApproval,
                                          String hxUserName) throws IOException {
        if(hxUserName == null){
            return ServerResponse.createByErrorMessage("未找到该用户");
        }
        Long groupId = HX_RestUtil.createChatGroups(groupname,desc,isPublic,maxusers,isApproval,hxUserName);
        if(groupId == null){
            return ServerResponse.createByErrorMessage("创建群失败");
        }
        ChatGroup chatGroup = new ChatGroup();
        chatGroup.setGroupName(groupname);
        chatGroup.setGroupDesc(desc);
        chatGroup.setHeadImage("aaa.jpg");
        chatGroup.setIsPublic(isPublic?1:0);
        chatGroup.setGroupId(groupId);
        chatGroup.setOwnerId(userId);
        int result = chatGroupMapper.insert(chatGroup);
        if(result <= 0){
            return ServerResponse.createByErrorMessage("创建群失败");
        }
        return ServerResponse.createBySuccessMsg("创建成功");
    }

    public ServerResponse getChatGroupInfo(Integer userId,Long groupId,String hxUserName) throws IOException {
        if(hxUserName == null){
            return ServerResponse.createByErrorMessage("未找到该用户");
        }
        String headImage = chatGroupMapper.selectHeadImageByGroupId(groupId);
        if(headImage == null){
            return ServerResponse.createByErrorMessage("没有该群组");
        }
        headImage = PropertiesUtil.getProperty("ftp.servxer.http.prefi") + headImage;

        ChatGroupInfo chatGroupInfo = HX_RestUtil.getGroupInfo(groupId,hxUserName);
        if(chatGroupInfo == null){
            return ServerResponse.createByErrorMessage("网络出现异常，请重试");
        }
        if(chatGroupInfo.isOwner()){
            chatGroupInfo.setIsIn(true);
        }
        else {
            chatGroupInfo.setIsIn(HX_RestUtil.isInGroup(hxUserName,groupId));
        }
        chatGroupInfo.setHeadImage(headImage);

        return ServerResponse.createBySuccess("查询成功",chatGroupInfo);
    }

    public ServerResponse getInfoByhxId(Integer myUserId,String hxId,String myHxUserName) throws IOException {
        Integer userId = userMapper.selectUseridByhxId(hxId);
        if(null == userId){
            return ServerResponse.createByErrorMessage("没有找到该用户");
        }
        SearchFriendInfo searchlFriendInfo = userAuthsMapper.selectUserIdByUserId(userId);

        if(searchlFriendInfo == null){
            return ServerResponse.createByErrorMessage("暂无搜索结果");
        }

        searchlFriendInfo.setUserId(userId);
        if(myHxUserName == null){
            return ServerResponse.createByErrorMessage("网络异常,请重试");
        }
        if (myHxUserName.equals(searchlFriendInfo.getHxmapAccount())){
            searchlFriendInfo.setFriend(0);
        }else {
            searchlFriendInfo.setFriend(HX_RestUtil.isFriend(myHxUserName, searchlFriendInfo.getHxmapAccount()));
        }
        return ServerResponse.createBySuccess("找到以下数据",searchlFriendInfo);
    }

    public ServerResponse addFriend(String hxUserName,Integer userId,String myhxUserName) throws IOException {

        if(hxUserName == null){
            return ServerResponse.createByErrorMessage("网络异常");
        }


        if(HX_RestUtil.addFriend(myhxUserName,hxUserName)){
            return ServerResponse.createBySuccessMsg("添加成功");
        }
        else {
            return ServerResponse.createByErrorMessage("添加失败");
        }
    }
    public ServerResponse delFriend(String hxUserName,Integer userId,String myhxUserName) throws IOException {

        if(hxUserName == null){
            return ServerResponse.createByErrorMessage("参数错误");
        }


        if(HX_RestUtil.delFriend(myhxUserName,hxUserName)){
            return ServerResponse.createBySuccessMsg("删除成功");
        }
        else {
            return ServerResponse.createByErrorMessage("删除失败");
        }
    }
    public ServerResponse getFriendList(Integer userId,Long groupId,String myHxUserName) throws IOException {
        List<String> hxUserNameList = HX_RestUtil.getFriendList(myHxUserName,groupId);
        if(hxUserNameList.size() == 0){
            return ServerResponse.createByErrorMessage("您的所有好友都已在此群");
        }
        if(CollectionUtils.isEmpty(hxUserNameList)){
            return ServerResponse.createByErrorMessage("网络异常");
        }
        List<FriendListInfo> friendListInfoList = userAuthsMapper.selectFriendListInfobyHxUserNameList(hxUserNameList);
        if(null == friendListInfoList){
            return ServerResponse.createByErrorMessage("拉取好友列表失败");
        }
        return ServerResponse.createBySuccess("拉取好友列表成功",friendListInfoList);
    }
    public ServerResponse applyAddChatGroup(Integer userId,Long groupId,String myHxUserName) throws IOException {
        List<String> hxUserNameList = Lists.newArrayList();
        hxUserNameList.add(myHxUserName);

        if(HX_RestUtil.applyChatGroup(groupId,hxUserNameList)){
            return ServerResponse.createBySuccessMsg("添加成功");
        }else {
            return ServerResponse.createByErrorMessage("添加失败");
        }

    }
    public ServerResponse inviteAddChatGroup(Integer userId,Long groupId,String userList,String myHxUserName) throws IOException {

        System.out.println(userList);

        List<String> hxUserNameList = JsonUtil.parseObject(userList,List.class);

        System.out.println("----------------------------"+hxUserNameList);

        if(CollectionUtils.isEmpty(hxUserNameList)){
            return ServerResponse.createByErrorMessage("添加失败");
        }

        if(HX_RestUtil.applyChatGroup(groupId,hxUserNameList)){
            return ServerResponse.createBySuccessMsg("添加成功");
        }else {
            return ServerResponse.createByErrorMessage("添加失败");
        }

    }
    public ServerResponse exitGroup(Integer userId,Long groupId) throws IOException {
        String myHxUserName = pluginsMapper.selectHXUsernameByUserId(userId);

        if(HX_RestUtil.removeChatGroup(groupId,myHxUserName)){
            return ServerResponse.createBySuccessMsg("退出成功");
        }else {
            return ServerResponse.createByErrorMessage("退出失败");
        }

    }

    public ServerResponse delGroup(Integer userId,Long groupId,String hxUserName) throws IOException {
        Integer ownerId = chatGroupMapper.selectOwnerByGroupId(groupId);
        if(ownerId == null){
            return ServerResponse.createByErrorMessage("网络异常，请重试");
        }
        if(ownerId.equals(userId)){

            if(HX_RestUtil.deleteChatGroup(groupId)){
                int result = chatGroupMapper.deleteByGroupId(groupId);
                if(result <= 0){
                    //todo 加入队列
                }
                return ServerResponse.createBySuccessMsg("解散群成功");
            }
            else{
                return ServerResponse.createByErrorMessage("网络异常");
            }
        }else {
            if(HX_RestUtil.removeChatGroup(groupId,hxUserName)){
                return ServerResponse.createBySuccessMsg("退出群成功");
            }
            else{
                return ServerResponse.createByErrorMessage("网络异常");
            }
        }
    }

//    public ServerResponse getGroupList(Integer userId){
//        String myHxUserName = pluginsMapper.selectHXUsernameByUserId(userId);
//
//    }
    public ServerResponse getJoinGroups(Integer userId,String myHxUserName) throws IOException {

        List<Map<String,Object>> groups = HX_RestUtil.getUserJoinGroups(myHxUserName);

        if(null == groups){
            return ServerResponse.createByErrorMessage("网络异常");
        }
        if(groups.size()<=0){
            return ServerResponse.createByErrorMessage("该用户还没有加入群组");
        }
        for (Map<String, Object> map : groups) {
            Long groupId = Long.valueOf(map.get("groupid").toString());
            String headImage = chatGroupMapper.selectHeadImageByGroupId(groupId);
            if(null == headImage){
                map.put("headImage",PropertiesUtil.getProperty("ftp.servxer.http.prefi") + headImage);
            }else {
                //默认头像
                map.put("headImage",PropertiesUtil.getProperty("ftp.servxer.http.prefi") + "aaa.jpg");
            }
            Integer ownerId = chatGroupMapper.selectOwnerByGroupId(groupId);
            if(null != ownerId){
                if(userId.equals(ownerId)){
                    //代表群主
                    map.put("access",0);
                }
                else{
                    //代表普通成员
                    map.put("access",2);
                }
            }
        }
        return ServerResponse.createBySuccess("用户所有参与群",groups);


    }

    public ServerResponse getGroupUsers(String myHxId,Long groupId) throws IOException {
        List<String> usersList = HX_RestUtil.getGroupUsers(groupId);
        if(CollectionUtils.isEmpty(usersList)){
            return ServerResponse.createByErrorMessage("网络异常，请重试");
        }
        if(!usersList.contains(myHxId)){
            return ServerResponse.createByErrorMessage("该用户没有在群内");
        }
        Integer ownerId = chatGroupMapper.selectOwnerByGroupId(groupId);
        if(null == ownerId){
            return ServerResponse.createByErrorMessage("没有该群");
        }
        List<GroupUsers> groupUsersList = Lists.newArrayList();
        for (String hxId : usersList) {
            Integer id = userMapper.selectUseridByhxId(hxId);
            GroupUsers groupUsers = userAuthsMapper.selectNameGenderHead(id);
            groupUsers.setHxmapAccount(hxId);
            String headImage = PropertiesUtil.getProperty("ftp.servxer.http.prefi")+PropertiesUtil.getProperty("ftp.servxer.img.thumbnail") + groupUsers.getHeadImage();
            groupUsers.setHeadImage(headImage);
            if(id.equals(ownerId)){
                //代表群主
                groupUsers.setAccess(0);
            }
            else{
                //代表普通用户
                groupUsers.setAccess(2);
            }
            groupUsersList.add(groupUsers);
        }
        if(CollectionUtils.isEmpty(groupUsersList)){
            return ServerResponse.createByErrorMessage("网络异常，请重试");
        }
//        Collections.sort(groupUsersList, Collator.getInstance(java.util.Locale.CHINA));
        Collections.sort(groupUsersList,new GroupUsersComparator());

        return ServerResponse.createBySuccess("所有成员",groupUsersList);
    }

    public ServerResponse delAlbum(Integer userId,Integer index) throws IOException {
        if(index > 5 || index < 0){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        String albumStr = userAuthsMapper.selectAlbumByUserId(userId);
        List<String> albumList;
        if("null".equals(albumStr)){
           return ServerResponse.createByErrorMessage("相册为空");
        }else {
            albumList = Lists.newArrayList(albumStr.split(","));
        }
        if(index + 1 > albumList.size()){
            return ServerResponse.createByErrorMessage("下标越界");
        }
        String filename = albumList.get(index);
        String thumbnailFileName = PropertiesUtil.getProperty("ftp.servxer.img.thumbnail") + filename;

        try {
            FTPUtil.delefile(filename);
            FTPUtil.delefile(thumbnailFileName);
        } catch (IOException e) {
            throw e;
        }

        System.out.println("1----------------------------------------------"+albumList.toString());
        albumList.remove(filename);
        System.out.println("2----------------------------------------------"+albumList.toString());
        if(albumList.size() == 0){
            albumStr = "null";
        }else {
            albumStr = "";
            for (String s : albumList) {
                albumStr += s + ",";
            }
            albumStr = albumStr.substring(0,albumStr.length()-1);
        }
        System.out.println("3----------------------------------------------"+albumStr);
        int reulst = userAuthsMapper.updateAlbumById(albumStr,userId);

        if(reulst > 0){
            return ServerResponse.createBySuccessMsg("图片删除成功");
        }

        return ServerResponse.createBySuccessMsg("网络异常");
    }








    private Integer selectUserIdByhxId(String hxId){
        return userMapper.selectUseridByhxId(hxId);
    }















                                          //发送验证码
    public String sendSmsCode(String telphone){
        //设置超时时间-可自行调整
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        //初始化ascClient需要的几个参数
        final String product = "Dysmsapi";//短信API产品名称
        final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名
        //AK
        final String accessKeyId = "LTAIZYOXbIOCScdg";//accessKeyId
        final String accessKeySecret = "Rk8mVQ24ipWdTyYHsJVki4Zfr8PN1y";//accessKeySecret
        //初始化ascClient,暂时不支持多region（请勿修改）
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId,
                accessKeySecret);
        try {
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        IAcsClient acsClient = new DefaultAcsClient(profile);
        //组装请求对象
        SendSmsRequest request = new SendSmsRequest();
        //使用post提交
        request.setMethod(MethodType.POST);

        //手机号
        request.setPhoneNumbers(telphone);
        //签名
        request.setSignName("于海超");
        //模版
        request.setTemplateCode("SMS_121161051");
        //生成验证码
        int ran = (int) (Math.random() * 9999 + 1);
        String code = String.valueOf(ran);
        //替换Json串
        request.setTemplateParam("{\"code\":\""+ code + "\"}");

        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId("yourOutId");

        SendSmsResponse sendSmsResponse = null;
        try {
            sendSmsResponse = acsClient.getAcsResponse(request);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        System.out.println(sendSmsResponse.getCode());
        if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
            System.out.println("发送成功");
            return code;
        }
        return null;
    }

    public String getHXUserName(){
        String hxUserName = getUUID();
        while (pluginsMapper.selectHXUsername(hxUserName) > 0){
            hxUserName=getUUID();
        }
        return hxUserName;
    }

    public  String getUUID() {
        String chars = "qwertyuioplkjhgfdsazxcvbnm";
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        // 去掉"-"符号
        String temp = chars.charAt(new Random().nextInt(26)) + str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 17);
        return temp;
    }



}

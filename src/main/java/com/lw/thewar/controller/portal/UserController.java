package com.lw.thewar.controller.portal;

import com.google.zxing.common.detector.MathUtils;
import com.lw.thewar.common.Const;
import com.lw.thewar.common.ResponseCode;
import com.lw.thewar.common.ServerResponse;
import com.lw.thewar.pojo.Test;
import com.lw.thewar.pojo.User;
import com.lw.thewar.pojo.UserAuths;
import com.lw.thewar.redis.RedisDao;
import com.lw.thewar.service.IFileService;
import com.lw.thewar.service.IUserService;
import com.lw.thewar.vo.UserLoginInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;
import org.springframework.session.web.http.HeaderHttpSessionStrategy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IFileService fileService;



    /**
     * 登录接口 login.do
     * @param telphone  手机号  String
     * @param password  密码   String
     * @param session 此处的session是把已经登录的账号信息添加到session缓存中
     * @return  Integer status  String msg  com.lw.thewar.vo.UserLoginInfo
     */
    @RequestMapping("login.do")
    @ResponseBody
    public ServerResponse login(String telphone, String password, HttpSession session){
        if(telphone == null || password == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        ServerResponse serverResponse = userService.selectLogin(telphone,password);

        if(serverResponse.isSuccess()){
            User user = (User) serverResponse.getData();
            if(user.getStatus() == Const.AccountLoginInfo.SEAL.getCode()){
                return ServerResponse.createByErrorMessage("该账号暂时被禁封");
            }
            String hxmpAccount = userService.selectplugins(user.getPluginsId());
            if(hxmpAccount == null){
                return ServerResponse.createByErrorMessage("登录出现异常请重试");
            }
            UserLoginInfo userLoginInfo = new UserLoginInfo();
            userLoginInfo.setHxUserName(hxmpAccount);
            userLoginInfo.setMapUserName(hxmpAccount);
            userLoginInfo.setLoginAccount(user.getIdentifiter());
            userLoginInfo.setLoginWay(user.getIdentityType());
            userLoginInfo.setUserID(user.getId());
            userLoginInfo.setLoginStatus(user.getAuthsStatus());
            session.setAttribute(Const.CURRENT_USER,userLoginInfo);
            userLoginInfo.setxAuthToken(session.getId());
            System.out.println("-------------------" + session.getId());
//            redisDao.set(session.getId(),hxmpAccount);
            //todo   将环信端账号退出登录
            return ServerResponse.createBySuccess("登录成功",userLoginInfo);
        }
        return serverResponse;
    }

    /**
     * 根据header头里面传来的token获取对应的session如果该seesion里面存在登录信息就返回否则就返回身份验证过期
     * @param session
     * @return Integer status  String msg  om.lw.thewar.vo.UserLoginInfo
     */
    @RequestMapping("get_login_info.do")
    @ResponseBody
    public ServerResponse getLoginInfo(HttpSession session){
        UserLoginInfo userLoginInfo = (UserLoginInfo) session.getAttribute(Const.CURRENT_USER);
        if(userLoginInfo == null){
            return ServerResponse.createByErrorMessage("身份验证已过期");
        }
        //todo   将环信端账退出登录
        //todo   验证客户端传来的UserID跟缓存是否一致
        return ServerResponse.createBySuccess("登录成功",userLoginInfo);

    }

    /**
     * 退出登录接口   logout.do
     * @param session 此处的session是把已经登录的账号信息从session缓存中移除
     * @return Integer status  String msg
     */
    @RequestMapping("logout.do")
    @ResponseBody
    public ServerResponse logout(HttpSession session){
        UserLoginInfo userLoginInfo = (UserLoginInfo) session.getAttribute(Const.CURRENT_USER);
        if(userLoginInfo == null){
            return ServerResponse.createByErrorMessage("当前用户未登录");
        }
        //123
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccessMsg("退出成功");
    }



    /**
     *在输入手机号进入以后  验证手机号是否被注册 如果注册过就输入密码登录  如果没有就进行注册
     * check_telphone.do
     * @param telphone 手机号 类型String
     * @return Integer status  String msg
     */
    @RequestMapping("check_telphone.do")
    @ResponseBody
    public ServerResponse checkTelphone(String telphone){
        if(telphone == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        if(userService.selectAccountExist(Const.LoginWay.PHONE_LOGIN.getCode(),telphone)){
            return ServerResponse.createByErrorMessage("该手机号已被注册");
        }
        return ServerResponse.createBySuccessMsg("该手机号没有被注册");
    }

    /**
     * 验证手机号是否被注册过，并且发送验证码到手机
     * registered_check_telphone.do
     * @param telphone 手机号 类型String
     * @return Integer status  String msg
     */
    @RequestMapping("registered_check_telphone.do")
    @ResponseBody
    public ServerResponse registeredCheckTelphone(String telphone){
        if(telphone == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        return userService.registered_checkTelphone(telphone);
    }

    /**
     * 注册接口 registered_check_code.do
     *如果验证码输入无误则注册成功
     * @param code      验证码    String
     * @param telphone  手机号    String
     * @param password  密码      String
     * @return Integer status  String msg
     * @throws IOException
     */
    @RequestMapping("registered_check_code.do")
    @ResponseBody
    public ServerResponse registeredCheckCode(String code,String telphone,String password) throws IOException {
        if(telphone == null || code == null || password == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        return userService.insert_registered_checkCode(code,telphone,password);
    }

    /**
     * 忘记密码发送验证码到手机上  forget_password.do
     * @param telphone  手机号 String
     * @return Integer status  String msg
     */
    @RequestMapping("forget_password.do")
    @ResponseBody
    public ServerResponse forgetPassword(String telphone){
        return userService.forgetPassword(telphone);
    }

    /**
     * 重置密码 reset_password.do
     * @param telphone   手机号    String
     * @param code       验证码    String
     * @param password   新密码    String
     * @return Integer status  String msg
     */
    //重置密码
    @RequestMapping("reset_password.do")
    @ResponseBody
    public ServerResponse resetPassword(String telphone,String code,String password){
        if(telphone == null || code == null || password == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
       ServerResponse serverResponse = userService.updatePassword(telphone, code, password);
       if(serverResponse.isSuccess()){
           //todo 在更改密码后把session删除  单点登录问题
           return ServerResponse.createBySuccessMsg("密码重置成功，请重新登录");
       }
       return serverResponse;
    }

    /**
     * 用户完善信息页面 上传头像接口
     * 并且压缩一个图片图片名称为[thumbnail]用户环信名.jpg
     * @param file  要上传的头像
     * @param session
     * @param userId  当前登录的用户ID
     * @return Integer status  String msg
     * @throws IOException
     */
    @RequestMapping("upload_photo.do")
    @ResponseBody
    public ServerResponse uploadPhoto(MultipartFile file, HttpSession session, Integer userId) throws IOException {
        if(file == null || userId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        UserLoginInfo userLoginInfo = (UserLoginInfo) session.getAttribute(Const.CURRENT_USER);
        if(userLoginInfo == null){
            return ServerResponse.createByErrorMessage("当前没有登录用户");
        }
        if(!userId.equals(userLoginInfo.getUserID()))
        {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return fileService.updatePhoto(file,"Photo",userLoginInfo.getUserID());
    }

    /**
     * 完善信息页面更新资料
     * 并且更新用户环信的昵称
     * @param name      用户的昵称
     * @param gender    用户的性别 0女 1男
     * @param region    用户的所在地区  - 分割  省市
     * @param birthday  用户的出生日期
     * @param introduce 用户的个性签名
     * @param jhId      用户的家豪ID
     * @param userId    当前登录的用户ID
     * @param session
     * @return Integer status  String msg
     * @throws IOException
     */
    @RequestMapping("perfect_auths.do")
    @ResponseBody
    public ServerResponse perfectAuths( @RequestParam(value = "name",required = true) String name,
                                        @RequestParam(value = "gender",required = true) Integer gender,
                                        @RequestParam(value = "region",required = true) String region,
                                        @DateTimeFormat(pattern = "yyyy-mm-dd")
                                        @RequestParam(value = "birthday",required = true) Date birthday,
                                        @RequestParam(value = "introduce",required = false) String introduce,
                                        @RequestParam(value = "jhId",required = false) String jhId,
                                        @RequestParam(value = "userId",required = true) Integer userId,
                                        HttpSession session) throws IOException {

        if(name == null || gender == null || region == null || birthday == null || userId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        UserLoginInfo userLoginInfo = (UserLoginInfo) session.getAttribute(Const.CURRENT_USER);
        if(userLoginInfo == null){
            return ServerResponse.createByErrorMessage("当前没有登录用户");
        }
        if(!userId.equals(userLoginInfo.getUserID()))
        {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        String[] diqu = region.split("-");
        UserAuths userAuths = new UserAuths();
        userAuths.setName(name);
        userAuths.setGender(gender);
        userAuths.setProvinces(diqu[0]);
        userAuths.setCity(diqu[1]);
        userAuths.setBirthday(birthday);

        if(introduce != null){
            userAuths.setIntroduce(introduce);
        }
        if(jhId != null){
            userAuths.setJhId(jhId);
        }

        return userService.updatePerfectUserAuths(userAuths,userLoginInfo.getUserID(),userLoginInfo.getHxUserName());
    }

    /**
     * 通过环信ID获取昵称
     * @param hxId  环信ID
     * @return Integer status  String msg  String
     */
    @RequestMapping("get_name.do")
    @ResponseBody
    public ServerResponse getName(String hxId){
        if(StringUtils.isBlank(hxId)){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return userService.select_hxname(hxId);
    }

    /**
     *通过环信ID获取用户的的信息
     * @param hxId
     * @param session
     * @return Integer status  String msg  Map
     */
    @RequestMapping("get_name_photo.do")
    @ResponseBody
    public ServerResponse getNamePhoto(String hxId,HttpSession session){
        if (StringUtils.isBlank(hxId)) {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        UserLoginInfo userLoginInfo = (UserLoginInfo) session.getAttribute(Const.CURRENT_USER);
        if(userLoginInfo == null){
            return ServerResponse.createByErrorMessage("当前没有登录用户");
        }

        return userService.getNamePhotoByHXId(hxId);


    }

    /**
     * 通过环信ID获取用户头像　缩略图
     * @param hxId
     * @return  String
     */
    @RequestMapping("get_head_photo.do")
    @ResponseBody
    public ServerResponse getHeadPhoto(String hxId) {
        if (StringUtils.isBlank(hxId)) {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        String c = hxId.substring(0, 1);
        if (c.matches("[0-9]")) {
            return userService.selectGroupHeadPhoto(hxId);
        } else {
            return userService.selectUserHeadPhoto(hxId);
        }
    }
    /**
     * 通过昵称模糊搜索添加好友
     * @param name
     * @param session
     * @return com.lw.thewar.voSearchlFriendInfo
     */
    @RequestMapping("get_like_name.do")
    @ResponseBody
    public ServerResponse getLikeName(String name,HttpSession session){
        UserLoginInfo userLoginInfo = (UserLoginInfo) session.getAttribute(Const.CURRENT_USER);
        if(userLoginInfo == null){
            return ServerResponse.createByErrorMessage("当前没有登录用户");
        }

        return userService.searchFriendInfoList(name);
    }

    /**
     * 通过用户ID查找用户的信息
     * @param userId
     * @param session
     * @return com.lw.thewar.vo.SearchlFriendInfo
     */
    @RequestMapping("search_by_id.do")
    @ResponseBody
    public ServerResponse searchById(Integer userId,HttpSession session){
        UserLoginInfo userLoginInfo = (UserLoginInfo) session.getAttribute(Const.CURRENT_USER);
        if(userLoginInfo == null){
            return ServerResponse.createByErrorMessage("当前没有登录用户");
        }
        if(userId.equals(userLoginInfo.getUserID())){
            return ServerResponse.createByErrorMessage("不能添加自己为好友");
        }
        try {
            return userService.searchByUserId(userLoginInfo.getUserID(),userId);
        } catch (IOException e) {
            return ServerResponse.createByErrorMessage("网络异常,请重试");
        }
    }

    /**
     * 通过环信ID获取对方的详细信息
     * @param hxId 对方的环信ID
     * @param session
     * @return com.lw.thewar.vo.SearchlFriendInfo
     */
    @RequestMapping("get_info_by_hxid.do")
    @ResponseBody
    public ServerResponse getInfoByhxId(String hxId,HttpSession session){
        UserLoginInfo userLoginInfo = (UserLoginInfo) session.getAttribute(Const.CURRENT_USER);
        if(userLoginInfo == null){
            return ServerResponse.createByErrorMessage("当前没有登录用户");
        }

        try {
            return userService.getInfoByhxId(userLoginInfo.getUserID(),hxId,userLoginInfo.getHxUserName());
        } catch (IOException e) {
            return ServerResponse.createByErrorMessage("网络异常,请重试");
        }


    }

    /**
     *创建一个群组
     * @param userId  当前登录的用户ID
     * @param session
     * @param groupname  群名
     * @param desc       群详情
     * @param isPublic   是否公开
     * @param maxusers   最大成员数
     * @param isApproval 加群是否需要通过
     * @return  String
     * @throws IOException
     */
    @RequestMapping("create_chat_group.do")
    @ResponseBody
    public ServerResponse createChatGroup(@RequestParam(value = "userId",required = true) Integer userId,
                                          HttpSession session,
                                          @RequestParam(value = "groupname",required = true) String groupname,
                                          @RequestParam(value = "desc",required = false,defaultValue = "暂无群简介") String desc,
                                          @RequestParam(value = "isPublic",required = false,defaultValue = "true") boolean isPublic,
                                          @RequestParam(value = "maxusers",required = true)Integer maxusers,
                                          @RequestParam(value = "isApproval",required = false,defaultValue = "false")boolean isApproval) throws IOException {
        UserLoginInfo userLoginInfo = (UserLoginInfo) session.getAttribute(Const.CURRENT_USER);
        if(userId == null || groupname == null || desc == null || maxusers == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        if(userLoginInfo == null){
            return ServerResponse.createByErrorMessage("当前没有登录用户");
        }
        if(!userId.equals(userLoginInfo.getUserID()))
        {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        try {
            return userService.createChatGroup(userLoginInfo.getUserID(),groupname,desc,isPublic,maxusers,isApproval,userLoginInfo.getHxUserName());
        } catch (IOException e) {
            return ServerResponse.createByErrorMessage("网络异常");
        }
    }

    /**
     * 通过群ID获取群的详情资料
     * @param session
     * @param userId   当前的用户Id
     * @param groupId  群ID
     * @return com.lw.thewar.vo.ChatGroupInfo
     */
    @RequestMapping("get_chat_group_info.do")
    @ResponseBody
    public ServerResponse getChatGroupInfo(HttpSession session,Integer userId,Long groupId){
        UserLoginInfo userLoginInfo = (UserLoginInfo) session.getAttribute(Const.CURRENT_USER);
        if(groupId == null || userId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        if(userLoginInfo == null){
            return ServerResponse.createByErrorMessage("当前没有登录用户");
        }
        if(!userId.equals(userLoginInfo.getUserID()))
        {
            return ServerResponse.createByErrorMessage("参数错误");
        }

        try {
            return userService.getChatGroupInfo(userLoginInfo.getUserID(),groupId,userLoginInfo.getHxUserName());
        } catch (IOException e) {
            return ServerResponse.createByErrorMessage("网络异常,请重试");
        }


    }

    /**
     * 添加好友接口
     * @param session
     * @param hxUserName 要添加人的环信用户名
     * @param userId
     * @return String
     */
    @RequestMapping("add_friend.do")
    @ResponseBody
    public ServerResponse addFriend(HttpSession session,String hxUserName,Integer userId){
        UserLoginInfo userLoginInfo = (UserLoginInfo) session.getAttribute(Const.CURRENT_USER);
        if(userLoginInfo == null){
            return ServerResponse.createByErrorMessage("当前没有登录用户");
        }
        System.out.println("-------------------------------" + userId + "-----------" + hxUserName);
        if(!userId.equals(userLoginInfo.getUserID())){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        try {
            return userService.addFriend(hxUserName,userId,userLoginInfo.getHxUserName());
        } catch (IOException e) {
            return ServerResponse.createByErrorMessage("网络异常");
        }

    }

    /**
     * 删除好友
     * @param session
     * @param hxUserName 要删除的好友的环信ID
     * @param userId     当前的登录用户ID
     * @return  String
     */
    @RequestMapping("del_friend.do")
    @ResponseBody
    public ServerResponse delFriend(HttpSession session,String hxUserName,Integer userId){
        UserLoginInfo userLoginInfo = (UserLoginInfo) session.getAttribute(Const.CURRENT_USER);
        if(userLoginInfo == null){
            return ServerResponse.createByErrorMessage("当前没有登录用户");
        }
        if(!userId.equals(userLoginInfo.getUserID())){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        try {
            return userService.delFriend(hxUserName,userId,userLoginInfo.getHxUserName());
        } catch (IOException e) {
            return ServerResponse.createByErrorMessage("网络异常");
        }

    }

    /**
     * 邀请好友入群的时候要拉取的好友列表 不包括已经在此群里的好友
     * @param session
     * @param userId    当前登录的用户ID
     * @param groupId   群ID
     * @return     List<FriendListInfo>
     */
    @RequestMapping("get_friend_list.do")
    @ResponseBody
    public ServerResponse getFriendList(HttpSession session,Integer userId,Long groupId){
        UserLoginInfo userLoginInfo = (UserLoginInfo) session.getAttribute(Const.CURRENT_USER);
        if(userLoginInfo == null){
            return ServerResponse.createByErrorMessage("当前没有登录用户");
        }
        if(!userId.equals(userLoginInfo.getUserID())){
            return ServerResponse.createByErrorMessage("参数错误");

        }

        try {
            return userService.getFriendList(userId,groupId,userLoginInfo.getHxUserName());
        } catch (IOException e) {
            return ServerResponse.createByErrorMessage("网络异常");
        }
    }

    /**
     * 申请加入群组接口
     * @param session
     * @param userId    当前登录的用户ID
     * @param groupId   群ID
     * @return  String
     */
    @RequestMapping("apply_add_group.do")
    @ResponseBody
    public ServerResponse applyAddChatGroup(HttpSession session,Integer userId,Long groupId){
        UserLoginInfo userLoginInfo = (UserLoginInfo) session.getAttribute(Const.CURRENT_USER);
        if(userLoginInfo == null){
            return ServerResponse.createByErrorMessage("当前没有登录用户");
        }
        if(!userId.equals(userLoginInfo.getUserID())){
            return ServerResponse.createByErrorMessage("参数错误");

        }
        try {
            return userService.applyAddChatGroup(userId,groupId,userLoginInfo.getHxUserName());
        } catch (IOException e) {
            return ServerResponse.createByErrorMessage("网络异常");
        }
    }

    /**
     * 邀请好友加群  最多60个人
     * @param session
     * @param userId     当前登录的用户ID
     * @param groupId    群ID
     * @param usersList  要邀请的好友的环信ID json字符串形式
     * @return String
     */
    @RequestMapping("invite_add_group.do")
    @ResponseBody
    public ServerResponse inviteAddChatGroup(HttpSession session,Integer userId,Long groupId,String usersList){
        UserLoginInfo userLoginInfo = (UserLoginInfo) session.getAttribute(Const.CURRENT_USER);
        if(userLoginInfo == null){
            return ServerResponse.createByErrorMessage("当前没有登录用户");
        }
        if(!userId.equals(userLoginInfo.getUserID())){
            return ServerResponse.createByErrorMessage("参数错误");

        }
        try {
            return userService.inviteAddChatGroup(userId,groupId,usersList,userLoginInfo.getHxUserName());
        } catch (IOException e) {
            return ServerResponse.createByErrorMessage("网络异常");
        }
    }

    /**
     * 退出群组或删除群组
     * @param session
     * @param userId    当前登录的用户ID
     * @param groupId   群ID
     * @return String
     */
    @RequestMapping("del_group.do")
    @ResponseBody
    public ServerResponse delGroup(HttpSession session,Integer userId,Long groupId) {
        UserLoginInfo userLoginInfo = (UserLoginInfo) session.getAttribute(Const.CURRENT_USER);
        if(userLoginInfo == null){
            return ServerResponse.createByErrorMessage("当前没有登录用户");
        }
        if(!userId.equals(userLoginInfo.getUserID())){
            return ServerResponse.createByErrorMessage("参数错误");

        }

        try {
            return userService.delGroup(userId,groupId,userLoginInfo.getHxUserName());
        } catch (IOException e) {
            return ServerResponse.createByErrorMessage("网络异常");
        }

    }

    /**
     * 获取当前的登录用户参加的群组
     * @param userId    当前登录的用户ID
     * @param session
     * @return  List<Map<String,Object>>
     */
    @RequestMapping("get_joined_groups.do")
    @ResponseBody
    public ServerResponse getJoinGroups(Integer userId,HttpSession session){
        UserLoginInfo userLoginInfo = (UserLoginInfo) session.getAttribute(Const.CURRENT_USER);
        if(userLoginInfo == null){
            return ServerResponse.createByErrorMessage("当前没有登录用户");
        }
        if(!userId.equals(userLoginInfo.getUserID())){
            return ServerResponse.createByErrorMessage("参数错误");
        }

        try {
            return userService.getJoinGroups(userId,userLoginInfo.getHxUserName());
        } catch (IOException e) {
            return ServerResponse.createByErrorMessage("网络异常");
        }

    }

    /**
     * 获取群组的所有成员
     * @param userId     当前登录ID
     * @param session
     * @param groupId    群组ID
     * @return
     */
    @RequestMapping("get_group_users.do")
    @ResponseBody
    public ServerResponse getGroupUsers(Integer userId,HttpSession session,Long groupId){
        UserLoginInfo userLoginInfo = (UserLoginInfo) session.getAttribute(Const.CURRENT_USER);
        if(userLoginInfo == null){
            return ServerResponse.createByErrorMessage("当前没有登录用户");
        }
        if(!userId.equals(userLoginInfo.getUserID())){
            return ServerResponse.createByErrorMessage("参数错误");
        }

        try {
            return userService.getGroupUsers(userLoginInfo.getHxUserName(),groupId);
        } catch (IOException e) {
            return ServerResponse.createByErrorMessage("网络异常");
        }

    }
    @RequestMapping("get_myauth.do")
    @ResponseBody
    public ServerResponse getMyAuth(Integer userId,HttpSession session){
        UserLoginInfo userLoginInfo = (UserLoginInfo) session.getAttribute(Const.CURRENT_USER);
        if(userLoginInfo == null){
            return ServerResponse.createByErrorMessage("当前没有登录用户");
        }
        if(!userId.equals(userLoginInfo.getUserID())){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return userService.getMyAuth(userId);
    }

    @RequestMapping("upload_album.do")
    @ResponseBody
    public ServerResponse uploadAlbum(MultipartFile file, HttpSession session, Integer userId) throws IOException {
        if(file == null || userId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        UserLoginInfo userLoginInfo = (UserLoginInfo) session.getAttribute(Const.CURRENT_USER);
        if(userLoginInfo == null){
            return ServerResponse.createByErrorMessage("当前没有登录用户");
        }
        if(!userId.equals(userLoginInfo.getUserID()))
        {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return fileService.uploadAlbum(file,"album",userId,userLoginInfo.getHxUserName());
    }

    @RequestMapping("del_album.do")
    @ResponseBody
    public ServerResponse deleAlbum(Integer userId,Integer index, HttpSession session){
        if(index == null || userId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        UserLoginInfo userLoginInfo = (UserLoginInfo) session.getAttribute(Const.CURRENT_USER);
        if(userLoginInfo == null){
            return ServerResponse.createByErrorMessage("当前没有登录用户");
        }
        if(!userId.equals(userLoginInfo.getUserID()))
        {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        try {
            return userService.delAlbum(userId,index);
        } catch (IOException e) {
            return ServerResponse.createByErrorMessage("网络异常");
        }
    }

















//    @RequestMapping("exit_group.do")
//    @ResponseBody
//    public ServerResponse exitGroup(HttpSession session,Integer userId,Long groupId){
//        UserLoginInfo userLoginInfo = (UserLoginInfo) session.getAttribute(Const.CURRENT_USER);
//        if(userLoginInfo == null){
//            return ServerResponse.createByErrorMessage("当前没有登录用户");
//        }
//        if(!userId.equals(userLoginInfo.getUserID())){
//            return ServerResponse.createByErrorMessage("参数错误");
//
//        }
//        try {
//            return userService.exitGroup(userId,groupId);
//        } catch (IOException e) {
//            return ServerResponse.createByErrorMessage("网络异常");
//        }
//    }
//
//    @RequestMapping("get_group_list.do")
//    @ResponseBody
//    public ServerResponse getGroupList(HttpSession session,Integer userId) {
//        UserLoginInfo userLoginInfo = (UserLoginInfo) session.getAttribute(Const.CURRENT_USER);
//        if(userLoginInfo == null){
//            return ServerResponse.createByErrorMessage("当前没有登录用户");
//        }
//        if(!userId.equals(userLoginInfo.getUserID())){
//            return ServerResponse.createByErrorMessage("参数错误");
//
//        }
//
//    }

//    @RequestMapping("test.do")
//    @ResponseBody
//    public ServerResponse test(Test test) {
//        System.out.println(test.getBri());
//        return ServerResponse.createBySuccess(test);
//    }
//
//
//    @org.junit.Test
//    public void test() {
//        String region = "河北/石家庄/新乐";
//        String[] diqu = region.split("/");
//        System.out.println(diqu[0]);
//        System.out.println(diqu[1]);
//        System.out.println(diqu[2]);
//
//    }


//    @org.junit.Test
//    public void test(){
//        Calendar calendar2 = Calendar.getInstance();
//        calendar2.setTimeInMillis(880387200000L);
//        int year = calendar2.get(Calendar.YEAR);
//        int month = calendar2.get(Calendar.MONTH);
//        int day = calendar2.get(Calendar.DAY_OF_MONTH);
//        int hour = calendar2.get(Calendar.HOUR_OF_DAY);//24小时制
////      int hour = calendar2.get(Calendar.HOUR);//12小时制
//        int minute = calendar2.get(Calendar.MINUTE);
//        int second = calendar2.get(Calendar.SECOND);
//        System.out.println(year + "年" + (month + 1) + "月" + day + "日"
//                + hour + "时" + minute + "分" + second + "秒");
//    }
    @org.junit.Test
    public void test(){
        String str1 = "a1456651";
        String str2 = "11456651";
        String c = str2.substring(0,1);

        System.out.println(c.matches("[0-9]"));
    }
}

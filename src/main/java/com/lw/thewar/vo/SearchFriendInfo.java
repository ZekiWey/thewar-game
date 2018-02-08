package com.lw.thewar.vo;

import com.lw.thewar.util.PropertiesUtil;

public class SearchFriendInfo {

    private Integer userId;
    private String hxmapAccount;
    private String name;
    private String headImage;
    private Integer gender;
    private Integer age;
    private String brithday;
    private String introduce;
    private String region;
    private Integer Friend;

    public String getBrithday() {
        return brithday;
    }

    public void setBrithday(String brithday) {
        this.brithday = brithday;
    }

    public SearchFriendInfo(String hxmapAccount, String name, String headImage, Integer gender, Integer age, String brithday, String introduce, String region) {
        this.hxmapAccount = hxmapAccount;
        this.name = name;
        this.headImage = PropertiesUtil.getProperty("ftp.servxer.http.prefi") + headImage;
        this.gender = gender;
        this.age = age;
        this.brithday = brithday;
        this.introduce = introduce;
        this.region = region;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getHxmapAccount() {
        return hxmapAccount;
    }

    public void setHxmapAccount(String hxmapAccount) {
        this.hxmapAccount = hxmapAccount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Integer getFriend() {
        return Friend;
    }

    public void setFriend(Integer friend) {
        Friend = friend;
    }

    //    头像
//    昵称
//    id
//    性别
//    年龄
//    地区
//    签名
//    环信id
//    是否是好友


}

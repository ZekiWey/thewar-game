package com.lw.thewar.vo;

import com.lw.thewar.util.PropertiesUtil;

public class SearchLikeFriendInfo {

    private String hxmapAccount;
    private String name;
    private String headImage;
    private String gender;
    private Integer age;


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHxmapAccount() {
        return hxmapAccount;
    }

    public void setHxmapAccount(String hxmapAccount) {
        this.hxmapAccount = hxmapAccount;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public SearchLikeFriendInfo(String hxmapAccount, String name, String headImage, String gender, Integer age) {
        this.hxmapAccount = hxmapAccount;
        this.name = name;
        this.headImage = PropertiesUtil.getProperty("ftp.servxer.http.prefi") + headImage;
        this.gender = gender;
        this.age = age;
    }
}

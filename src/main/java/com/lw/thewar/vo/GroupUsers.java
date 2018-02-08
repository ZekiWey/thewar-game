package com.lw.thewar.vo;

public class GroupUsers {
    private String hxmapAccount;
    private String name;
    private String gender;
    private String headImage;
    private Integer access;

    public GroupUsers(String name, String gender, String headImage) {
        this.name = name;
        this.gender = gender;
        this.headImage = headImage;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAccess() {
        return access;
    }

    public void setAccess(Integer access) {
        this.access = access;
    }
}

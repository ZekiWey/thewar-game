package com.lw.thewar.vo;

import com.lw.thewar.util.PropertiesUtil;

public class FriendListInfo {

    private String hxUserName;
    private String name;
    private String headImage;

    public FriendListInfo(String hxUserName, String name, String headImage) {
        this.hxUserName = hxUserName;
        this.name = name;
        this.headImage = PropertiesUtil.getProperty("ftp.servxer.http.prefi") + PropertiesUtil.getProperty("ftp.servxer.img.thumbnail") + headImage;
    }

    public String getHxUserName() {
        return hxUserName;
    }

    public void setHxUserName(String hxUserName) {
        this.hxUserName = hxUserName;
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
}

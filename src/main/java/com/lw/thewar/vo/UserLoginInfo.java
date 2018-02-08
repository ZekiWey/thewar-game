package com.lw.thewar.vo;

import java.io.Serializable;

public class UserLoginInfo implements Serializable{

    private Integer loginWay;
    private String loginAccount;
    private Integer userID;
    private String hxUserName;
    private String mapUserName;

    private Integer loginStatus;

    private String xAuthToken;

    public String getxAuthToken() {
        return xAuthToken;
    }
    public void setxAuthToken(String xAuthToken) {
        this.xAuthToken = xAuthToken;
    }
    public String getLoginAccount() {
        return loginAccount;
    }

    public void setLoginAccount(String loginAccount) {
        this.loginAccount = loginAccount;
    }

    public String getMapUserName() {
        return mapUserName;
    }

    public void setMapUserName(String mapUserName) {
        this.mapUserName = mapUserName;
    }

    public Integer getLoginWay() {
        return loginWay;
    }

    public void setLoginWay(Integer loginWay) {
        this.loginWay = loginWay;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getHxUserName() {
        return hxUserName;
    }

    public void setHxUserName(String hxUserName) {
        this.hxUserName = hxUserName;
    }

    public Integer getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(Integer loginStatus) {
        this.loginStatus = loginStatus;
    }
}

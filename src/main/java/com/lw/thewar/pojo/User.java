package com.lw.thewar.pojo;

import java.util.Date;

public class User {
    private Integer id;

    private Integer status;

    private Integer identityType;

    private String identifiter;

    private String password;

    private Integer pluginsId;

    private Integer authsStatus;

    private Date createTime;

    private Date updateTime;

    public User(Integer id, Integer status, Integer identityType, String identifiter, String password, Integer pluginsId, Integer authsStatus, Date createTime, Date updateTime) {
        this.id = id;
        this.status = status;
        this.identityType = identityType;
        this.identifiter = identifiter;
        this.password = password;
        this.pluginsId = pluginsId;
        this.authsStatus = authsStatus;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public User() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIdentityType() {
        return identityType;
    }

    public void setIdentityType(Integer identityType) {
        this.identityType = identityType;
    }

    public String getIdentifiter() {
        return identifiter;
    }

    public void setIdentifiter(String identifiter) {
        this.identifiter = identifiter == null ? null : identifiter.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public Integer getPluginsId() {
        return pluginsId;
    }

    public void setPluginsId(Integer pluginsId) {
        this.pluginsId = pluginsId;
    }

    public Integer getAuthsStatus() {
        return authsStatus;
    }

    public void setAuthsStatus(Integer authsStatus) {
        this.authsStatus = authsStatus;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
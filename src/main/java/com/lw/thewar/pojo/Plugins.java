package com.lw.thewar.pojo;

import java.util.Date;

public class Plugins {
    private Integer id;

    private String hxmapAccount;

    private Date createTime;

    public Plugins(Integer id, String hxmapAccount, Date createTime) {
        this.id = id;
        this.hxmapAccount = hxmapAccount;
        this.createTime = createTime;
    }

    public Plugins() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHxmapAccount() {
        return hxmapAccount;
    }

    public void setHxmapAccount(String hxmapAccount) {
        this.hxmapAccount = hxmapAccount == null ? null : hxmapAccount.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
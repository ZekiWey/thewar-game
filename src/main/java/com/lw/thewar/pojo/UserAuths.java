package com.lw.thewar.pojo;

import java.util.Date;

public class UserAuths {
    private Integer id;

    private String jhId;

    private Integer userId;

    private String name;

    private Integer gender;

    private String provinces;

    private String city;

    private String headImage;

    private Date birthday;

    private String introduce;

    private String album;

    private Date createTime;

    private Date updateTime;

    public UserAuths(Integer id, String jhId, Integer userId, String name, Integer gender, String provinces, String city, String headImage, Date birthday, String introduce, String album, Date createTime, Date updateTime) {
        this.id = id;
        this.jhId = jhId;
        this.userId = userId;
        this.name = name;
        this.gender = gender;
        this.provinces = provinces;
        this.city = city;
        this.headImage = headImage;
        this.birthday = birthday;
        this.introduce = introduce;
        this.album = album;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public UserAuths() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getJhId() {
        return jhId;
    }

    public void setJhId(String jhId) {
        this.jhId = jhId == null ? null : jhId.trim();
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getProvinces() {
        return provinces;
    }

    public void setProvinces(String provinces) {
        this.provinces = provinces == null ? null : provinces.trim();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage == null ? null : headImage.trim();
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce == null ? null : introduce.trim();
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album == null ? null : album.trim();
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
package com.lw.thewar.vo;

import com.google.common.collect.Lists;

import java.util.List;

public class MyAuth {
    private Integer userId;
    private String name;
    private Integer gender;
    private String region;
    private String headImage;
    private Integer age;
    private String birthday;
    private String introduce;
    private String album;
    private List<String> albumlist;

    public MyAuth(Integer userId, String name, Integer gender, String region, String headImage, Integer age, String birthday, String introduce, String album) {
        this.userId = userId;
        this.name = name;
        this.gender = gender;
        this.region = region;
        this.headImage = headImage;
        this.age = age;
        this.birthday = birthday;
        this.introduce = introduce;
        this.album = album;
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
        this.name = name;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public List<String> getAlbumlist() {
        return albumlist;
    }

    public void setAlbumlist(List<String> albumlist) {
        this.albumlist = albumlist;
    }
}

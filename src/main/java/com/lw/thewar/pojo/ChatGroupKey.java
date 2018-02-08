package com.lw.thewar.pojo;

public class ChatGroupKey {
    private Integer id;

    private Integer ownerId;

    public ChatGroupKey(Integer id, Integer ownerId) {
        this.id = id;
        this.ownerId = ownerId;
    }

    public ChatGroupKey() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }
}
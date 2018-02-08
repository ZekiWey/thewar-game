package com.lw.thewar.dao;

import com.lw.thewar.pojo.ChatGroup;
import com.lw.thewar.pojo.ChatGroupKey;

public interface ChatGroupMapper {
    int deleteByPrimaryKey(ChatGroupKey key);

    int insert(ChatGroup record);

    int insertSelective(ChatGroup record);

    ChatGroup selectByPrimaryKey(ChatGroupKey key);

    int updateByPrimaryKeySelective(ChatGroup record);

    int updateByPrimaryKey(ChatGroup record);

    String selectHeadImageByGroupId(Long groupId);

    Integer selectOwnerByGroupId(Long groupId);

    int deleteByGroupId(Long groupId);

}
package com.lw.thewar.dao;

import com.lw.thewar.pojo.Plugins;

import java.util.List;

public interface PluginsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Plugins record);

    int insertSelective(Plugins record);

    Plugins selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Plugins record);

    int updateByPrimaryKey(Plugins record);

    String selectPlugins(Integer plugins_id);

    int selectHXUsername(String hxUsername);


    String selectHXUsernameByUserId(Integer userId);
}
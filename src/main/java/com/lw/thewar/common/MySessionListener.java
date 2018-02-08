package com.lw.thewar.common;

import com.lw.thewar.redis.RedisDao;
import com.lw.thewar.util.HX_RestUtil;
import com.lw.thewar.vo.UserLoginInfo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.io.IOException;

public class MySessionListener implements HttpSessionListener {

    @Autowired
    private RedisDao redisDao;

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        System.out.println("------------------------------创建成功---------------------------");
        HttpSession session =  httpSessionEvent.getSession();
        System.out.println(session.getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {

        System.out.println("------------------------------销毁成功---------------------------");
        HttpSession session =  httpSessionEvent.getSession();
        String hxUserName = redisDao.getSSKV(session.getId());
        if(null == hxUserName){
            return;
        }
        try {
            HX_RestUtil.disconnect(hxUserName);
        } catch (IOException e) {
            System.out.println("异常");
        }
        redisDao.del(session.getId());
    }
}

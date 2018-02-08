package com.lw.thewar.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lw.thewar.pojo.User;
import org.junit.Test;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;

import java.io.IOException;
import java.util.Date;

public class Main {

    public ServerResponse<LoginInfo> testLogin(){
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setUsername("aaa");
        loginInfo.setGender("男");

        return ServerResponse.createBySuccess(loginInfo);
    }

    public String testUser() throws JsonProcessingException {
        UserInfo userInfo = new UserInfo();
        userInfo.setName("aaa");
        userInfo.setBri(new Date());

        ServerResponse serverResponse =  ServerResponse.createBySuccess("aaa",userInfo);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(serverResponse);

        return  json;

    }


    @Test
    public void a() throws JsonProcessingException {
        System.out.println(testUser());
    }



    @Test
    public void test1() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ServerResponse serverResponse = objectMapper.readValue(testUser(),ServerResponse.class);

        System.out.println(serverResponse.getMsg());

        System.out.println(serverResponse.getStatus());

        UserInfo userInfo = (UserInfo) serverResponse.getData();

        System.out.println(userInfo);

    }

}

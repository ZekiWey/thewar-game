package com.lw.thewar.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lw.thewar.common.Const;
import com.lw.thewar.hxpojo.HXToken;
import com.lw.thewar.redis.RedisDao;
import com.lw.thewar.vo.ChatGroupInfo;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.junit.Test;
import org.omg.CORBA.portable.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class HX_RestUtil {



    private final static String ORG_NAME="1140170706178138";
    private final static String APP_NAME="yuezhan";
    private final static String CLIENT_ID="YXA6J22MoPN6EeexbH0aGc8WPQ";
    private final static String CLIENT_SECRET="YXA6egANex5XMitZNOX1kxWPEBfJvds";
    private final static String HX_PASSWORD="qmyz88888888";

    private final static String URL_DOMAIN="http://a1.easemob.com/";

    private final static String url = new StringBuffer().append(URL_DOMAIN).append(ORG_NAME).append('/').append(APP_NAME).append('/').toString();

    private static HX_RestUtil hx_restUtil;

    @Autowired
    private RedisDao redisDao;

    @PostConstruct
    public void init() {
        hx_restUtil = this;
        hx_restUtil.redisDao = this.redisDao;
    }

    /*    @Test
    public void test(){
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        RedisDao redisDao = (RedisDao) context.getBean("redisDao");

        System.out.println(redisDao.getSSKV("name"));

    }*/

    public static String getHXToken() throws IOException {
        Client client =  Client.create();


        Map<String,String> map = Maps.newHashMap();
        map.put("grant_type","client_credentials");
        map.put("client_id",CLIENT_ID);
        map.put("client_secret",CLIENT_SECRET);

        ObjectMapper mapper =  new ObjectMapper();
        String json = mapper.writeValueAsString(map);

        System.out.println(json);

        WebResource webResource = client.resource(url+"token");
        ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,json);
        if(clientResponse.getStatus() == 200){
            String jsonStr = clientResponse.getEntity(String.class);
            mapper = new ObjectMapper();
            HXToken hxToken = mapper.readValue(jsonStr,HXToken.class);
            System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-获取到token");
            hx_restUtil.redisDao.setSSKV(Const.HX_ADMIN_TOKEN,hxToken.getAccess_token(),hxToken.getExpires_in(), TimeUnit.SECONDS);
            System.out.println("-------------------------" + hxToken.getExpires_in());
            return hxToken.getAccess_token();

        }
        return null;
    }

    public static Boolean regist_user(String username) throws IOException {

        String hx_token = hx_restUtil.redisDao.getSSKV(Const.HX_ADMIN_TOKEN);

        if(null==hx_token){
            hx_token = getHXToken();
        }
        Map<String,String> usermap = Maps.newHashMap();
        usermap.put("username",username);
        usermap.put("password",HX_PASSWORD);

        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(usermap);


        Client client =  Client.create();
        WebResource webResource = client.resource(url+"users");


        ClientResponse clientResponse = webResource.header("Accept",MediaType.APPLICATION_JSON).header("Authorization","Bearer "+hx_token).type(MediaType.APPLICATION_JSON).post(ClientResponse.class,body);
        if(clientResponse.getStatus() == 200){
            return true;
        }
        return false;
    }

    public static Boolean updateName(String name,String username) throws IOException{
        String hx_token = hx_restUtil.redisDao.getSSKV(Const.HX_ADMIN_TOKEN);

        if(null==hx_token){
            hx_token = getHXToken();
        }
        Map<String,String> usermap = Maps.newHashMap();
        usermap.put("nickname",name);
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(usermap);
        Client client =  Client.create();
        WebResource webResource = client.resource(url+"users"+"/"+username);
        ClientResponse clientResponse = webResource.header("Accept",MediaType.APPLICATION_JSON).header("Authorization","Bearer "+hx_token).put(ClientResponse.class,body);
        System.out.println(clientResponse.getStatus());
        System.out.println(clientResponse);
        String s = clientResponse.getEntity(String.class);
        System.out.println(s);
        if(clientResponse.getStatus() == 200){
            return true;
        }
        return false;
    }

    public static Long createChatGroups(String groupname,String desc,boolean publi,Integer maxusers,boolean approval,String owner) throws IOException{
        String hx_token = hx_restUtil.redisDao.getSSKV(Const.HX_ADMIN_TOKEN);

        if(null==hx_token){
            hx_token = getHXToken();
        }
        Map<String,Object> chatGroupsMap = Maps.newHashMap();
        chatGroupsMap.put("groupname",groupname);
        chatGroupsMap.put("desc",desc);
        chatGroupsMap.put("public",publi);
        chatGroupsMap.put("maxusers",maxusers);
        chatGroupsMap.put("approval",approval);
        chatGroupsMap.put("owner",owner);
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(chatGroupsMap);
        Client client =  Client.create();
        WebResource webResource = client.resource(url+"chatgroups");
        ClientResponse clientResponse = webResource.header("Accept",MediaType.APPLICATION_JSON).header("Authorization","Bearer "+hx_token).post(ClientResponse.class,body);
        System.out.println(clientResponse.getStatus());
        System.out.println(clientResponse);
        String s = clientResponse.getEntity(String.class);
        System.out.println(s);
        if(clientResponse.getStatus() == 200){
            Map<String,Object>  hxResp = JsonUtil.parseObject(s,Map.class);
            Map<String,String>  data = (Map<String, String>) hxResp.get("data");
            return Long.valueOf(data.get("groupid"));
        }

        return null;
    }

    public static ChatGroupInfo getGroupInfo(Long groupId,String hxUserName) throws IOException{
        String hx_token = hx_restUtil.redisDao.getSSKV(Const.HX_ADMIN_TOKEN);

        if(null==hx_token){
            hx_token = getHXToken();
        }

        Client client =  Client.create();
        WebResource webResource = client.resource(url+"chatgroups/"+groupId);
        ClientResponse clientResponse = webResource.header("Accept",MediaType.APPLICATION_JSON).header("Authorization","Bearer "+hx_token).get(ClientResponse.class);
        String s = clientResponse.getEntity(String.class);
        System.out.println(s);
        if(clientResponse.getStatus() == 200){
            Map<String,String>  hxResp = JsonUtil.parseObject(s,Map.class);

            String dataStr = JsonUtil.toJSONString(hxResp.get("data"));

            dataStr = dataStr.substring(1,dataStr.length()-1);

            Map<String,Object> data = JsonUtil.parseObject(dataStr,Map.class);

            System.out.println(data.toString());

            ChatGroupInfo groupInfo = new ChatGroupInfo();
            groupInfo.setGroupId(Long.valueOf(data.get("id").toString()));
            groupInfo.setGroupName(data.get("name").toString());
            groupInfo.setGroupDesc(data.get("description").toString());
            groupInfo.setMaxusers(Integer.valueOf(data.get("maxusers").toString()));
            groupInfo.setMembersCount(Integer.valueOf(data.get("affiliations_count").toString()));
            if(hxUserName.equals(data.get("owner"))){
                groupInfo.setOwner(true);
            }
            else{
                groupInfo.setOwner(false);
            }
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            date.setTime(Long.valueOf(data.get("created").toString()));
            groupInfo.setCreatTime(simpleDateFormat.format(date));

            return groupInfo;
        }

        return null;
    }

    public static Integer isFriend(String myHxUserName,String hxUserName) throws IOException{

        String hx_token = hx_restUtil.redisDao.getSSKV(Const.HX_ADMIN_TOKEN);

        if(null==hx_token){
            hx_token = getHXToken();
        }

        Client client =  Client.create();
        WebResource webResource = client.resource(url+"users/" + myHxUserName +"/contacts/users");
        ClientResponse clientResponse = webResource.header("Accept",MediaType.APPLICATION_JSON).header("Authorization","Bearer "+hx_token).get(ClientResponse.class);
        String s = clientResponse.getEntity(String.class);
        System.out.println(s);
        if(clientResponse.getStatus() == 200){
            Map<String,String>  hxResp = JsonUtil.parseObject(s,Map.class);

            System.out.println("hxResp----------------------" + hxResp.toString());

            String dataStr = JsonUtil.toJSONString(hxResp.get("data"));

            System.out.println("dataStr---------------------" + dataStr);

            List<String> data = JsonUtil.parseObject(dataStr,List.class);

            System.out.println(hxUserName);

            System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + data.contains(hxUserName));

            if(CollectionUtils.isEmpty(data)){
                return 2;
            }

            return data.contains(hxUserName)? 1:2;
        }

        return 2;
    }

    public static boolean isInGroup(String hxUsername,Long groupId) throws IOException{
        String hx_token = hx_restUtil.redisDao.getSSKV(Const.HX_ADMIN_TOKEN);
        if (null == hx_token) {
            hx_token = getHXToken();
        }
        Client client = Client.create();
        WebResource webResource = client.resource(url + "chatgroups/" + groupId + "/users");
        ClientResponse clientResponse = webResource.header("Accept", MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + hx_token).get(ClientResponse.class);
        String s = clientResponse.getEntity(String.class);
        System.out.println(s);
        if (clientResponse.getStatus() == 200) {
            Map<String, String> hxResp = JsonUtil.parseObject(s, Map.class);/*            ystem.out.println("hxResp----------------------" + hxResp.toString());*/
            String dataStr = JsonUtil.toJSONString(hxResp.get("data"));/*            System.out.println("dataStr---------------------" + dataStr);*/
            List<Map<String, String>> data = JsonUtil.parseObject(dataStr, List.class);
            boolean flag = false;
            hxUsername = "{member=" + hxUsername + "}";
            for (Map<String, String> map : data) if (map.toString().equals(hxUsername)) return true;
        }
        return false;
    }

    public static boolean addFriend(String myhxUsername,String friHxUsername) throws IOException{
        String hx_token = hx_restUtil.redisDao.getSSKV(Const.HX_ADMIN_TOKEN);
        if (null == hx_token) {
            hx_token = getHXToken();
        }
        Client client = Client.create();
        WebResource webResource = client.resource(url + "users/" + myhxUsername + "/contacts/users/"+friHxUsername);
        ClientResponse clientResponse = webResource.header("Accept", MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + hx_token).post(ClientResponse.class);
        String s = clientResponse.getEntity(String.class);
        System.out.println(s);
        if (clientResponse.getStatus() == 200) {
            return true;
        }
        return false;
    }
    public static boolean delFriend(String myhxUsername,String friHxUsername) throws IOException{
        String hx_token = hx_restUtil.redisDao.getSSKV(Const.HX_ADMIN_TOKEN);
        if (null == hx_token) {
            hx_token = getHXToken();
        }
        Client client = Client.create();
        WebResource webResource = client.resource(url + "users/" + myhxUsername + "/contacts/users/"+friHxUsername);
        ClientResponse clientResponse = webResource.header("Accept", MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + hx_token).delete(ClientResponse.class);
        String s = clientResponse.getEntity(String.class);
        System.out.println(s);
        if (clientResponse.getStatus() == 200) {
            return true;
        }
        return false;
    }

    public static List<String> getGroupUsers(Long groupId) throws IOException{
        String hx_token = hx_restUtil.redisDao.getSSKV(Const.HX_ADMIN_TOKEN);
        if (null == hx_token) {
            hx_token = getHXToken();
        }
        Client client = Client.create();
        WebResource webResource = client.resource(url + "chatgroups/" + groupId + "/users");
        ClientResponse clientResponse = webResource.header("Accept", MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + hx_token).get(ClientResponse.class);
        String s = clientResponse.getEntity(String.class);
        System.out.println(s);
        if (clientResponse.getStatus() == 200) {
            Map<String, String> hxResp = JsonUtil.parseObject(s, Map.class);/*            ystem.out.println("hxResp----------------------" + hxResp.toString());*/
            String dataStr = JsonUtil.toJSONString(hxResp.get("data"));/*            System.out.println("dataStr---------------------" + dataStr);*/
            List<Map<String, String>> data = JsonUtil.parseObject(dataStr, List.class);

            List<String> usernames = Lists.newArrayList();
            for (Map<String, String> map : data) {
                System.out.println(map);
                usernames.add(map.get(map.keySet().iterator().next().toString()));
            }

            return usernames;
        }
        return null;
    }

    public static List<String> getFriendList(String myhxUsername,Long groupId) throws IOException{
        String hx_token = hx_restUtil.redisDao.getSSKV(Const.HX_ADMIN_TOKEN);
        if (null == hx_token) {
            hx_token = getHXToken();
        }
        Client client = Client.create();
        WebResource webResource = client.resource(url + "users/" + myhxUsername + "/contacts/users/");
        ClientResponse clientResponse = webResource.header("Accept", MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + hx_token).get(ClientResponse.class);
        String s = clientResponse.getEntity(String.class);
        System.out.println(s);
        if (clientResponse.getStatus() == 200) {
            Map<String,String>  hxResp = JsonUtil.parseObject(s,Map.class);

            System.out.println("hxResp----------------------" + hxResp.toString());

            String dataStr = JsonUtil.toJSONString(hxResp.get("data"));

            System.out.println("dataStr---------------------" + dataStr);

            List<String> data = JsonUtil.parseObject(dataStr,List.class);
            List<String> usernames =  getGroupUsers(groupId);

            for(int i=data.size()-1;i>=0;i--){
                if(usernames.contains(data.get(i))) {
                    System.out.println("1111111111111" + data.get(i));
                    data.remove(i);
                }
            }
            return data;
        }
        return null;
    }

    public static boolean applyChatGroup(Long groupId,List<String> hxUsernameList) throws IOException{
        String hx_token = hx_restUtil.redisDao.getSSKV(Const.HX_ADMIN_TOKEN);
        if (null == hx_token) {
            hx_token = getHXToken();
        }
        Client client = Client.create();
        WebResource webResource = client.resource(url + "chatgroups/" + groupId + "/users");
        Map<String,Object> map = Maps.newHashMap();
        map.put("usernames",hxUsernameList);
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(map);
        ClientResponse clientResponse = webResource.header("Accept", MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + hx_token).post(ClientResponse.class,body);
        String s = clientResponse.getEntity(String.class);
        System.out.println(s);
        if (clientResponse.getStatus() == 200) {
            return true;
        }
        return false;
    }

    public static boolean removeChatGroup(Long groupId,String hxUsername) throws IOException{
        String hx_token = hx_restUtil.redisDao.getSSKV(Const.HX_ADMIN_TOKEN);
        if (null == hx_token) {
            hx_token = getHXToken();
        }
        Client client = Client.create();
        WebResource webResource = client.resource(url + "chatgroups/" + groupId + "/users/" + hxUsername);
        ClientResponse clientResponse = webResource.header("Accept", MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + hx_token).delete(ClientResponse.class);
        String s = clientResponse.getEntity(String.class);
        System.out.println(s);
        if (clientResponse.getStatus() == 200) {
            return true;
        }
        return false;
    }

    public static boolean deleteChatGroup(Long groupId) throws IOException{
        String hx_token = hx_restUtil.redisDao.getSSKV(Const.HX_ADMIN_TOKEN);
        if (null == hx_token) {
            hx_token = getHXToken();
        }
        Client client = Client.create();
        WebResource webResource = client.resource(url + "chatgroups/" + groupId);
        ClientResponse clientResponse = webResource.header("Accept", MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + hx_token).delete(ClientResponse.class);
        String s = clientResponse.getEntity(String.class);
        System.out.println(s);
        if (clientResponse.getStatus() == 200) {
            return true;
        }
        return false;
    }

    @Test
    public void updateChatGroup() throws JsonProcessingException {
         String ORG_NAME="1140170706178138";
         String APP_NAME="yuezhan";
         String URL_DOMAIN="http://a1.easemob.com/";

         String url = new StringBuffer().append(URL_DOMAIN).append(ORG_NAME).append('/').append(APP_NAME).append('/').toString();
         String hx_token = "YWMtwKybzgNoEeivkIWEFux3nAAAAAAAAAAAAAAAAAAAAAEnbYyg83oR57FsfRoZzxY9AgMAAAFhN-BjaQBPGgCnsqFyC1Aa9JOwYp1xj8eJQjEn-fQtPeSUoheulwMvXg";
         String groupId = "39551372427265";
        Map<String,Object> map = Maps.newHashMap();
        map.put("groupname","亚亚亚亚亚亚");
        map.put("description","1111111");
        map.put("maxusers",500);

        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(map);
        Client client = Client.create();
        WebResource webResource = client.resource(url + "chatgroups/" + groupId);
        ClientResponse clientResponse = webResource.header("Accept", MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + hx_token).put(ClientResponse.class,body);
        String s = clientResponse.getEntity(String.class);
        if (clientResponse.getStatus() == 200) {
            System.out.println("aaaaaa");
        }
    }

    public static List<Map<String,Object>> getUserJoinGroups(String hxUserName) throws IOException {
        String hx_token = hx_restUtil.redisDao.getSSKV(Const.HX_ADMIN_TOKEN);
        if (null == hx_token) {
            hx_token = getHXToken();
        }
        Client client = Client.create();
        WebResource webResource = client.resource(url + "users/" + hxUserName + "/joined_chatgroups");

        ClientResponse clientResponse = webResource.header("Accept", MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + hx_token).get(ClientResponse.class);
        String s = clientResponse.getEntity(String.class);
        if (clientResponse.getStatus() == 200) {
            Map<String, String> hxResp = JsonUtil.parseObject(s, Map.class);/*            ystem.out.println("hxResp----------------------" + hxResp.toString());*/
            String dataStr = JsonUtil.toJSONString(hxResp.get("data"));/*            System.out.println("dataStr---------------------" + dataStr);*/
            List<Map<String, Object>> data = JsonUtil.parseObject(dataStr, List.class);
            return data;
        }

        return null;
    }

    public static void disconnect(String hxUserName) throws IOException {
        String hx_token = hx_restUtil.redisDao.getSSKV(Const.HX_ADMIN_TOKEN);
        if (null == hx_token) {
            hx_token = getHXToken();
        }
        Client client = Client.create();
        WebResource webResource = client.resource(url + "users/" + hxUserName + "/disconnect");

        ClientResponse clientResponse = webResource.header("Accept", MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + hx_token).get(ClientResponse.class);
    }

}

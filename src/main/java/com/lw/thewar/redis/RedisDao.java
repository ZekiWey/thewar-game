package com.lw.thewar.redis;

import com.google.common.collect.Maps;
import com.lw.thewar.common.UserLoginCount;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RedisDao {



    @Autowired
    private RedisTemplate redisTemplate;

    public void setSSKV(String key, String value, long timeout, TimeUnit unit){
        redisTemplate.opsForValue().set(key,value,timeout,unit);
    }
    public void set(String key, String value){
        redisTemplate.opsForValue().set(key,value);
    }

    public void del(String key){
        redisTemplate.delete(key);
    }

    public String getSSKV(String key){
        Object object = redisTemplate.opsForValue().get(key);
        if(null != object){
            return object.toString();
        }
        return null;
    }

    public void setHashSSKV(String key, Object value){
        redisTemplate.opsForHash().put("users",key,value);
    }

    public Object getHashSSKV(String key){
        return redisTemplate.opsForHash().get("users",key);
    }

    @Test
    public void test3(){
        UserLoginCount count = new UserLoginCount();
        count.setId(1);
        count.setHxAccount("dasjkdjaslda");
        count.setSessionId("dasdas-dasdasd-dasdasda-dasda");
        Map<String,String> map = Maps.newHashMap();
        map.put("id","1");
        map.put("hxAccount","dasdasfdhsfusd");
        map.put("sessionId","asdas-1sa1d5-das147-dsa413");
        ApplicationContext context = new ClassPathXmlApplicationContext("/spring-redis/spring-redis.xml");
        RedisTemplate redisTemplate = (RedisTemplate) context.getBean("redisTemplate");
        HashOperations<String,Object,Object> hashOperations = redisTemplate.opsForHash();
        hashOperations.putAll(map.get("id"),map);
    }
    @Test
    public void test4(){

        ApplicationContext context = new ClassPathXmlApplicationContext("/spring-redis/spring-redis.xml");
        RedisTemplate redisTemplate = (RedisTemplate) context.getBean("redisTemplate");
        HashOperations<String,Object,Object> hashOperations = redisTemplate.opsForHash();
        Map<String,String> map = redisTemplate.opsForHash().entries("1");
        System.out.println(map.toString());
    }


    @Test
    public void test(){
        ApplicationContext context = new ClassPathXmlApplicationContext("/spring-redis/spring-redis.xml");
        RedisTemplate<String,String> stringRedisTemplate = (RedisTemplate<String, String>) context.getBean("redisTemplate");
//        Map<Object, Object> map = Maps.newHashMap();
//        map = stringRedisTemplate.opsForHash().entries("spring:session:sessions:5160a0cc-a611-4a1b-ab0b-4e5ccbbbf65b");
//        System.out.println(map.toString());
        String h = "spring:session:sessions:3f0026bb-ae14-4af8-9260-29c0f92ca460" ;
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(h);
        System.out.println(map);
    }

    @Test
    public void test1(){
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-redis.xml");
        RedisTemplate<String,String> stringRedisTemplate = (RedisTemplate<String, String>) context.getBean("redisTemplate");
        System.out.println(stringRedisTemplate.opsForValue().get("nameabc"));

    }
}

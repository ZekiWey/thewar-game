package com.lw.thewar.pojo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.lw.thewar.util.FTPUtil;
import com.lw.thewar.util.HX_RestUtil;
import com.lw.thewar.util.JsonUtil;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Test {

    private String name;
    private Date bri;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @DateTimeFormat
    public Date getBri() {
        return bri;
    }

    public void setBri(Date bri) {
        this.bri = bri;
    }

    @org.junit.Test
    public void test() throws IOException {
        System.out.println(FTPUtil.delefile("6389c987f-b294-44e4-b20f-b95117a3d24e.jpg"));
    }

    @org.junit.Test
    public void test1(){
        String json = "{\n" +
                "  \"action\": \"post\",\n" +
                "  \"application\": \"276d8ca0-f37a-11e7-b16c-7d1a19cf163d\",\n" +
                "  \"uri\": \"http://a1.easemob.com/1140170706178138/yuezhan/chatgroups\",\n" +
                "  \"entities\": [],\n" +
                "  \"data\": {\n" +
                "    \"groupid\": \"39206145556485\"\n" +
                "  },\n" +
                "  \"timestamp\": 1516788294575,\n" +
                "  \"duration\": 0,\n" +
                "  \"organization\": \"1140170706178138\",\n" +
                "  \"applicationName\": \"yuezhan\"\n" +
                "}";


        Map<String,Object>  hxResp = JsonUtil.parseObject(json,Map.class);
        Map<String,String>  data = (Map<String, String>) hxResp.get("data");
        System.out.println(data.get("groupid"));

        Long l = Long.valueOf(data.get("groupid"));

        System.out.println(l+1);


    }

    @org.junit.Test
    public void test02() throws IOException {
        HX_RestUtil.isFriend("g171c3d3a36254a8","g171c3d3a36254a8");
    }

    @org.junit.Test
    public void test03(){
        String str = "dsadasdasdas.jpg";
        List<String> stringList = Lists.newArrayList(str.split(","));
        System.out.println(stringList.size());
        System.out.println(stringList.toString());
        stringList.remove(0);
        System.out.println(stringList.size());
    }

}

package com.lw.thewar.common;

/**
 * Created by Administrator on 2018/1/2.
 */
public class Const {

    public static final String CURRENT_USER = "currentUser";

    public static final String HX_ADMIN_TOKEN = "hx_admin_token";

    public enum LoginWay{

        PHONE_LOGIN(1,"手机"),
        WX_LOGIN(2,"QQ"),
        QQ_LOGIN(3,"微信");

        LoginWay(int code,String value){
            this.code = code;
            this.value = value;
        }
        private int code;
        private String value;

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }
    }

    public enum AccountLoginInfo{

        JX_MEMBER(0,"家豪会员"),
        MEMBER(1,"普通用户"),
        SEAL(3,"被禁封");

        AccountLoginInfo(int code,String value){
            this.code = code;
            this.value = value;
        }
        private int code;
        private String value;

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }
    }

}

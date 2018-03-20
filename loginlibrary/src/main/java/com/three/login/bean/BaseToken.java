package com.three.login.bean;

/**
 * Created by onetouch on 2017/11/21.
 */

public class BaseToken {

    private String access_token;

    private String openid;


    public String getAccessToken() {
        return access_token;
    }

    public void setAccessToken(String access_token) {
        this.access_token = access_token;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    @Override
    public String toString() {
        return "BaseToken{" + "access_token='" + access_token + '\'' + ", openid='" + openid + '\'' + '}';
    }
}

package com.three;

/**
 * Created by onetouch on 2017/11/21.
 */

public class ConfigAppKey {

    private String wxId = "wx5022d3c8ad81e74f";

    private String wxSecret = "db846ab731f65c89fe77c49ad3af5612";

    private String qqId = "101436153";

    private int aliPRIVATE_KEY;

    private String weiboId = "315110531";
        /*微博*/
  //  App Key：315110531
   // App Secret：16d7e57645e866c72e18559220b9258b

    private String weiboRedirectUrl = "https://api.weibo.com/oauth2/default.html";

    private String weiboScope = "email";

    public int getAliPRIVATE_KEY() {
        return aliPRIVATE_KEY;
    }

    public void setAliPRIVATE_KEY(int aliPRIVATE_KEY) {
        this.aliPRIVATE_KEY = aliPRIVATE_KEY;
    }

    public static ConfigAppKey instance() {
        return new ConfigAppKey();
    }

    public String getWeiboId() {
        return weiboId;
    }

    public void setWeiboId(String weiboId) {
        this.weiboId = weiboId;
    }

    public ConfigAppKey wxId(String id) {
        wxId = id;
        return this;
    }

    public String getQqId() {
        return qqId;
    }

    public void setQqId(String qqId) {
        this.qqId = qqId;
    }

    public ConfigAppKey wxSecret(String id) {
        wxSecret = id;
        return this;
    }


    public String getWxId() {
        return wxId;
    }

    public String getWxSecret() {
        return wxSecret;
    }


}

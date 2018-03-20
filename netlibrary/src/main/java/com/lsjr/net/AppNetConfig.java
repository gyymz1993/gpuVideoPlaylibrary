package com.lsjr.net;

/**
 * Created by Administrator on 2018/3/20.
 */

public class AppNetConfig{


    private static AppNetConfig appNetConfig=new AppNetConfig();

    public static AppNetConfig getInstance(){
        return appNetConfig;
    }

    private AppNetConfig(){}

    private Builder mbuider;

    public Builder getMbuider() {
        return mbuider;
    }

    public void setBuider(Builder buider) {
        mbuider = buider;
    }


    public static class Builder {
        private String baseUrl;
        private String shareUrl;
        private String webUrl;

        public String getWebUrl() {
            return webUrl;
        }

        public Builder setWebUrl(String webUrl) {
            this.webUrl = webUrl;
            return this;
        }


        public String getBaseUrl() {
            return baseUrl;
        }

        public Builder setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public String getShareUrl() {
            return shareUrl;
        }

        public Builder setShareUrl(String shareUrl) {
            this.shareUrl = shareUrl;
            return this;
        }

    }

}

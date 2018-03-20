package com.lsjr.bean;

/**
 * Created by zjl_d on 2017/5/3.
 * 用于构造reftrofit的builder
 */
public class EncryptBean {

    private String string ;
    private String random;
    private String sign;
    private int priority;
    private String destination;

    public EncryptBean(String string, String random, String sign, int priority, String destination) {
        this.string = string;
        this.random = random;
        this.sign = sign;
        this.priority = priority;
        this.destination = destination;
    }


    public EncryptBean() {
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public String getRandom() {
        return random;
    }

    public void setRandom(String random) {
        this.random = random;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}

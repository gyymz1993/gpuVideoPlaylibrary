package com.lsjr.bean;

/**
 * Created by admin on 2016/8/4.
 */
public class EncryptReturnBean {

    public String string;
    public String sign;
    public String random;


    public EncryptReturnBean(String random, String sign, String string) {
        this.random = random;
        this.sign = sign;
        this.string = string;
    }


    public EncryptReturnBean() {
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

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return "EncryptReturnBean{" +
                "random='" + random + '\'' +
                ", string='" + string + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}

package com.three.pay.bean;

import java.io.Serializable;

public final class PayInfo implements Serializable {

    private static final String TAG = PayInfo.class.getName();

	private static final long serialVersionUID = 1L;


    private int orderId;
    private String orderNo;

    private String prepayId;
    private String nonceStr;
    private String timeStamp;
    private String sign;

    public static String getTAG() {
        return TAG;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPrepayId() {
        return prepayId;
    }

    public void setPrepayId(String prepayId) {
        this.prepayId = prepayId;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "PayInfo{" + "orderId=" + orderId + ", orderNo='" + orderNo + '\'' + ", prepayId='" + prepayId + '\'' + ", nonceStr='" + nonceStr + '\'' + ", timeStamp='" + timeStamp + '\'' + ", sign='" + sign + '\'' + '}';
    }
}
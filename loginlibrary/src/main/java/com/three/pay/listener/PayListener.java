package com.three.pay.listener;

/**
 * Created by onetouch on 2017/11/22.
 */

public interface PayListener {
    void paySuccess();

    void payFailure(Exception e);

    void userCancel();
}

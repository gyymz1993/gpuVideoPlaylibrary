package com.three.pay.instance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.three.pay.bean.PayInfo;
import com.three.pay.listener.PayListener;

/**
 * Created by onetouch on 2017/11/22.
 */

public abstract class PayInstance {
    public abstract void handleResult(int requestCode, int resultCode, Intent data);

    public abstract boolean isInstall(Context context);

    public abstract void doPay(Activity activity, PayListener listener, PayInfo fetchUserInfo);

}

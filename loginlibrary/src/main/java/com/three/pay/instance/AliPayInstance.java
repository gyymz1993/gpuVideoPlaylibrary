package com.three.pay.instance;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.three.pay.aliutils.PayResult;
import com.three.pay.bean.PayInfo;
import com.three.pay.listener.PayListener;

import java.util.Map;

/**
 * Created by onetouch on 2017/11/22.
 */

public class AliPayInstance extends PayInstance {
    private PayListener mPayListener;
    private Activity activity;
    private static final int SDK_PAY_FLAG = 1;
    private PayInfo mPayInfo;

    public AliPayInstance(Activity activity, PayInfo payInfo,PayListener listener) {
        mPayListener = listener;
    }


    @Override
    public void doPay(final Activity activity, PayListener listener, PayInfo info) {
        this.activity = activity;
        this.mPayListener = listener;
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(activity);
                Map<String, String> result = alipay.payV2("", true);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                alipayHandler.sendMessage(msg);
            }
        };
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    @Override
    public void handleResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public boolean isInstall(Context context) {
        return false;
    }


    @SuppressLint("HandlerLeak")
    private Handler alipayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    String resultInfo = payResult.getResult();
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };
}

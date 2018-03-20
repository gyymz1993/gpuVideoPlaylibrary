package com.lsjr.callback;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lsjr.bean.Encrypt;
import com.lsjr.bean.EncryptReturnBean;
import com.lsjr.bean.Result;

public abstract class EncryBeanCallBack extends BaseCallBack<EncryptReturnBean> {

    @Override
    public void onNext(EncryptReturnBean response) {
        final String dataString = Encrypt.transEncrytionDataStringRetrofit(response);
        JSONObject jsonObject = JSON.parseObject(dataString);
        int code = jsonObject.getIntValue(Result.ONSUCCESS);
        final String msg = jsonObject.getString(Result.RESULT_MSG);
        if (code == Result.CODE_SUCCESS) {
            final String data = jsonObject.getString(Result.DATA);
            Log.e("StringCallBack", "成功返回data---->:" + data);
            mDelivery.post(new Runnable() {
                @Override
                public void run() {
                    Log.e("StringCallBack", "解密数据" + data);
                    onSuccess(data);
                }
            });
        } else {
            mDelivery.post(new Runnable() {
                @Override
                public void run() {
                    onXError(msg);
                    Log.e("TODO", msg + "");
                }
            });
        }
    }


    @Override
    public void onError(final Throwable e) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                Log.e("TODO", e.getMessage() + "");
                if (e.getMessage() == null) {
                    onXError("服务器不舒服");
                } else if (e.getMessage().contains("HTTP")) {
                    onXError("服务器不舒服");
                } else if (e.getMessage().contains("Failed to connect to")) {
                    onXError("请检查网络");
                } else if (e.getMessage().contains("Unable to resolve host")) {
                    // onXError(e.getMessage());
                    onXError("请检查网络");
                } else if (e.getMessage().contains("itbour.onetouchshow.App cannot be cast")) {
                    onXError("请检查网络");
                } else {
                    onXError("请检查网络");
                }

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCompleted() {
    }

    protected abstract void onXError(String exception);


    protected abstract void onSuccess(String response);


}
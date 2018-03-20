package com.lsjr.callback;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.lsjr.bean.EncryptBean;
import com.lsjr.bean.EncryptReturnBean;
import com.lsjr.bean.Result;
import com.lsjr.bean.Encrypt;

public abstract class StringCallBack extends BaseCallBack<String> {

    @Override
    public void onNext(String response) {
        Log.e("StringCallBack", "网络请求成功" + response);
        response = response.replace("null", "\"\"");
        final String finalResponse = response;
        EncryptReturnBean encryptBean = new Gson().fromJson(finalResponse, EncryptReturnBean.class);
        final String dataString = Encrypt.transEncrytionDataStringRetrofit(encryptBean);

        JSONObject jsonObject = JSON.parseObject(dataString);
        int code = jsonObject.getIntValue(Result.ONSUCCESS);
        final String msg = jsonObject.getString(Result.RESULT_MSG);
        if (code==Result.CODE_SUCCESS){
            final String data = jsonObject.getString(Result.DATA);
            Log.e("StringCallBack", "成功返回data---->:" + data );
            if (!TextUtils.isEmpty(data)) {
                mDelivery.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("StringCallBack", "解密数据" + data);
                        onSuccess(data);
                    }
                });
            }
        }else {
            mDelivery.post(new Runnable() {
                @Override
                public void run() {
                    onXError(msg);
                    Log.e("TODO",msg+"");
                }
            });
        }
    }


    @Override
    public void onError(final Throwable e) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                Log.e("TODO",e.getMessage()+"");
                onXError(e.getMessage());
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
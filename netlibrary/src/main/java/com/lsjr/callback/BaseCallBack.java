package com.lsjr.callback;

import android.os.Handler;
import android.os.Looper;

import rx.Subscriber;

/**
  * @author: gyymz1993
  * 创建时间：2017/5/3 22:27
  * @version
  *
 **/
 public abstract class BaseCallBack<T> extends Subscriber<T> {
    protected Handler mDelivery;
    BaseCallBack() {
        mDelivery = new Handler(Looper.getMainLooper());
    }

}
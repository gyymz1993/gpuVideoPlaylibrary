package com.example.administrator.mylibrary;

import android.app.Application;

import itbour.onetouchshow.base.BaseApplication;

/**
 * Created by Administrator on 2018/4/8.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BaseApplication.instance().initialize(this);
    }
}

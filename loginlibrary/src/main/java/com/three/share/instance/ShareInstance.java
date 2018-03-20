package com.three.share.instance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.three.share.bean.ShareImageObject;
import com.three.share.listener.ShareListener;


/**
 * Created by onetouch on 2017/11/22.
 */

public interface ShareInstance {


    void shareText(int platform, String text, Activity activity, ShareListener listener);

    void shareMedia(int platform, String title, String targetUrl, String summary, ShareImageObject shareImageObject, Activity activity, ShareListener listener);

    void shareImage(int platform, ShareImageObject shareImageObject, Activity activity, ShareListener listener);

    void handleResult(Intent data);

    boolean isInstall(Context context);

    void recycle();
}

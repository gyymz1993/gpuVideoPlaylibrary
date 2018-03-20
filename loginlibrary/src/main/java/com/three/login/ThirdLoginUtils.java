package com.three.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.three.ThirdActivityPorvider;
import com.three.login.bean.BaseToken;
import com.three.login.instance.LoginInstance;
import com.three.login.instance.WxLoginInstance;
import com.three.login.listener.LoginListener;
import com.three.login.result.LoginResult;


/**
 * Created by onetouch on 2017/11/21.
 */

public class ThirdLoginUtils {


    private LoginInstance mLoginInstance;

    private LoginListener mLoginListener;

    private int mPlatform;
    private boolean isFetchUserInfo;
    public static final int TYPE = 799;
    private static ThirdLoginUtils thirdLoginUtils;

    public static ThirdLoginUtils initialize() {
        if (thirdLoginUtils == null) {
            synchronized (ThirdLoginUtils.class) {
                thirdLoginUtils = new ThirdLoginUtils();
            }
        }
        return thirdLoginUtils;
    }


    private ThirdLoginUtils() {

    }


    public void login(Context context, @LoginPlatform.Platform int platform, LoginListener listener) {
        login(context, platform, listener, true);
    }

    private void login(Context context, @LoginPlatform.Platform int platform, LoginListener listener, boolean fetchUserInfo) {
        mPlatform = platform;
        mLoginListener = new LoginListenerProxy(listener);
        isFetchUserInfo = fetchUserInfo;
        context.startActivity(ThirdActivityPorvider.newInstance(context, TYPE));
    }

    public void action(Activity activity) {
        // 防止之后调用 NullPointException
        if (mLoginListener == null) {
            activity.finish();
            return;
        }
        switch (mPlatform) {
            case LoginPlatform.WX:
                mLoginInstance = new WxLoginInstance(activity, mLoginListener, isFetchUserInfo);
                break;
            default:
                mLoginListener.loginFailure(new Exception(INFO.UNKNOW_PLATFORM));
                activity.finish();
        }
        mLoginInstance.doLogin(activity, mLoginListener, isFetchUserInfo);
    }

    public void handleResult(int requestCode, int resultCode, Intent data) {
        if (mLoginInstance != null) {
            mLoginInstance.handleResult(requestCode, resultCode, data);
        }
    }

    public void recycle() {
        if (mLoginInstance != null) {
            mLoginInstance.recycle();
        }
        mLoginInstance = null;
        mLoginListener = null;
        mPlatform = 0;
        isFetchUserInfo = false;
    }

    private class LoginListenerProxy extends LoginListener {

        private LoginListener mListener;

        LoginListenerProxy(LoginListener listener) {
            mListener = listener;
        }

        @Override
        public void loginSuccess(LoginResult result) {
            //ShareLogger.i(INFO.LOGIN_SUCCESS);
            mListener.loginSuccess(result);
            recycle();
        }

        @Override
        public void loginFailure(Exception e) {
            //ShareLogger.i(INFO.LOGIN_FAIl);
            mListener.loginFailure(e);
            recycle();
        }

        @Override
        public void loginCancel() {
            //ShareLogger.i(INFO.LOGIN_CANCEL);
            mListener.loginCancel();
            recycle();
        }

        @Override
        public void beforeFetchUserInfo(BaseToken token) {
            //ShareLogger.i(INFO.LOGIN_AUTH_SUCCESS);
            mListener.beforeFetchUserInfo(token);
        }
    }
}

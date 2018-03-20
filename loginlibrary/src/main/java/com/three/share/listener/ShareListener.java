package com.three.share.listener;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.three.login.INFO;

/**
 * Created by onetouch on 2017/11/22.
 */


public abstract class ShareListener implements IUiListener {

    public abstract void shareSuccess();

    public abstract void shareFailure(Exception e);

    public abstract void shareCancel();

    public void shareRequest() {
    }

    /*---------------qq start   */
    @Override
    public final void onComplete(Object o) {
        shareSuccess();
    }

    @Override
    public final void onError(UiError uiError) {
        shareFailure(new Exception(uiError == null ? INFO.DEFAULT_QQ_SHARE_ERROR : uiError.errorDetail));
    }

    @Override
    public final void onCancel() {
        shareCancel();
    }
     /*---------------qq end   */
}
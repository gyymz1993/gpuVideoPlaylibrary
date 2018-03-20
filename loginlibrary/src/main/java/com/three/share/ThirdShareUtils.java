package com.three.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.three.ThirdActivityPorvider;
import com.three.ThirdConfigManager;
import com.three.login.INFO;
import com.three.share.bean.ShareImageObject;
import com.three.share.instance.DefaultShareInstance;
import com.three.share.instance.QQShareInstance;
import com.three.share.instance.ShareInstance;
import com.three.share.instance.WeiboShareInstance;
import com.three.share.instance.WxShareInstance;
import com.three.share.listener.ShareListener;

import java.util.List;
import java.util.Locale;

/**
 * Created by onetouch on 2017/11/22.
 */

public class ThirdShareUtils {
    /**
     * 1. 本地图片 vs 网络图片
     * 2. 图片大小限制
     * 3. 文字长度限制
     */

    public static final int TYPE = 798;

    public ShareListener mShareListener;

    private ShareInstance mShareInstance;

    private final static int TYPE_IMAGE = 1;
    private final static int TYPE_TEXT = 2;
    private final static int TYPE_MEDIA = 3;

    private int mType;
    private int mPlatform;
    private String mText;
    private ShareImageObject mShareImageObject;
    private String mTitle;
    private String mSummary;
    private String mTargetUrl;
    private static ThirdShareUtils thirdShareUtils;

    public static ThirdShareUtils initialize() {
        if (thirdShareUtils == null) {
            synchronized (ThirdShareUtils.class) {
                thirdShareUtils = new ThirdShareUtils();
            }
        }
        return thirdShareUtils;
    }


    public void action(Activity activity) {
        mShareInstance = getShareInstance(mPlatform, activity);
        // 防止之后调用 NullPointException
        if (mShareListener == null) {
            activity.finish();
            return;
        }

        if (!mShareInstance.isInstall(activity)) {
            mShareListener.shareFailure(new Exception(INFO.NOT_INSTALL));
            activity.finish();
            return;
        }

        switch (mType) {
            case TYPE_TEXT:
                mShareInstance.shareText(mPlatform, mText, activity, mShareListener);
                break;
            case TYPE_IMAGE:
                mShareInstance.shareImage(mPlatform, mShareImageObject, activity, mShareListener);
                break;
            case TYPE_MEDIA:
                mShareInstance.shareMedia(mPlatform, mTitle, mTargetUrl, mSummary, mShareImageObject, activity, mShareListener);
                break;
        }
    }

    public void shareText(Context context,@SharePlatform.Platform int platform, String text, ShareListener listener) {
        mType = TYPE_TEXT;
        mText = text;
        mPlatform = platform;
        mShareListener = buildProxyListener(listener);

        context.startActivity(ThirdActivityPorvider.newInstance(context, TYPE));
    }

    public void shareImage(Context context,@SharePlatform.Platform final int platform, final String urlOrPath, ShareListener listener) {
        mType = TYPE_IMAGE;
        mPlatform = platform;
        mShareImageObject = new ShareImageObject(urlOrPath);
        mShareListener = buildProxyListener(listener);

        context.startActivity(ThirdActivityPorvider.newInstance(context, TYPE));
    }

    public void shareImage(Context context,@SharePlatform.Platform final int platform, final Bitmap bitmap, ShareListener listener) {
        mType = TYPE_IMAGE;
        mPlatform = platform;
        mShareImageObject = new ShareImageObject(bitmap);
        mShareListener = buildProxyListener(listener);

        context.startActivity(ThirdActivityPorvider.newInstance(context, TYPE));
    }

    public void shareMedia(Context context,@SharePlatform.Platform int platform, String title, String summary, String targetUrl, Bitmap thumb, ShareListener listener) {
        mType = TYPE_MEDIA;
        mPlatform = platform;
        mShareImageObject = new ShareImageObject(thumb);
        mSummary = summary;
        mTargetUrl = targetUrl;
        mTitle = title;
        mShareListener = buildProxyListener(listener);

        context.startActivity(ThirdActivityPorvider.newInstance(context, TYPE));
    }

    public void shareMedia(Context context,@SharePlatform.Platform int platform, String title, String summary, String targetUrl, String thumbUrlOrPath, ShareListener listener) {
        mType = TYPE_MEDIA;
        mPlatform = platform;
        mShareImageObject = new ShareImageObject(thumbUrlOrPath);
        mSummary = summary;
        mTargetUrl = targetUrl;
        mTitle = title;
        mShareListener = buildProxyListener(listener);
        context.startActivity(ThirdActivityPorvider.newInstance(context, TYPE));
    }

    private ShareListener buildProxyListener(ShareListener listener) {
        return new ShareListenerProxy(listener);
    }

    public void handleResult(Intent data) {
        // 微博分享会同时回调onActivityResult和onNewIntent， 而且前者返回的intent为null
        if (mShareInstance != null && data != null) {
            mShareInstance.handleResult(data);
        } else if (data == null) {
            if (mPlatform != SharePlatform.WEIBO) {
                //ShareLogger.e(INFO.HANDLE_DATA_NULL);
            }
        } else {
            // ShareLogger.e(INFO.UNKNOWN_ERROR);
        }
    }

    private ShareInstance getShareInstance(@SharePlatform.Platform int platform, Context context) {
        switch (platform) {
            case SharePlatform.WX:
            case SharePlatform.WX_TIMELINE:
                return new WxShareInstance(context, ThirdConfigManager.CONFIG.getWxId());
            case SharePlatform.QQ:
            case SharePlatform.QZONE:
                return new QQShareInstance(context, ThirdConfigManager.CONFIG.getQqId());
            case SharePlatform.WEIBO:
                return new WeiboShareInstance(context, ThirdConfigManager.CONFIG.getWeiboId());
            case SharePlatform.DEFAULT:
            default:
                return new DefaultShareInstance();
        }
    }

    public void recycle() {
        mTitle = null;
        mSummary = null;
        mShareListener = null;

        // bitmap recycle
        if (mShareImageObject != null && mShareImageObject.getBitmap() != null && !mShareImageObject.getBitmap().isRecycled()) {
            mShareImageObject.getBitmap().recycle();
        }
        mShareImageObject = null;

        if (mShareInstance != null) {
            mShareInstance.recycle();
        }
        mShareInstance = null;
    }

    /**
     * 检查客户端是否安装
     */

    public boolean isInstalled(@SharePlatform.Platform int platform, Context context) {
        switch (platform) {
            case SharePlatform.QQ:
            case SharePlatform.QZONE:
                return isQQInstalled(context);
            case SharePlatform.WEIBO:
                return isWeiBoInstalled(context);
            case SharePlatform.WX:
            case SharePlatform.WX_TIMELINE:
                return isWeiXinInstalled(context);
            case SharePlatform.DEFAULT:
                return true;
            default:
                return false;
        }
    }

    @Deprecated
    public boolean isQQInstalled(@NonNull Context context) {
        PackageManager pm = context.getPackageManager();
        if (pm == null) {
            return false;
        }

        List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
        for (PackageInfo info : packageInfos) {
            if (TextUtils.equals(info.packageName.toLowerCase(Locale.getDefault()), "com.tencent.mobileqq")) {
                return true;
            }
        }
        return false;
    }

    @Deprecated
    public boolean isWeiBoInstalled(@NonNull Context context) {
        return true;
    }

    @Deprecated
    public boolean isWeiXinInstalled(Context context) {
        IWXAPI api = WXAPIFactory.createWXAPI(context, ThirdConfigManager.CONFIG.getWxId(), true);
        return api.isWXAppInstalled();
    }

    private class ShareListenerProxy extends ShareListener {

        private final ShareListener mShareListener;

        ShareListenerProxy(ShareListener listener) {
            mShareListener = listener;
        }

        @Override
        public void shareSuccess() {
            recycle();
            mShareListener.shareSuccess();
        }

        @Override
        public void shareFailure(Exception e) {
            recycle();
            mShareListener.shareFailure(e);
        }

        @Override
        public void shareCancel() {
            recycle();
            mShareListener.shareCancel();
        }

        @Override
        public void shareRequest() {
            mShareListener.shareRequest();
        }

    }
}

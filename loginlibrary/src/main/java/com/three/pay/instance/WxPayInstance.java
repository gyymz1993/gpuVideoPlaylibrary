package com.three.pay.instance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.three.ThirdConfigManager;
import com.three.login.INFO;
import com.three.pay.bean.PayInfo;
import com.three.pay.listener.PayListener;

/**
 * O
 * Created by onetouch on 2017/11/22.
 */

public class WxPayInstance extends PayInstance {

    public IWXAPI mIWXAPI;
    private PayListener mPayListener;
    private PayInfo mPayInfo;

    public WxPayInstance(Context activity, PayInfo payInfo, PayListener listener) {
        this.mPayListener = listener;
        this.mPayInfo = payInfo;
        mIWXAPI = WXAPIFactory.createWXAPI(activity, ThirdConfigManager.CONFIG.getWxId());
        mIWXAPI.registerApp(ThirdConfigManager.CONFIG.getWxId());
    }


    @Override
    public void handleResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ConstantsAPI.COMMAND_PAY_BY_WX) {
            switch (resultCode) {
                case BaseResp.ErrCode.ERR_OK:
                    mPayListener.paySuccess();
                    break;
                case BaseResp.ErrCode.ERR_COMM:
                    mPayListener.payFailure(new Exception(INFO.WX_ERR_AUTH_ERROR));
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    mPayListener.userCancel();
                    break;
                default:
                    mPayListener.payFailure(new Exception(INFO.WX_ERR_AUTH_ERROR));
            }
        }
    }

    @Override
    public boolean isInstall(Context context) {
        return mIWXAPI.isWXAppInstalled();
    }

    @Override
    public void doPay(Activity activity, PayListener listener, PayInfo payInfo) {
        PayReq payReq = new PayReq();
        /*应用id*/
        payReq.appId = "wx5022d3c8ad81e74f";
        /*商户号*/
        payReq.partnerId = "1492744852";
        /*预支付订单号*/
        payReq.prepayId = mPayInfo.getPrepayId();
        /*随机字符串*/
        payReq.nonceStr = mPayInfo.getNonceStr();
        /*时间戳*/
        payReq.timeStamp = mPayInfo.getTimeStamp();
        /*扩展字段*/
        payReq.packageValue = "Sign=WXPay";
        /*签名*/
        payReq.sign = mPayInfo.getSign();
        //判断是否安装微信客户端
        mIWXAPI.sendReq(payReq);
    }
}

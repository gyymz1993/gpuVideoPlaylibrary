package com.three.pay;

import com.tencent.mm.opensdk.modelpay.PayReq;
import com.three.pay.bean.PayInfo;

/**
 * Created by onetouch on 2017/11/22.
 */

public abstract class PayUrlGeneratro {

    private PayInfo payInfo;

    public PayUrlGeneratro(PayInfo payInfo) {
        this.payInfo = payInfo;
    }

    public abstract PayReq getPayReq();
}

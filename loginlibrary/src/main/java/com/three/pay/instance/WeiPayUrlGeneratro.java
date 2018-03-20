package com.three.pay.instance;

import com.tencent.mm.opensdk.modelpay.PayReq;
import com.three.pay.PayUrlGeneratro;
import com.three.pay.bean.PayInfo;

/**
 * Created by onetouch on 2017/11/22.
 */

public class WeiPayUrlGeneratro extends PayUrlGeneratro {
    public WeiPayUrlGeneratro(PayInfo payInfo) {
        super(payInfo);
    }

    @Override
    public PayReq getPayReq() {
        return null;
    }
}

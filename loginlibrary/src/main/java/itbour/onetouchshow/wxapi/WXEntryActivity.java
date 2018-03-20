package itbour.onetouchshow.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;


/**
 * @author ymz
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("WXEntryActivity ","onCreate");
    }

    @Override
    public void onReq(BaseReq req) {

        Log.i("WXEntryActivity",req.toString()+"");
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.i("WXEntryActivity ","onResp");
    }
}

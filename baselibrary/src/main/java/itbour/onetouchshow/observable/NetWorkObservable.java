package itbour.onetouchshow.observable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Observable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import itbour.onetouchshow.base.BaseApplication;
import itbour.onetouchshow.utils.T_;


public class NetWorkObservable extends Observable<NetWorkObservable.NetWorkObserver> {
    public interface NetWorkObserver {
        void onNetWorkStatusChange(boolean connected);

        void onNewWorkEnvironment(NetStateChangeEvent.NetState netState);
    }

    public static class NetStateChangeEvent {
        public enum NetState {
            NET_NO, NET_WIFI, NET_4G
        }

        NetState state;

        public NetStateChangeEvent(NetState state) {
            this.state = state;
        }

        public NetState getState() {
            return state;
        }
    }

    private Context mContext;
    private ConnectivityManager mConnectivityManager;
    private boolean mIsNetWorkActive;
    /**
     * 当前网络是否连接上
     */
    private boolean mRegisted;
    // 是否注册了广播

    public NetWorkObservable(Context context) {
        mContext = context;
        // 获取程序启动时的网络状态
        mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        mIsNetWorkActive = isGprsOrWifiConnected();
        //L_.e("mIsNetWorkActive:" + mIsNetWorkActive);
        // 注册网络监听广播
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(mNetWorkChangeReceiver, intentFilter);
        mRegisted = true;
    }

    private boolean isGprsOrWifiConnected() {
        NetworkInfo gprs = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isConnectedGprs = gprs != null && gprs.isConnected();
        boolean isConnectedWifi = wifi != null && wifi.isConnected();
        return isConnectedGprs || isConnectedWifi;
    }

    private BroadcastReceiver mNetWorkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                return;
            }
            final boolean isConnected = isGprsOrWifiConnected();
            if (mIsNetWorkActive != isConnected) {// 和之前的状态不同
                mIsNetWorkActive = isConnected;
                notifyChanged(mIsNetWorkActive);
            }


            String action = intent.getAction(); //当前接受到的广播的标识(行动/意图)
            // 当当前接受到的广播的标识(意图)为网络状态的标识时做相应判断
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                // 获取网络连接管理器
                ConnectivityManager connectivityManager = (ConnectivityManager) BaseApplication.getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
                // 获取当前网络状态信息
                // 网络状态信息的实例
                NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()) {
                    //当NetworkInfo不为空且是可用的情况下，获取当前网络的Type状态
                    //根据NetworkInfo.getTypeName()判断当前网络
                    String name = info.getTypeName();
                    //更改NetworkStateService的静态变量，之后只要在Activity中进行判断就好了
                    if (name.equals("WIFI")) {
                        // networkStatus = 2;
                        notifyChangedEnvironment(NetStateChangeEvent.NetState.NET_WIFI);
                        //T_.showToastReal("网络NET_WIFI");
                    } else {
                        // networkStatus = 1;
                        notifyChangedEnvironment(NetStateChangeEvent.NetState.NET_4G);
                        //T_.showToastReal("网络NET_4G");
                    }
                } else {
                    // NetworkInfo为空或者是不可用的情况下
                    // networkStatus = 0;
                    notifyChangedEnvironment(NetStateChangeEvent.NetState.NET_NO);
                    // Toast.makeText(context, "没有可用网络!\n请连接网络后刷新本界面", Toast.LENGTH_SHORT).show();
                    //T_.showToastReal("网络不可用");
                }
            }
        }
    };

    public void notifyChanged(boolean connected) {
        synchronized (mObservers) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onNetWorkStatusChange(connected);
            }
        }
    }

    public void notifyChangedEnvironment(NetStateChangeEvent.NetState netState) {
        synchronized (mObservers) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onNewWorkEnvironment(netState);
            }
        }
    }

    public void release() {
        if (mRegisted && mContext != null) {
            mContext.unregisterReceiver(mNetWorkChangeReceiver);
            mRegisted = false;
        }
        unregisterAll();
    }

    public boolean isNetworkActive() {
        return mIsNetWorkActive;
    }

}

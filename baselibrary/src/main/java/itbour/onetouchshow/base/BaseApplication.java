package itbour.onetouchshow.base;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import itbour.onetouchshow.AppCache;
import itbour.onetouchshow.observable.NetWorkObservable;
import itbour.onetouchshow.view.ScreenView;

public class BaseApplication {
    private static Looper mMainThreadLooper = null;
    private static Handler mMainThreadHandler = null;
    private static int mMainThreadId;
    private static Thread mMainThread = null;
    private static Application mApplication;
    private static BaseApplication mBaseApplication;
    private static boolean isInit = false;

    private BaseApplication() {
    }

    public static BaseApplication instance() {
        if (mBaseApplication == null) {
            synchronized (BaseApplication.class) {
                if (mApplication == null) {
                    mBaseApplication = new BaseApplication();
                }
            }
        }
        return mBaseApplication;
    }

    public void initialize(Application application) {
        if (isInit) {
            return;
        }
        mApplication = application;
        if (mApplication != null) {
            mMainThreadLooper = mApplication.getMainLooper();
            mMainThreadHandler = new Handler();
            mMainThreadId = android.os.Process.myTid();
            mMainThread = Thread.currentThread();
            //initException();
           // initSputils();
            //initLeakCanary();
            initImageLoader();
            AppCache.getInstance().initCreateAppDir();;

            AppCache.getInstance().initCreateAppDir();
            // 初始化网络监听
            mNetWorkObservable = new NetWorkObservable(application);
            isInit = true;

        }
    }

    public void registerUiScreen(int width, int height) {
        //需要传入ui设计给的大小
        new ScreenView(mApplication, width, height).register();
    }

    private void initImageLoader() {
        //ImageLoader.init(mApplication);
    }

    /**
     * 检测内存泄露
     */
    public void initLeakCanary() {
    }


    private void initSputils() {
        //SpUtils.getInstance().init(mApplication);
    }


//    /***
//     * 全局异常处理
//     * */
//    public void initException() {
//           /* 全局异常崩溃处理 */
//        ExceptionCrashHander.getInstance().init(mApplication);
//        // 获取上次的崩溃信息
//        File crashFile = ExceptionCrashHander.getInstance().getCrashFile();
//    }

    public static Application getApplication() {
        if (mApplication == null) {
            throw new NullPointerException("mApplication 为空");
        }
        return mApplication;
    }


    /********************* 提供网络全局监听 ************************/
    private NetWorkObservable mNetWorkObservable;

    public boolean isNetworkActive() {
        if (mNetWorkObservable != null) {
            return mNetWorkObservable.isNetworkActive();
        }
        return true;
    }

    public void registerNetWorkObserver(NetWorkObservable.NetWorkObserver observer) {
        if (mNetWorkObservable != null) {
            mNetWorkObservable.registerObserver(observer);
        }
    }

    public void unregisterNetWorkObserver(NetWorkObservable.NetWorkObserver observer) {
        if (mNetWorkObservable != null) {
            mNetWorkObservable.unregisterObserver(observer);
        }
    }

    public static Looper getMainThreadLooper() {
        return mMainThreadLooper;
    }

    public static Handler getMainThreadHandler() {
        return mMainThreadHandler;
    }

    public static int getMainThreadId() {
        return mMainThreadId;
    }

    public static Thread getMainThread() {
        return mMainThread;
    }

}

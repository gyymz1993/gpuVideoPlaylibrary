package com.lsjr.net;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lsjr.utils.NetUtils;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

class AppClient {

    private static Retrofit mRetrofit;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    public static final String DB_DATA_FORMAT = "yyyy-MM-DD HH:mm:ss";

    private static Retrofit retrofit() {
        if (mRetrofit == null) {
            Gson gson = new GsonBuilder().setDateFormat(DB_DATA_FORMAT).create();
            //使用 gson coverter，统一日期请求格式
            String getBaseUrl = AppNetConfig.getInstance().getMbuider().getBaseUrl();
            if (getBaseUrl == null) {
                throw new NullPointerException("请设置BaseUrl");
            }

            mRetrofit = new Retrofit.Builder()
                    .baseUrl(getBaseUrl)
                    .client(getOkHttpClient())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    //自定义解析增加加密方式`
                    //一定要在gsonConverter前面,否则gson会拦截所有的解析方式
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return mRetrofit;
        }
        return mRetrofit;
    }

    public static SSLContext acceptHttps() {
        X509TrustManager xtm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] x509Certificates = new X509Certificate[0];
                return x509Certificates;
            }
        };

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");

            sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sslContext;
    }

    public static HostnameVerifier gethosVerifier() {
        HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        return DO_NOT_VERIFY;
    }


    /*
    *请求路径打印
    */
    private static final int DEFAULT_MILLISECONDS = 60; //默认的超时时间

    private static OkHttpClient getOkHttpClient() {
        //定制OkHttp
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        //httpClientBuilder.connectTimeout(10, TimeUnit.SECONDS)
        httpClientBuilder.connectTimeout(DEFAULT_MILLISECONDS, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_MILLISECONDS, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_MILLISECONDS, TimeUnit.SECONDS);
        httpClientBuilder.addInterceptor(new LoggingInterceptor());
        httpClientBuilder.addNetworkInterceptor(new CacheInterceptor());
        //允许HTTPS

        X509TrustManager xtm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] x509Certificates = new X509Certificate[0];
                return x509Certificates;
            }
        };

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");

            sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());

        } catch (Exception e) {
            e.printStackTrace();
        }
        HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        httpClientBuilder.sslSocketFactory(sslContext.getSocketFactory())
                .hostnameVerifier(DO_NOT_VERIFY);

        /*设置缓存*/
        File cacheFile = new File(mContext.getCacheDir(), "[缓存目录]");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); //100Mb
        httpClientBuilder.cache(cache);
        return httpClientBuilder.build();
    }


    private static class CacheInterceptor implements Interceptor {
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        public Response intercept(Chain chain) throws IOException {
            //通过 CacheControl 控制缓存数据
            Request request = chain.request();
            if (NetUtils.isConnected(mContext)) {
                Response response = chain.proceed(request);
                // read from cache for 60 s
                int maxAge = 60;
                String cacheControl = request.cacheControl().toString();
                Log.e("Tamic", "60s load cahe" + cacheControl);
                return response.newBuilder().removeHeader("Pragma").removeHeader("Cache-Control").header("Cache-Control", "public, max-age=" + maxAge).build();
            } else {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "当前无网络! 为你智能加载缓存", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Tamic", " no network load cahe");
                request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
                Response response = chain.proceed(request);
                //set cahe times is 3 days
                int maxStale = 60 * 60 * 24 * 3;
                return response.newBuilder().removeHeader("Pragma").removeHeader("Cache-Control").header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale).build();
            }
        }
    }


    private static class LoggingInterceptor implements Interceptor {
        @SuppressLint("DefaultLocale")
        @Override
        public Response intercept(Chain chain) throws IOException {
            //这个chain里面包含了request和response，所以你要什么都可以从这里拿
            Request request = chain.request();
            long t1 = System.nanoTime();//请求发起的时间
            Log.e("AppClient",String.format("发送请求 %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));
            Response response = chain.proceed(request);
            long t2 = System.nanoTime();//收到响应的时间
            ResponseBody responseBody = response.peekBody(1024 * 1024);
            Log.e("AppClient", String.format("接收响应: [%s] %n返回json:【%s】 %.1fms%n%s",
                    response.request().url(),
                    responseBody.string(),
                    (t2 - t1) / 1e6d,
                    response.headers()));
            return response;
        }

    }

    static ApiService getApiService(Context context) {
        mContext = context;
        return retrofit().create(ApiService.class);
    }

}

package com.lsjr.utils;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * 创建人：$ gyymz1993
 * 创建时间：2017/6/28 19:42
 */

public class UrlUtils {
    /* Get 参数拼接 */
    public static String spliceGetUrl(String baseurl,Map mParams) {
        String param = "";
        if (mParams != null && mParams.size() > 0) {
            String url = baseurl;
            if (url != null && !url.contains("?")) {
                url += "?";
            }
            for (Object key : mParams.keySet()) {
                param += (key + "=" + mParams.get(key) + "&");
            }
            param = param.substring(0, param.length() - 1);// 去掉最后一个&
            Log.i("UrlUtils--->","没有加密的URL  get:"+url + param);
        }
        return param;
    }

    /* post 参数拼接 */
    private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";
    public static String encodesParameters(String baseurl,Map<String, String> params) {
        if (params==null) {
            return baseurl;
        }
        if (baseurl==null) {
            throw new NullPointerException("请设置BaseUrl");
        }
        String url = baseurl;
        if(!baseurl.contains("?")) {
            url = baseurl + "?";
        }
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), DEFAULT_PARAMS_ENCODING));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), DEFAULT_PARAMS_ENCODING));
                encodedParams.append('&');
            }
            // Log.e("UrlUtils---->","没有加密的URL  post:"+baseurl+strUrl);
            return url+encodedParams.substring(0, encodedParams.length() - 1);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + DEFAULT_PARAMS_ENCODING, uee);
        }
    }
}

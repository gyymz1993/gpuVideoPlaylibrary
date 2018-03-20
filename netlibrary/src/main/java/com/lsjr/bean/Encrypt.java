package com.lsjr.bean;

import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.lsjr.net.RequestParams;
import com.lsjr.utils.Md5Utils;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by admin on 2016/8/4.
 */
public class Encrypt {

    private static final int RANDOM_STRING_SIZE = 16;
    private static final int SUBSTRING_SIZE = 16;
    private static final String LOCAL_KEY = "0123456789abcdef";
    private static final int SHARE_TRING_SIZE = 5;
    private static final String TAG = "Encrypt";
    private static String sign;

    public static String getSign() {
        return sign;
    }

    public static String getLocalKey() {
        return LOCAL_KEY;
    }

    public static int getRandomStringSize() {
        return RANDOM_STRING_SIZE;
    }

    public static int getSubstringSize() {
        return SUBSTRING_SIZE;
    }

    /**
     * @param paramsMap
     * @param destination 这个参数是传入的请求接口
     * @return
     */
    public static EncryptBean transEncrytionParamsReftrofit(HashMap<String, ? extends Object> paramsMap, String destination) {
        return transEncrytionParamsReftrofitBase(paramsMap, destination, 1);
    }

    public static EncryptBean transEncrytionParamsReftrofitBase(HashMap<String, ? extends Object> paramsMap, String destination, int priority) {
        //第一次生成随机字符串
        String random = RandomString(RANDOM_STRING_SIZE);
        //外界传入参数 然后将参数转化为json
        String originalString = new Gson().toJson(paramsMap);
        String jsonBase64 = new String(Base64.encode(originalString.getBytes(), Base64.DEFAULT));
        String string = random + jsonBase64;
        String sendRandom = RandomString(RANDOM_STRING_SIZE);
        String orignSign = sendRandom + LOCAL_KEY;
        sign = Md5Utils.encode(orignSign);
        EncryptBean encryptBean = new EncryptBean(string, sendRandom, sign, priority, destination);
        return encryptBean;
    }


    /**
     * 拆分 传入一个HashMap 返回一个Params
     */
    public static HashMap transEncrytionParams(HashMap<String, ? extends Object> paramsMap, String destination) {
        EncryptBean encryptBean = transEncrytionParamsReftrofitBase(paramsMap, destination, 1);
        HashMap<String, String> params = new HashMap<>();
        params.put("string", encryptBean.getString());
        params.put("random", encryptBean.getRandom());
        Log.i(TAG, "onSuccess: beforesign===" + sign);
        params.put("sign", encryptBean.getSign());
        params.put("priority", 1 + "");
        params.put("destination", encryptBean.getDestination());
        return params;
    }


    /**
     * 拆分 传入一个HashMap 返回一个Params webview的参数 区别和常规连接的区别仅仅在priority为5
     */
    public static RequestParams transWebEncrytionParams(HashMap<String, ? extends Object> paramsMap, String destination) {
        EncryptBean encryptBean = transEncrytionParamsReftrofitBase(paramsMap, destination, 5);
        RequestParams params = new RequestParams();
        params.addFormDataPart("string", encryptBean.getString());
        params.addFormDataPart("random", encryptBean.getRandom());
        params.addFormDataPart("sign", encryptBean.getSign());
        params.addFormDataPart("priority", 5 + "");
        params.addFormDataPart("destination", encryptBean.getDestination());
        return params;
    }


    /**
     * 产生一个随机的指定位数的字符串
     * 字符串的内容只包含在26个大小写字母和数字中
     */
    public static String RandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < length; i++) {
            //选取的范围字符串长度为62
            int num = random.nextInt(62);
            buf.append(str.charAt(num));
        }
        return buf.toString();
    }


    /**
     * 传入一个返回的String 拿到数据DataString  解密
     */
    public static String transEncrytionDataStringRetrofit(EncryptReturnBean encryptReturnBean) {
        Log.i(TAG, "encryptReturnBean" + encryptReturnBean);
        Log.i(TAG, "onSuccess: check" + Md5Utils.encode(encryptReturnBean.getRandom() + Encrypt.getLocalKey()) + "===" + encryptReturnBean.getSign());
        Log.i(TAG, "onSuccess: sign===" + Encrypt.getSign() + "===" + encryptReturnBean.getSign());
        if (Md5Utils.encode(encryptReturnBean.getRandom() + Encrypt.getLocalKey()).equals(encryptReturnBean.getSign())) {
            String subString = encryptReturnBean.getString().substring(Encrypt.getSubstringSize(), encryptReturnBean.getString().length());
            Log.i(TAG, "onSuccess: subString" + subString);
            byte[] decodeStringBytes = Base64.decode(subString.getBytes(), Base64.DEFAULT);
            String dataString = new String(decodeStringBytes);
            Log.i(TAG, "dataString===" + dataString);
            return dataString;
        } else {
//            DialogUtils.encryptDialog(context);
            Log.i(TAG, "lowerEncryption:orignSign   请注意数据安全");
            // Toast.makeText(MyApplication.context, "请注意数据安全", Toast.LENGTH_SHORT).show();
            return "";
        }
    }


}

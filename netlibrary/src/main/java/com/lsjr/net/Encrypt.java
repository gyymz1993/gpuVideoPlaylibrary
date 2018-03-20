package com.lsjr.net;

import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lsjr.bean.EncryptBean;
import com.lsjr.utils.Md5Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
     *
     * @param paramsMap
     * @param destination 这个参数是传入的请求接口
     * @return
     */
    public static EncryptBean transEncrytionParamsReftrofit(HashMap<String,? extends Object> paramsMap, String destination){
        return transEncrytionParamsReftrofitBase(paramsMap,destination,1);
    }

    public static EncryptBean transEncrytionParamsReftrofitBase(HashMap<String,? extends Object> paramsMap, String destination, int priority){
        String random = RandomString(RANDOM_STRING_SIZE);//第一次生成随机字符串
        String originalString = new Gson().toJson(paramsMap);//外界传入参数 然后将参数转化为json
        String jsonBase64 = new String(Base64.encode(originalString.getBytes(), Base64.DEFAULT));
        String string = random + jsonBase64;
        String sendRandom = RandomString(RANDOM_STRING_SIZE);
        String orignSign = sendRandom + LOCAL_KEY;
        Log.i(TAG, "lowerEncryption:orignSign" + orignSign);
        sign = Md5Utils.encode(orignSign);
        EncryptBean encryptBean = new EncryptBean(string,sendRandom,sign,priority,destination);
        return encryptBean;
    }


    //拆分 传入一个HashMap 返回一个Params
    public static HashMap transEncrytionParams(HashMap<String,? extends Object> paramsMap, String destination){
        String random = RandomString(RANDOM_STRING_SIZE);//第一次生成随机字符串
        String originalString = new Gson().toJson(paramsMap);//外界传入参数 然后将参数转化为json
        String jsonBase64 = new String(Base64.encode(originalString.getBytes(), Base64.DEFAULT));
        String string = random + jsonBase64;
        String sendRandom = RandomString(RANDOM_STRING_SIZE);

        String orignSign = sendRandom + LOCAL_KEY;
        Log.i(TAG, "lowerEncryption:orignSign" + orignSign);
        sign = Md5Utils.encode(orignSign);


        HashMap<String,String>  params= new HashMap<>();
        params.put("string", string);
        params.put("random", sendRandom);
        Log.i(TAG, "onSuccess: beforesign===" + sign);
        params.put("sign", sign);
        params.put("priority", 1+"");
        params.put("destination", destination);
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
            int num = random.nextInt(62);//选取的范围字符串长度为62
            buf.append(str.charAt(num));
        }
        return buf.toString();
    }


    //传入一个返回的String 拿到数据DataString  解密
    //防泄漏V540
    public static String transEncrytionDataStringRetrofit(EncryptBean encryptReturnBean){

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
        }  else {
//            DialogUtils.encryptDialog(context);
            Log.i(TAG, "lowerEncryption:orignSign   请注意数据安全" );
           // Toast.makeText(MyApplication.context, "请注意数据安全", Toast.LENGTH_SHORT).show();
            return "";
        }
    }



    /**
     * 加密一组整形数字
     *
     * @param numbers 整形number 数组
     * @return 加密后的文字
     * <p>
     * 注意:
     * 1. 整形 number 不得超过14位
     * 2. numbres 数组内元素不能超过五个
     */
    public static String shareCustomEncryptNumbers(List<Long> numbers) {
        if ( numbers.size() <= 0 || numbers.size() > 5) {
            return null;
        }
        List<Map<String,Object>> arrSEP=new ArrayList<>();
        Map<String,Object> map1=new HashMap<>();
        map1.put("R",26);
        map1.put("S","v");
        Map<String,Object> map2=new HashMap<>();
        map2.put("R",27);
        map2.put("S","w");

        Map<String,Object> map3=new HashMap<>();
        map3.put("R",28);
        map3.put("S","x");

        Map<String,Object> map4=new HashMap<>();
        map4.put("R",29);
        map4.put("S","y");

        Map<String,Object> map5=new HashMap<>();
        map5.put("R",30);
        map5.put("S","z");
        arrSEP.add(map1);
        arrSEP.add(map2);
        arrSEP.add(map3);
        arrSEP.add(map4);
        arrSEP.add(map5);

        String strOut=new String();
        for (int i = 0; i < numbers.size(); i++) {
            Map<String,Object> stringObjectMap = arrSEP.get(i);
            Integer r = (Integer) stringObjectMap.get("R");
            String s = (String) stringObjectMap.get("S");
            Long aLong = numbers.get(i);
            strOut += toStringWithRadix(aLong,r);
            strOut += s;
        }
        return strOut;

    }



    /**
     将 十进制整形 number 转换为指定进制的字符串

     @param radix 进制数 [2-62]之间
     @return 转换后的数据
     */
    public static String toStringWithRadix(Long longLongValue,Integer radix){
        if(radix <2 || radix > 62) {
            return null;
        }
        // 62进制字符集
        String [] strings = new String[]{"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        String chars=new String();
        Long qutient=longLongValue;
        do {
            long mod = qutient % radix;
            qutient = (qutient - mod) / radix;
            chars = strings[Integer.valueOf(String.valueOf(mod))] + chars;
        } while (qutient > 0);
        return chars;
    }

}

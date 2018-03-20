package com.lsjr.net;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.lsjr.bean.EncryptBean;
import com.lsjr.bean.EncryptReturnBean;
import com.lsjr.param.RequestBodyUtils;
import com.lsjr.param.RxHttpParams;
import com.lsjr.param.UploadProgressRequestBody;
import com.lsjr.utils.UrlUtils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Observable;

/**
 * @version 需要加密目前这种方法可行
 * @author: gyymz1993
 * 创建时间：2017/5/3 22:46
 **/
public class DcodeService {
    private static Context mContext;
    public static void initialize(Context context) {
        mContext = context;
    }

    private static ApiService getApiService() {
        if (mContext == null) {
            throw new NullPointerException("请先初始化 initialize");
        }
        return AppClient.getApiService(mContext);
    }

    /**
     * get网络请求入口  数据封装到url
     *
     * */
    public static Observable<String> getServiceData(String baseUrl,Map map) {
        if (TextUtils.isEmpty(baseUrl)) {
            throw new NullPointerException("请设置BaseUrl");
        }
        /**
         * 此处用post拼参数
         *  UrlUtils.spliceGetUrl(baseUrl, map);
          */
        String url=UrlUtils.encodesParameters(baseUrl, map);
        Log.e("UrlUtils----post>","没有加密的URL  post:"+baseUrl+url);
        return getApiService().getData(url);
    }

    /**
     *  post网络请求入口  数据封装到url
     * */
    public static Observable<String> postServiceData(String baseUrl,Map map) {
        if (TextUtils.isEmpty(baseUrl)) {
            throw new NullPointerException("请设置BaseUrl");
        }
        String url=UrlUtils.encodesParameters(baseUrl, map);
       // Log.e("UrlUtils----post>","没有加密的URL  post:"+url);
        return getApiService().postData(url);
    }

    /**
     *  post网络请求入口   表单方式请求
     * */
    public static Observable<String> postServiceDataForBody(String baseUrl,Map map) {
        if (TextUtils.isEmpty(baseUrl)) {
            throw new NullPointerException("请设置BaseUrl");
        }
        String url=UrlUtils.encodesParameters(baseUrl, map);
        //Log.e("UrlUtils----post>","没有加密的URL  post:"+url);
        return getApiService().postDataforBody(baseUrl,map);
    }


    /**
     *  post  返回Bean 网络请求入口  数据封装到body
     * */
    public static Observable<EncryptReturnBean> executePost(EncryptBean encryptBean) {
      //  String getPost = AppNetConfig.getInstance().getMbuider().getPostRequest();
        return getApiService().postReftroCommonRequest(encryptBean.getString(), encryptBean.getRandom(),
                encryptBean.getSign(), encryptBean.getPriority(), encryptBean.getDestination());
    }

    /**
     *  get  返回Bean 网络请求入口  数据封装到body
     * */
    public static Observable<EncryptReturnBean> executeGet(EncryptBean encryptBean) {
      //  String getRequest = AppNetConfig.getInstance().getMbuider().getGetRequest();
        return getApiService().getReftroCommonRequest(encryptBean.getString(), encryptBean.getRandom(),
                encryptBean.getSign(), encryptBean.getPriority(), encryptBean.getDestination());
    }


    /***
     * 多图片上传
     * //表单类型;
     *
     * */
    public static Observable<String> uploadFilesWithParts(String baseUrl,Map<String, String> parameters, List<File> fileList) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String _key = entry.getKey();
            String _value = entry.getValue();
            builder.addFormDataPart(_key, _value);
            Log.e("postFile_key---------->",_key+"-----------:L"+_value);
        }
        String url=UrlUtils.encodesParameters(baseUrl, parameters);
        for (int i = 0; i < fileList.size(); i++) {
            RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), fileList.get(i));
            //"shareImg"+i 后台接收图片流的参数名
            builder.addFormDataPart("file1", fileList.get(i).getName(), imageBody);
        }
        List<MultipartBody.Part> parts = builder.build().parts();
        return getApiService().uploadFiles(baseUrl,parts);
    }

    /***
     * 单文件上传
     * */
    public static Observable<String> uploadFilesWithParts(String baseUrl,Map<String, String> parameters,File file) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        //表单类型;
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String _key = entry.getKey();
            String _value = entry.getValue();
            builder.addFormDataPart(_key, _value);
            Log.e("postFile_key---------->",_key+"-----------:L"+_value);
        }
        String url=UrlUtils.encodesParameters(baseUrl, parameters);
        RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        builder.addFormDataPart("file1", file.getName(), imageBody);
        List<MultipartBody.Part> parts = builder.build().parts();
        return getApiService().uploadFiles(baseUrl,parts);
    }


    /*多图片上传*/
    public static Observable<ResponseBody> uploadFilesWithBodys(String url,RxHttpParams params){
        String getBaseUrl = AppNetConfig.getInstance().getMbuider().getBaseUrl();
        if (TextUtils.isEmpty(getBaseUrl)) {
            throw new NullPointerException("请设置BaseUrl");
        }
        Map<String, RequestBody> mBodyMap = new HashMap<>();
        //拼接参数键值对
        for (Map.Entry<String, String> mapEntry : params.urlParamsMap.entrySet()) {
            RequestBody body = RequestBody.create(MediaType.parse("text/plain"), mapEntry.getValue());
            mBodyMap.put(mapEntry.getKey(), body);
        }
        //拼接文件
        for (Map.Entry<String, List<RxHttpParams.FileWrapper>> entry : params.fileParamsMap.entrySet()) {
            List<RxHttpParams.FileWrapper> fileValues = entry.getValue();
            for (RxHttpParams.FileWrapper fileWrapper : fileValues) {
                RequestBody requestBody = getRequestBody(fileWrapper);
                UploadProgressRequestBody uploadProgressRequestBody = new UploadProgressRequestBody(requestBody, fileWrapper.responseCallBack);
                mBodyMap.put(entry.getKey(), uploadProgressRequestBody);
            }
        }
        return getApiService().uploadFiles(url, mBodyMap);
    }


    /**
    *  带上传进度
    * */
    public static Observable<String> uploadFilesWithParts(String url,RxHttpParams params) {
        List<MultipartBody.Part> parts = new ArrayList<>();
        //拼接参数键值对
        for (Map.Entry<String, String> mapEntry : params.urlParamsMap.entrySet()) {
            parts.add(MultipartBody.Part.createFormData(mapEntry.getKey(), mapEntry.getValue()));
        }
        //拼接文件
        for (Map.Entry<String, List<RxHttpParams.FileWrapper>> entry : params.fileParamsMap.entrySet()) {
            List<RxHttpParams.FileWrapper> fileValues = entry.getValue();
            for (RxHttpParams.FileWrapper fileWrapper : fileValues) {
                MultipartBody.Part part = addFile(entry.getKey(), fileWrapper);
                parts.add(part);
            }
        }
        return getApiService().uploadFiles(url,parts);
    }


    /**
     *  带下载进度
    * */
    public static Observable<ResponseBody> downloadFile(String url) {
        return getApiService().downloadFile(url);
    }


    //文件方式
    private static MultipartBody.Part addFile(String key, RxHttpParams.FileWrapper fileWrapper) {
        //MediaType.parse("application/octet-stream", file)
        RequestBody requestBody = getRequestBody(fileWrapper);
      //  Utils.checkNotNull(requestBody, "requestBody==null fileWrapper.file must is File/InputStream/byte[]");
        //包装RequestBody，在其内部实现上传进度监听
        if (fileWrapper.responseCallBack != null) {
            UploadProgressRequestBody uploadProgressRequestBody = new UploadProgressRequestBody(requestBody, fileWrapper.responseCallBack);
            MultipartBody.Part part = MultipartBody.Part.createFormData(key, fileWrapper.fileName, uploadProgressRequestBody);
            return part;
        } else {
            MultipartBody.Part part = MultipartBody.Part.createFormData(key, fileWrapper.fileName, requestBody);
            return part;
        }
    }


    private static RequestBody getRequestBody(RxHttpParams.FileWrapper fileWrapper) {
        RequestBody requestBody = null;
        if (fileWrapper.file instanceof File) {
            requestBody = RequestBody.create(fileWrapper.contentType, (File) fileWrapper.file);
        } else if (fileWrapper.file instanceof InputStream) {
            //requestBody = RequestBodyUtils.create(RequestBodyUtils.MEDIA_TYPE_MARKDOWN, (InputStream) fileWrapper.file);
            requestBody = RequestBodyUtils.create(fileWrapper.contentType, (InputStream) fileWrapper.file);
        } else if (fileWrapper.file instanceof byte[]) {
            requestBody = RequestBody.create(fileWrapper.contentType, (byte[]) fileWrapper.file);
        }
        return requestBody;
    }

}

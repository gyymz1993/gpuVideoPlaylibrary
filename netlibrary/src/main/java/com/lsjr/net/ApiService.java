package com.lsjr.net;

import com.lsjr.bean.EncryptReturnBean;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * @version 需要加密目前这种方法可行
 * @author: gyymz1993
 * 创建时间：2017/5/3 22:46
 **/
public interface ApiService {



    @GET
    Observable<String> getData(@Url String url);

    @POST
    Observable<String> postData(@Url String url);

    //参数较多
    @FormUrlEncoded
    @POST
    Observable<String> postDataforBody(@Url String url,@FieldMap Map<String, String> maps);



    @Multipart
    @POST()
    Observable<ResponseBody> uploadFiles(@Url() String url, @PartMap() Map<String, RequestBody> maps);

    /**
     * 多文件多参数上传
     **/
    @Multipart
    @POST()
    Observable<String> uploadFiles(@Url String url, @Part() List<MultipartBody.Part> parts);

    /**
     *  文件下载
     * @param fileUrl
     * @return
     */
    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String fileUrl);


    /**
     * rxjava post模式
     * @param string
     * @param random
     * @param sign
     * @param priority
     * @param destination
     * @return
     */
    @FormUrlEncoded
    @POST(BaseUrl.HTTP_ENCRYPT_ENDDING_POST)
    Observable<EncryptReturnBean> postReftroCommonRequest(@Field("string") String string, @Field("random") String random
            , @Field("sign") String sign, @Field("priority") int priority, @Field("destination") String destination);

    /**
     * RxJava get模式
     * @param string
     * @param random
     * @param sign
     * @param priority
     * @param destination
     * @return
     */
    @GET(BaseUrl.HTTP_ENCRYPT_ENDDING_GET)
    Observable<EncryptReturnBean> getReftroCommonRequest(@Query("string") String string, @Query("random") String random
            , @Query("sign") String sign, @Query("priority") int priority, @Query("destination") String destination);

}

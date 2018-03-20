/*
 * Copyright (C) 2017 zhouyou(478319399@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lsjr.callback;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.lsjr.param.HttpLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public abstract class DownloadSubscriber<ResponseBody extends okhttp3.ResponseBody> extends BaseCallBack<ResponseBody> {
    private Context context;
    private String path;
    private String name;
    private static String APK_CONTENTTYPE = "application/vnd.android.package-archive";
    private static String PNG_CONTENTTYPE = "image/png";
    private static String JPG_CONTENTTYPE = "image/jpg";
    private static String TEXT_CONTENTTYPE = "text/html; charset=utf-8";
    private static String fileSuffix = "";
    private long lastRefreshUiTime;

    public DownloadSubscriber(Context context, String path, String name) {
        this.context = context;
        this.path = path;
        this.name = name;
        this.lastRefreshUiTime = System.currentTimeMillis();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        HttpLog.e("DownSubscriber:>>>> onError:" + e.getMessage());
    }

    @Override
    public void onNext(ResponseBody responseBody) {
        writeResponseBodyToDisk(path, name, context, responseBody);
    }

    private void writeResponseBodyToDisk(String path, String name, Context context, ResponseBody body) {
        //HttpLog.e("contentType:>>>>" + body.contentType().toString());
        if (!TextUtils.isEmpty(name)) {//text/html; charset=utf-8
            String type;
            if (!name.contains(".")) {
                type = body.contentType().toString();
                if (type.equals(APK_CONTENTTYPE)) {
                    fileSuffix = ".apk";
                } else if (type.equals(PNG_CONTENTTYPE)) {
                    fileSuffix = ".png";
                } else if (type.equals(JPG_CONTENTTYPE)) {
                    fileSuffix = ".jpg";
                } else {
                    fileSuffix = "." + body.contentType().subtype();
                }
                name = name + fileSuffix;
            }
        } else {
            name = System.currentTimeMillis() + fileSuffix;
        }

        path = Environment.getExternalStorageDirectory() + "/OneTouchShow";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        path = path + File.separator + name;
        path = path.replaceAll("//", "/");

        HttpLog.i("path:-->" + path);
        try {
            File futureStudioIconFile = new File(path);
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];

                final long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                HttpLog.d("file length: " + fileSize);
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    HttpLog.i("file download: " + fileSizeDownloaded + " of " + fileSize);
                    //下载进度
                    float progress = fileSizeDownloaded * 1.0f / fileSize;
                    long curTime = System.currentTimeMillis();
                    //每200毫秒刷新一次数据,防止频繁更新进度
                    if (curTime - lastRefreshUiTime >= 200 || progress == 1.0f) {
                        final long finalFileSizeDownloaded = fileSizeDownloaded;
                        Observable.just(finalFileSizeDownloaded).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                update(finalFileSizeDownloaded, fileSize, finalFileSizeDownloaded == fileSize);
                            }
                        });
                        lastRefreshUiTime = System.currentTimeMillis();
                    }
                }

                outputStream.flush();
                HttpLog.i("file downloaded: " + fileSizeDownloaded + " of " + fileSize);

                final String finalPath = path;
                Observable.just(finalPath).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
                    @Override
                    public void call(String aLong) {
                        onComplete(finalPath);
                    }
                });
                HttpLog.i("file downloaded: " + fileSizeDownloaded + " of " + fileSize);
                HttpLog.i("file downloaded: is sucess");

            } catch (IOException e) {
                finalonError(e);
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }


//                // 最后通知图库更新
//                try {
//                    MediaStore.Images.Media.insertImage(context.getContentResolver(), futureStudioIconFile.getAbsolutePath(), name, null);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                Uri uri = Uri.fromFile(futureStudioIconFile);
//                intent.setData(uri);
//                context.sendBroadcast(intent);

                if (name.contains(".mp4")) {
                    insertIntoMediaStore(context, true, futureStudioIconFile, 0);
                } else if (name.contains(".jpg") || name.contains(".png")) {
                    insertIntoMediaStore(context, false, futureStudioIconFile, 0);
                }
            }
        } catch (IOException e) {
            finalonError(e);
        }
    }

    private void finalonError(final Exception e) {
        onError(e);
    }


    public abstract void onComplete(String path);

    public abstract void update(long bytesRead, long contentLength, boolean done);

    protected abstract void onXError(String exception);

    //针对非系统影音资源文件夹
    public static void insertIntoMediaStore(Context context, boolean isVideo, File saveFile, long createTime) {
        ContentResolver mContentResolver = context.getContentResolver();
        if (createTime == 0) {

            createTime = System.currentTimeMillis();
        }
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.TITLE, saveFile.getName());
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, saveFile.getName());
        //值一样，但是还是用常量区分对待
        values.put(isVideo
                ? MediaStore.Video.VideoColumns.DATE_TAKEN
                : MediaStore.Images.ImageColumns.DATE_TAKEN, createTime);
        values.put(MediaStore.MediaColumns.DATE_MODIFIED, System.currentTimeMillis());
        values.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis());
        if (!isVideo) {
            values.put(MediaStore.Images.ImageColumns.ORIENTATION, 0);
        }
        values.put(MediaStore.MediaColumns.DATA, saveFile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.SIZE, saveFile.length());
//        values.put(MediaStore.MediaColumns.MIME_TYPE, isVideo ? "video/mp4" : "image/jpeg");
        //插入
        mContentResolver.insert(isVideo
                ? MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                : MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //strDir视频路径   通知相册更新
        //下载完视频后，发送广播，系统接收到广播就去扫描媒体库
        // 还有另外一种方式，将视频重命名，也会更新媒体库；相册中也可以看到视频
        Uri localUri = Uri.parse("file://" + saveFile);
        Intent localIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        localIntent.setData(localUri);
        context.sendBroadcast(localIntent);
    }
}

package itbour.onetouchshow;

import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;


import java.io.File;
import java.util.UUID;

import itbour.onetouchshow.base.BaseApplication;
import itbour.onetouchshow.utils.FileUtils;
import itbour.onetouchshow.utils.L_;

/**
 * 创建人：$ gyymz1993
 * 创建时间：2017/8/12 15:05
 */

public class AppCache {


    private static AppCache appCache;

    private AppCache() {

    }

    public static AppCache getInstance() {
        if (appCache == null) {
            synchronized (AppCache.class) {
                if (appCache == null) {
                    appCache = new AppCache();
                }
            }
        }
        return appCache;
    }


    //照片存放位置

    //语音存放位置

    //视频存放位置

    //照片存放位置

    //头像保存位置

    //Glide

    //语音存放位置
    public static final String AUDIO_SAVE_DIR = FileUtils.getDir("audio");
    //public static final String AUDIO_SAVE_DIR = FileUtils.createDirs("audio");
    //视频存放位置
    public static final String VIDEO_SAVE_DIR = FileUtils.getDir("video");
    //照片存放位置
    public static final String PHOTO_SAVE_DIR = FileUtils.getDir("photo");
    //头像保存位置
    public static final String HEADER_SAVE_DIR = FileUtils.getDir("header");


    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    public boolean initCreataAppCache() {
        return true;
    }


    public File getmAppDir() {
        return new File(mAppDir);
    }

    public File getmPicturesDir() {
        return new File(mPicturesDir);
    }

    public File getmVoicesDir() {
        return new File(mVoicesDir);
    }


    public boolean fileExists(String filePath, String fileName) {
        return new File(filePath + File.separator + fileName).exists();
    }


    public boolean voiceExists(String filePath, String fileName) {
        return new File(mVoicesDir + File.separator + fileName).exists();
    }

    public String voicePath(String fileName) {
        return new File(mVoicesDir + File.separator + fileName).getAbsolutePath();
    }


    public boolean videoExists(String filePath, String fileName) {
        return new File(mVideosDir + File.separator + fileName).exists();
    }

    public String videoPath(String fileName) {
        return new File(mVideosDir + File.separator + fileName).getAbsolutePath();
    }


    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_ADUIO = 2;
    private static final int TYPE_VIDEO = 3;

    /**
     * {@link #TYPE_IMAGE}<br/>
     * {@link #TYPE_ADUIO}<br/>
     * {@link #TYPE_VIDEO} <br/>
     *
     * @param type
     * @return
     */
    public static String getPublicFilePath(int type) {
        String fileDir = null;
        String fileSuffix = null;
        switch (type) {
            case TYPE_ADUIO:
                fileDir = AppCache.getInstance().mVoicesDir;
                fileSuffix = ".mp3";
                break;
            case TYPE_VIDEO:
                fileDir = AppCache.getInstance().mVideosDir;
                fileSuffix = ".mp4";
                break;
            case TYPE_IMAGE:
                fileDir = AppCache.getInstance().mPicturesDir;
                fileSuffix = ".jpg";
                break;
        }
        if (fileDir == null) {
            return null;
        }
        File file = new File(fileDir);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                return null;
            }
        }
        return fileDir + File.separator + UUID.randomUUID().toString().replaceAll("-", "") + fileSuffix;
    }

    public File getmVideosDir() {
        return new File(mVideosDir);
    }

    public String fileNameGenerator(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        int lastIndex = url.lastIndexOf("/");
        if (lastIndex == -1) {
            return url;
        }
        return url.substring(lastIndex, url.length());
    }

    public File getmFilesDir() {
        return new File(mFilesDir);
    }

    /* 文件缓存的目录 */
    public String mAppDir;
    public String mPicturesDir;
    public String mVoicesDir;
    public String mVideosDir;
    public String mFilesDir;
    public String mException;

    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    public void initCreateAppDir() {
        // L_.e(BaseApplication.getApplication() + "initCreateAppDir-----------");
        File file = BaseApplication.getApplication().getExternalFilesDir(null);
        if (file != null && file != null) {
            mAppDir = file.getAbsolutePath();
        }


        file = BaseApplication.getApplication().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (file != null && file.exists()) {
            file.mkdirs();
        }
        mPicturesDir = file.getAbsolutePath();

        file = BaseApplication.getApplication().getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        assert file != null;
        if (file != null && !file.exists()) {
            file.mkdirs();
        }
        mVoicesDir = file.getAbsolutePath();

        file = BaseApplication.getApplication().getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        assert file != null;
        if (file != null && !file.exists()) {
            file.mkdirs();
        }
        mVideosDir = file.getAbsolutePath();

        file = BaseApplication.getApplication().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (file != null && !file.exists()) {
            file.mkdirs();
        }
        mFilesDir = file.getAbsolutePath();


        file = BaseApplication.getApplication().getExternalFilesDir("Exception");
        if (file != null && !file.exists()) {
            file.mkdirs();
        }
        mException = file.getAbsolutePath();
    }
}

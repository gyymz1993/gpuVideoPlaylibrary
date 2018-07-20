package com.gup.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.widget.FrameLayout;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public class GLMediaPlayerWrapper implements VideoPlayerControl {
    public static final int STATE_ERROR = -1;          // 播放错误
    public static final int STATE_IDLE = 0;            // 播放未开始
    public static final int STATE_PREPARING = 1;       // 播放准备中
    public static final int STATE_PREPARED = 2;        // 播放准备就绪
    public static final int STATE_PLAYING = 3;         // 正在播放
    public static final int STATE_PAUSED = 4;          // 暂停播放
    /**
     * 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，缓冲区数据足够后恢复播放)
     **/
    public static final int STATE_BUFFERING_PLAYING = 5;
    /**
     * 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，此时暂停播放器，继续缓冲，缓冲区数据足够后恢复暂停)
     **/
    public static final int STATE_BUFFERING_PAUSED = 6;
    public static final int STATE_COMPLETED = 7;       // 播放完成
    public static final int PLAYER_NORMAL = 10;        // 普通播放器
    public static final int PLAYER_FULL_SCREEN = 11;   // 全屏播放器
    public static final int PLAYER_TINY_WINDOW = 12;   // 小窗口播放器
    public AddWeaterFilterListener addWeaterFilterListener;
    FullScreenListener fullScreenListener;
    Handler handler = new Handler();
    private VideoInfo info;
    private MediaPlayer mMediaPlayer;    //current player
    private String mPath;          //video src list
    private Surface surface;
    private IMediaCallback mCallback;
    private Context context;
    private int mCurrentState = STATE_IDLE; //播放状态
    private int mWindowState = PLAYER_NORMAL;  //视频窗口态度
    /**
     * 缓冲进度
     */
    private int mBufferPercent;
    /**
     * 视频播放控制器
     */
    private VideoPlayerController mController;
    /**
     * MediaPlayer准备好播放监听
     */


    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
            mCurrentState = STATE_PREPARED;
            updateVideoPlayerState();
        }
    };
    /**
     * Video播放尺寸大小改变监听
     */
    private MediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangeListener = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            //LogUtil.d("onVideoSizeChanged ——> width：" + width + "， height：" + height + "sar_num:" + sar_num + "sar_den:" + sar_den);
            //mMediaPlayer.adaptVideoSize(width, height);
            if (mCallback != null) {
                VideoInfo videoInfo = new VideoInfo();
                videoInfo.width = width;
                videoInfo.height = height;
                mCallback.onVideoChanged(videoInfo);
            }
        }
    };
    /**
     * mediaPlayer播放完成监听
     */
    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(final MediaPlayer mp) {
            // mCurrentState = STATE_COMPLETED; //播放完成
            // updateVideoPlayerState();
            long position = mp.getCurrentPosition();
            long duration = mp.getDuration();
            Log.e("TAG", "OnCompletionListener position" + position + "///duration:" + duration);
            //重播i
            mCurrentState = STATE_COMPLETED;
            updateVideoPlayerState();

            /**
             * 如果不是循环播放 则暂停
             */
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                   try {
                       mp.pause();
                       seekTo(0);
                   }catch (Exception e){

                   }
                }
            }, 0);

        }
    };
    /**
     * Mediaplayer播放错误监听
     */
    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return true;
        }
    };
    /**
     * MediaPlayer缓冲状态改变监听
     */
    private MediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            mBufferPercent = percent;
        }
    };
    private MediaPlayer.OnInfoListener mOnInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                //播放器开始渲染
                mCurrentState = STATE_PLAYING;
                updateVideoPlayerState();
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                if (mCurrentState == STATE_PAUSED || mCurrentState == STATE_BUFFERING_PAUSED) {
                    mCurrentState = STATE_BUFFERING_PAUSED;
                    updateVideoPlayerState();
                } else {
                    mCurrentState = STATE_BUFFERING_PLAYING;
                    updateVideoPlayerState();
                }
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                if (mCurrentState == STATE_BUFFERING_PAUSED) {
                    mCurrentState = STATE_PAUSED;
                    updateVideoPlayerState();
                } else if (mCurrentState == STATE_BUFFERING_PLAYING) {
                    mCurrentState = STATE_PLAYING;
                    updateVideoPlayerState();
                }
            } else {

            }
            return true;
        }
    };
    /**
     * get video info and store
     *
     * @param dataSource 视频播放的源文件
     */
    private String duration;

    public GLMediaPlayerWrapper(Context contex) {
        this.context = context;
    }

    public static String videoDuration(int netWorkType, String videoUrl) {
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        if (netWorkType == 1) {
            metadataRetriever.setDataSource(videoUrl, new HashMap());
        } else {
            metadataRetriever.setDataSource(videoUrl);
        }

        String duration = metadataRetriever.extractMetadata(9);
        metadataRetriever.release();
        return duration;
    }

    public int getmCurrentState() {
        return mCurrentState;
    }

    /**
     * 关联视频控制器
     */
    public void setController(VideoPlayerController controller) {
        mController = controller;
        mController.setVideoPlayer(this);
    }

    public int getCurVideoDuration() {
        return info.duration;
    }

    public void setOnCompletionListener(IMediaCallback callback) {
        this.mCallback = callback;
    }

    public void setmWindowState(int mWindowState) {
        this.mWindowState = mWindowState;
    }

    public void setDataSource(String dataSource) {
        try {
            this.mPath = dataSource;
            MediaMetadataRetriever retr = new MediaMetadataRetriever();
            String path = dataSource;
            if (path.startsWith("http") || path.startsWith("https")) {
                retr.setDataSource(path, new HashMap());
            } else {
                Uri uri = Uri.parse(path);
                retr.setDataSource(context, uri);
            }
            String rotation = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            String width = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String height = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            duration = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            info = new VideoInfo();
            info.path = path;
            info.rotation = Integer.parseInt(rotation);
            info.width = Integer.parseInt(width);
            info.height = Integer.parseInt(height);
            info.duration = Integer.parseInt(duration);
            onVideoChanged(info);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void onVideoChanged(VideoInfo info) {
        if (info.rotation == 90 || info.rotation == 270) {
            int tmpS = info.height;
            info.height = info.width;
            info.width = tmpS;
        }
    }

    public VideoInfo getInfo() {
        return info;
    }

    public void setSurface(Surface surface) {
        this.surface = surface;
    }

    public void setMediaPlayerSurface(Surface surface) {
        this.surface = surface;
        mMediaPlayer.setSurface(surface);

    }

    /**
     */
    public MediaPlayer getMediaPlayer(Context context) {
        MediaPlayer mediaplayer = new MediaPlayer();
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
            return mediaplayer;
        }
        try {
            Class<?> cMediaTimeProvider = Class.forName("android.media.MediaTimeProvider");
            Class<?> cSubtitleController = Class.forName("android.media.SubtitleController");
            Class<?> iSubtitleControllerAnchor = Class.forName("android.media.SubtitleController$Anchor");
            Class<?> iSubtitleControllerListener = Class.forName("android.media.SubtitleController$Listener");
            Constructor constructor = cSubtitleController.getConstructor(
                    new Class[]{Context.class, cMediaTimeProvider, iSubtitleControllerListener});
            Object subtitleInstance = constructor.newInstance(context, null, null);
            Field f = cSubtitleController.getDeclaredField("mHandler");
            f.setAccessible(true);
            try {
                f.set(subtitleInstance, new Handler());
            } catch (IllegalAccessException e) {
                return mediaplayer;
            } finally {
                f.setAccessible(false);
            }
            Method setsubtitleanchor = mediaplayer.getClass().getMethod("setSubtitleAnchor",
                    cSubtitleController, iSubtitleControllerAnchor);
            setsubtitleanchor.invoke(mediaplayer, subtitleInstance, null);
        } catch (Exception e) {
            // LogUtil.d(TAG,"getMediaPlayer crash ,exception = "+e);
        }
        return mediaplayer;
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public void releaseMediaplayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void prepare() throws IOException {

        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);  //在播放时屏幕一直开启着
            mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangeListener);
            mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
            mMediaPlayer.setOnErrorListener(mOnErrorListener);
            mMediaPlayer.setOnInfoListener(mOnInfoListener);
            mMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
            mMediaPlayer.setLooping(false);
            //设置surface
            mMediaPlayer.setSurface(surface);
            if (mCallback != null) {
                mCallback.onVideoChanged(info);
            }
//            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
//                @RequiresApi(api = Build.VERSION_CODES.M)
//                public void onPrepared(MediaPlayer var1) {
//                    PlaybackParams params = null;
//                    params = mMediaPlayer.getPlaybackParams();
//                    params.setSpeed(5.0f);
//                    mMediaPlayer.setPlaybackParams(params);
//                }
//            });
        }

//        mMediaPlayer = new MediaPlayer();
//        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mMediaPlayer.setScreenOnWhilePlaying(true);  //在播放时屏幕一直开启着
//        mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
//        mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangeListener);
//        mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
//        mMediaPlayer.setOnErrorListener(mOnErrorListener);
//        mMediaPlayer.setOnInfoListener(mOnInfoListener);
//        mMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
//        mMediaPlayer.setDataSource(mPath);
//        mMediaPlayer.setLooping(true);
//        // 把视频输出到SurfaceView上
//        //mCurMediaPlayer.setDisplay(mSurfaceHolder);
//        mMediaPlayer.prepare();

    }


//    public void start() {
//        mMediaPlayer.setSurface(surface);
//        mMediaPlayer.start();
//        if (mCallback != null) {
//            mCallback.onVideoChanged(info);
//        }
//    }

    /**
     * 让mediaPlayer播放
     */
    @SuppressLint("NewApi")
    private void mediaPlayerStart() {
        try {
            //设置数据源
            mMediaPlayer.setDataSource(mPath);
            //异步网络准备
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        mMediaPlayer.stop();
    }

    @Override
    public void seekTo(int pos) {
        mMediaPlayer.seekTo(pos);
    }

    @Override
    public void start() {
        mediaPlayerStart();
    }

    @Override
    public void pause() {
        if (mCurrentState == STATE_PLAYING || mCurrentState == STATE_BUFFERING_PLAYING) {
            if (mMediaPlayer != null) {
                mMediaPlayer.pause();
                mCurrentState = mCurrentState == STATE_PLAYING ? STATE_PAUSED : STATE_BUFFERING_PAUSED;
                updateVideoPlayerState();
            }
        }
    }

    @Override
    public void restart() {
        if (mCurrentState == STATE_PAUSED || mCurrentState == STATE_BUFFERING_PAUSED) {
            mMediaPlayer.start();
            mCurrentState = mCurrentState == STATE_PAUSED ? STATE_PLAYING : STATE_BUFFERING_PLAYING;
            updateVideoPlayerState();
        } else if (mCurrentState == STATE_COMPLETED) {
            if (mMediaPlayer!=null){
                mMediaPlayer.start();
                mCurrentState = STATE_PLAYING;
                updateVideoPlayerState();
            }

        }
    }

    @Override
    public void release() {
        if (mController != null) {
            mController.reset();
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.release();  //释放资源
            mMediaPlayer = null;
        }
        if (surface != null) {
            surface.release();
            surface = null;
        }
    }

    @Override
    public boolean isIdle() {
        return mCurrentState == STATE_IDLE;
    }

    @Override
    public boolean isError() {
        return mCurrentState == STATE_ERROR;
    }

    @Override
    public boolean isPreparing() {
        return mCurrentState == STATE_PREPARING;
    }

    @Override
    public boolean isPrepared() {
        return mCurrentState == STATE_PREPARED;
    }

    @Override
    public boolean isBufferingPlaying() {
        return mCurrentState == STATE_BUFFERING_PLAYING;
    }

    @Override
    public boolean isBufferingPaused() {
        return mCurrentState == STATE_BUFFERING_PAUSED;
    }

    @Override
    public boolean isPlaying() {
        return mCurrentState == STATE_PLAYING;
    }

    @Override
    public boolean isPaused() {
        return mCurrentState == STATE_PAUSED;
    }

    @Override
    public boolean isCompleted() {
        return mCurrentState == STATE_COMPLETED;
    }

    @Override
    public int getDuration() {
        return mMediaPlayer != null ? mMediaPlayer.getDuration() : Integer.valueOf(duration);
    }

    @Override
    public int getCurrentProgress() {


//        Log.e("getCurrentProgress", mMediaPlayer.getCurrentPosition() + "");
//        if (mMediaPlayer.getCurrentPosition() > 5000) {
//
//            if (addWeaterFilterListener != null) {
//                addWeaterFilterListener.removerWeater();
//            }
//        } else {
//            if (addWeaterFilterListener != null) {
//                addWeaterFilterListener.addWeater();
//            }
//        }
        return mMediaPlayer != null ? mMediaPlayer.getCurrentPosition() : 0;
    }

    @Override
    public int getBufferPercent() {
        return mBufferPercent;
    }

    @Override
    public FrameLayout getContainer() {
        return null;
    }

    @Override
    public boolean isFullScreen() {
        return mWindowState == PLAYER_FULL_SCREEN;
    }

    @Override
    public boolean isNormalScreen() {
        return mWindowState == PLAYER_NORMAL;
    }

    @Override
    public boolean isTinyScreen() {
        return mWindowState == PLAYER_TINY_WINDOW;
    }

    //已经是全屏就不需要操作了
    @Override
    public void enterFullScreen() {
        if (mWindowState == PLAYER_FULL_SCREEN) return;
        if (fullScreenListener != null) {
            fullScreenListener.enterFullScreen();
        }
    }

    public void setFullScreenListener(FullScreenListener fullScreenListener) {
        this.fullScreenListener = fullScreenListener;
    }

    /**
     * 退出全屏，移除mTextureView和mController，并添加到非全屏的容器中。
     * 切换竖屏时需要在manifest的activity标签下添加
     * android:configChanges="orientation|keyboardHidden|screenSize"配置，
     * 以避免Activity重新走生命周期.
     *
     * @return true退出全屏成功.
     */
    @Override
    public boolean exitFullScreen() {
        if (mWindowState == PLAYER_FULL_SCREEN) {
            if (fullScreenListener != null) {
                fullScreenListener.exitFullScreen();
            }
            return true;
        }
        return false;
    }

    public void updateVideoPlayerState() {
        mController.setControllerState(mCurrentState, mWindowState);
    }

    @Override
    public void enterTinyScreen() {
        if (mWindowState == PLAYER_TINY_WINDOW) return;
        if (fullScreenListener != null) {
            fullScreenListener.enterTinyScreen();
        }
    }

    @Override
    public boolean exitTinyScreen() {
        if (mWindowState == PLAYER_TINY_WINDOW) {
            if (fullScreenListener != null) {
                fullScreenListener.exitTinyScreen();
            }
            return true;
        }
        return false;
    }

    public void setAddWeaterFilterListener(AddWeaterFilterListener addWeaterFilterListener) {
        this.addWeaterFilterListener = addWeaterFilterListener;
    }

    public interface FullScreenListener {
        /**
         * 全屏
         */
        void enterFullScreen();

        void exitFullScreen();

        /**
         * 小窗口
         */
        void enterTinyScreen();

        void exitTinyScreen();
    }

    public interface IMediaCallback {
        void onVideoChanged(VideoInfo info);
    }

    public interface AddWeaterFilterListener {
        void addWeater();

        void removerWeater();
    }
}

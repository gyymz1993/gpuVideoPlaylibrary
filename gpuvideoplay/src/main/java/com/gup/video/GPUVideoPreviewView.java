package com.gup.video;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.BaseGPUImageRenderer;
import jp.co.cyberagent.android.gpuimage.Rotation;

public class GPUVideoPreviewView extends GLSurfaceView {
    /**
     * 视频播放状态的回调
     */
    private GPUImageRenderer mRenderer;
    private GPUImageFilterGroup filterGroup;
    private VideoPlayerController mController;
    private GLMediaPlayerWrapper mMediaPlayer;
    private MeasureHelper mMeasureHelper;

    public GPUVideoPreviewView(Context context) {
        this(context, null);
    }

    public GPUVideoPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GPUImageRenderer getRenderer() {
        return mRenderer;
    }

    public GLMediaPlayerWrapper getMediaPlayer() {
        return mMediaPlayer;
    }
    //播放速度

    public void setMediaPlayer(final MediaPlayer mMediaPlayer) {
        mRenderer.setUpSurfaceTexture(mMediaPlayer);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void changePlayerSpeed(float speed) {
        if (mMediaPlayer == null) return;
        PlaybackParams playbackParams = mMediaPlayer.getMediaPlayer().getPlaybackParams();
        playbackParams.setSpeed(speed);
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.getMediaPlayer().setPlaybackParams(playbackParams);
        } else {
            mMediaPlayer.getMediaPlayer().setPlaybackParams(playbackParams);
            mMediaPlayer.pause();
        }
        Log.e("changePlayerSpeed ", mMediaPlayer.getMediaPlayer().getPlaybackParams().getSpeed() + "");
    }

    private void init(Context context) {
        Log.e("TAG", "init---------->>>> init  GPUVideoPreviewView");
        mMeasureHelper = new MeasureHelper(this);

        filterGroup = new GPUImageFilterGroup();
        filterGroup.addFilter(new GPUImageExtTexFilter());
        filterGroup.addFilter(new GPUImageFilter());
        //初始化Drawer和VideoPlayer
        mMediaPlayer = new GLMediaPlayerWrapper(context);
        // mController = new VideoPlayerController(context);
        mRenderer = new GPUImageRenderer(filterGroup);
        setEGLContextClientVersion(DEBUG_LOG_GL_CALLS);
        setRenderer(mRenderer);
        //setRenderMode(RENDERMODE_CONTINUOUSLY);
        setPreserveEGLContextOnPause(true);
        //setCameraDistance(100);
        Log.e("TAG", "init---------->>>> init  GPUVideoPreviewView" + mRenderer);
        //setVideoPlayerController(mController);
    }

    public void setFilter(GPUImageFilter filter) {
        mRenderer.setFilter(filter);
    }

    public void setSourceSize(int imageWidth, int imageHeight, Rotation rotation) {

        if (imageWidth > 0 && imageHeight > 0) {
            setVideoSize(imageWidth,imageHeight,rotation);
        }

        mRenderer.setSourceSize(imageWidth, imageHeight, rotation);
        Log.e("TAG", "init---------->>>> init  GPUVideoPreviewView" + mRenderer);
        mRenderer.setRotation(rotation);



    }

    public void setVideoPlayerController(VideoPlayerController videoPlayerController) {
        mController = videoPlayerController;
        mMediaPlayer.setController(videoPlayerController);
    }

    /**
     * 设置视频的播放地址
     */
    public void setVideoPath(String path) {
        mMediaPlayer.setSurface(mRenderer.getSurface());
        mMediaPlayer.setDataSource(path);
        try {
            mMediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
        setMediaPlayer(mMediaPlayer.getMediaPlayer());
        setSourceSize(mMediaPlayer.getInfo().width, mMediaPlayer.getInfo().height, getRotation(mMediaPlayer.getInfo().rotation));
    }


    public Rotation getRotation(int rotations) {
        Rotation rotation;
        if (rotations == 90 || rotations == 270) {
            rotation = Rotation.ROTATION_90;
        } else {
            rotation = Rotation.NORMAL;
        }
        return rotation;

    }

    /**
     * 切换视频源
     */
    public void chageVideo(String path) {
        mMediaPlayer.releaseMediaplayer();
        try {
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
        setMediaPlayer(mMediaPlayer.getMediaPlayer());
        setSourceSize(mMediaPlayer.getInfo().width, mMediaPlayer.getInfo().height, getRotation(mMediaPlayer.getInfo().rotation));
    }


    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void onStart() {
        mMediaPlayer.start();
    }

// --------------------
// Layout & Measure
    public void setVideoSize(int videoWidth, int videoHeight,Rotation rotation) {
        if (videoWidth > 0 && videoHeight > 0) {
            mMeasureHelper.setVideoSize(videoWidth, videoHeight);
            mMeasureHelper.setAspectRatio(rotation.asInt());
          //  getHolder().setFixedSize(videoWidth, videoHeight);
            requestLayout();
        }
    }

//    public void setAspectRatio(int aspectRatio) {
//        mMeasureHelper.setAspectRatio(aspectRatio);
//        requestLayout();
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMeasureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mMeasureHelper.getMeasuredWidth(), mMeasureHelper.getMeasuredHeight());
    }

    @Override
    public void setRotation(float rotation) {
        if (rotation != getRotation()) {
            super.setRotation(rotation);
            requestLayout();
        }
    }

    public void onDestroy() {
        /**
         * 视频播放状态的回调
         */
        mRenderer = null;
        filterGroup.onDestroy();
        mController = null;
        mMediaPlayer.release();
    }


    /**
     * Pause the rendering thread, optionally tearing down the EGL context
     * depending upon the value of {@link #setPreserveEGLContextOnPause(boolean)}.
     * <p>
     * This method should be called when it is no longer desirable for the
     * GLSurfaceView to continue rendering, such as in response to
     * {@link android.app.Activity#onStop Activity.onStop}.
     * <p>
     * Must not be called before a renderer has been set.
     */
    // @Override
    public void onPause() {
        super.onPause();
        mController.onPause();
    }

    /**
     * Resumes the rendering thread, re-creating the OpenGL context if necessary. It
     * is the counterpart to {@link #onPause()}.
     * <p>
     * This method should typically be called in
     * {@link android.app.Activity#onStart Activity.onStart}.
     * <p>
     * Must not be called before a renderer has been set.
     */
    // @Override
    public void onResume() {
        super.onResume();
        // requestRender();
        mController.onRestart();
    }


    /**
     * start play video
     */
    public void reStart() {
        mController.onRestart();
    }
}

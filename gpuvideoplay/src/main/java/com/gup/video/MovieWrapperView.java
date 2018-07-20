package com.gup.video;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import static com.gup.video.GLMediaPlayerWrapper.PLAYER_FULL_SCREEN;
import static com.gup.video.GLMediaPlayerWrapper.PLAYER_NORMAL;

public class MovieWrapperView extends FrameLayout {

    protected FrameLayout mContainer;
    protected GPUVideoPreviewView videoPreviewView;
    private VideoPlayerController mController;


    public MovieWrapperView(@NonNull Context context) {
        this(context, null);
    }

    public MovieWrapperView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MovieWrapperView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    public GPUVideoPreviewView getVideoPreviewView() {
        return videoPreviewView;
    }

    /**
     * 切换播放地址
     */
    public void setChangeVideoPath(String path) {
        videoPreviewView.chageVideo(path);
    }

    /**
     * 设置视频的播放地址
     */
    public void setVideoPath(String path) {
        videoPreviewView.setVideoPath(path);
    }

//    public void setOnFilterChangeListener(SlideGpuFilterGroup.OnFilterChangeListener listener) {
//        videoPreviewView.setOnFilterChangeListener(listener);
//    }

    /**
     * 获取当前视频的长度
     */
    public int getVideoDuration() {
        return videoPreviewView.getMediaPlayer().getCurVideoDuration();
    }

    /**
     * pause play
     */
    public void pause() {
        //mController.onPause();
       videoPreviewView.onPause();
    }

    /**
     * start play video
     */
    public void resume() {
        videoPreviewView.onResume();
       // mController.onRestart();
    }


    public GLMediaPlayerWrapper getMediaPlayer() {
        return videoPreviewView.getMediaPlayer();
    }

    /**
     * 释放资源
     */
    public boolean onBackPress() {
        if (getMediaPlayer().isFullScreen()) {
            getMediaPlayer().exitFullScreen();
            return true;
        } else if (getMediaPlayer().isTinyScreen()) {
            getMediaPlayer().exitTinyScreen();  //退出小屏
            return true;
        }
        if (getMediaPlayer() != null) {
            getMediaPlayer().release();
        }
        return false;
    }

    /**
     * 跳转到指定的时间点，只能跳到关键帧
     */
    public void seekTo(int time) {
        videoPreviewView.getMediaPlayer().seekTo(time);
    }

    protected void init(Context context) {
        mContainer = new FrameLayout(context);
        mController = new VideoPlayerController(context);
        mContainer.setBackgroundColor(Color.BLACK);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mContainer, params);
        Log.e("TAG", "init---------->>>> init");
        videoPreviewView = new GPUVideoPreviewView(context);
        videoPreviewView.setVideoPlayerController(mController);
        videoPreviewView.getMediaPlayer().setFullScreenListener(new GLMediaPlayerWrapper.FullScreenListener() {
            @Override
            public void enterFullScreen() {
                setEnterFullScreen();
            }

            @Override
            public void exitFullScreen() {
                setExitFullScreen();
            }

            @Override
            public void enterTinyScreen() {
                setEnterTinyScreen();
            }

            @Override
            public void exitTinyScreen() {
                setExitTinyScreen();
            }
        });

        addVideoPreviewView();
        addControllerView();
    }

    protected void addVideoPreviewView() {
        mContainer.removeView(videoPreviewView);
        mContainer.setBackgroundColor(Color.BLACK);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity= Gravity.CENTER;
        mContainer.addView(videoPreviewView, params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        setMeasuredDimension(measuredWidth, measuredWidth);
    }

    public void addControllerView() {
        mContainer.removeView(mController);
        // videoPlayerControllerView.showVideoLayout();
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(mController, params);
    }

    public void setEnterFullScreen() {
        //已经是全屏就不需要操作了
        VideoPlayerUtil.hideActionBar(getContext()); //隐藏状态栏
        VideoPlayerUtil.scanForActivity(getContext()).setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);  //设置屏幕方向 横向
        this.removeView(mContainer);
        ViewGroup contentView = (ViewGroup) VideoPlayerUtil.scanForActivity(getContext())
                .findViewById(android.R.id.content);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        contentView.addView(mContainer, params);
        videoPreviewView.getMediaPlayer().setmWindowState(PLAYER_FULL_SCREEN);
        videoPreviewView.getMediaPlayer().updateVideoPlayerState();

    }


    /**
     * 退出全屏，移除mTextureView和mController，并添加到非全屏的容器中。
     * 切换竖屏时需要在manifest的activity标签下添加
     * android:configChanges="orientation|keyboardHidden|screenSize"配置，
     * 以避免Activity重新走生命周期.
     */
    public void setExitFullScreen() {
        VideoPlayerUtil.showActionBar(getContext());  //显示状态栏
        VideoPlayerUtil.scanForActivity(getContext()).setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  //设置屏幕方向 竖向
        ViewGroup contentView = (ViewGroup) VideoPlayerUtil.scanForActivity(getContext())
                .findViewById(android.R.id.content);
        contentView.removeView(mContainer);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mContainer, params);
        videoPreviewView.getMediaPlayer().setmWindowState(PLAYER_NORMAL);
        videoPreviewView.getMediaPlayer().updateVideoPlayerState();
        //videoPreviewView.onPause();
    }

    public void setEnterTinyScreen() {
        this.removeView(mContainer);
        ViewGroup contentView = (ViewGroup) VideoPlayerUtil.scanForActivity(getContext())
                .findViewById(android.R.id.content);
        LayoutParams params = new LayoutParams(
                (int) (VideoPlayerUtil.getScreenWidth(getContext()) * 0.6),
                (int) (VideoPlayerUtil.getScreenWidth(getContext()) * 0.6 / 16 * 9));
        contentView.addView(mContainer, params);
        videoPreviewView.getMediaPlayer().setmWindowState(PLAYER_NORMAL);
        videoPreviewView.getMediaPlayer().updateVideoPlayerState();
    }

    public void setExitTinyScreen() {
        ViewGroup contentView = (ViewGroup) VideoPlayerUtil.scanForActivity(getContext())
                .findViewById(android.R.id.content);
        contentView.removeView(mContainer);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mContainer, params);
        videoPreviewView.getMediaPlayer().setmWindowState(PLAYER_NORMAL);
        videoPreviewView.getMediaPlayer().updateVideoPlayerState();
    }


}

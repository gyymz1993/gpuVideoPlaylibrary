package com.gup.video;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Formatter;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.gup.video.GLMediaPlayerWrapper.STATE_BUFFERING_PAUSED;


/**
 * 视频播放控制器
 */
public class VideoPlayerController extends FrameLayout implements
        View.OnClickListener,
        SeekBar.OnSeekBarChangeListener,
        View.OnTouchListener {
    private final static int MSG_ID = 0x01;
    private VideoPlayerControl mVideoPlayerControl;
    private Context mContext;
    private ImageView mImage;
    private ImageView mCenterStart;
    private LinearLayout mTop;
    private ImageView mBack;
    private TextView mTitle;
    private LinearLayout mBottom;
    private ImageView mRestartPause;
    private TextView mPosition;
    private TextView mDuration;
    private SeekBar mSeek;
    private ImageView mFullScreen;
    private LinearLayout mLoading;
    private TextView mLoadText;
    private LinearLayout mError;
    private TextView mRetry;
    private LinearLayout mCompleted;
    private TextView mReplay;
    private TextView mShare;
    private boolean topBottomVisible = false;
    private String mUrl;
    private int screenWidth;
    private int screenHeight;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            updateProgress();
            handler.sendEmptyMessageDelayed(MSG_ID, 1000);
        }
    };
    private float startX;
    private float startY;
    private int mCurrentState; //播放状态
    private int mWindowState;
    private Timer mUpdateProgressTimer;
    private TimerTask mUpdateProgressTimerTask;
    /**
     * 记录拖动的时候是否是播放状态  拖动的时候保持暂停状态
     */
    private boolean isOnTrackingState = false;
    private boolean isTracking = false;

    public VideoPlayerController(@NonNull Context context) {
        super(context);
        init(context);
    }

    /**
     * 将毫秒数格式化为"##:##"的时间
     *
     * @param milliseconds 毫秒数
     * @return ##:##
     */
    public static String formatTime(int milliseconds) {
        if (milliseconds <= 0 || milliseconds >= 24 * 60 * 60 * 1000) {
            return "00:00";
        }
        int totalSeconds = milliseconds / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * 将毫秒数格式化为"##:##"的时间
     *
     * @param milliseconds 毫秒数
     * @return ##:##
     */
    public static String formatTime(long milliseconds) {
        if (milliseconds <= 0 || milliseconds >= 24 * 60 * 60 * 1000) {
            return "00:00";
        }
        long totalSeconds = milliseconds / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * 将毫秒数格式化为"##:##"的时间
     *
     * @param milliseconds 毫秒数
     * @return ##:##
     */
    public static String setmDurationformatTime(long milliseconds) {


        if (milliseconds <= 0 || milliseconds >= 24 * 60 * 60 * 1000) {
            return "00:00";
        }
        long totalSeconds = milliseconds / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private void init(Context context) {

        inflate(context, R.layout.video_palyer_controller, this);
        mContext = context;
        screenWidth = DensityUtils.getScreenWH(context)[0];
        screenHeight = DensityUtils.getScreenWH(context)[1];

        mCenterStart = (ImageView) findViewById(R.id.center_start);
        mImage = (ImageView) findViewById(R.id.image);

        mTop = (LinearLayout) findViewById(R.id.top);
        mBack = (ImageView) findViewById(R.id.back);
        mTitle = (TextView) findViewById(R.id.title);

        mBottom = (LinearLayout) findViewById(R.id.bottom);
        mRestartPause = (ImageView) findViewById(R.id.restart_or_pause);
        mPosition = (TextView) findViewById(R.id.position);
        mDuration = (TextView) findViewById(R.id.duration);
        mSeek = (SeekBar) findViewById(R.id.seek);
        mFullScreen = (ImageView) findViewById(R.id.full_screen);

        mLoading = (LinearLayout) findViewById(R.id.loading);
        mLoadText = (TextView) findViewById(R.id.load_text);

        mError = (LinearLayout) findViewById(R.id.error);
        mRetry = (TextView) findViewById(R.id.retry);

        mCompleted = (LinearLayout) findViewById(R.id.completed);
        mReplay = (TextView) findViewById(R.id.replay);
        mShare = (TextView) findViewById(R.id.share);

        mCenterStart.setVisibility(GONE);
        mCenterStart.setOnClickListener(this);
        this.setOnClickListener(this);
        mReplay.setOnClickListener(this);
        mSeek.setOnSeekBarChangeListener(this);
        mRestartPause.setOnClickListener(this);
        mFullScreen.setVisibility(GONE);
        mFullScreen.setOnClickListener(this);
        mBack.setOnClickListener(this);
        setOnTouchListener(this);
    }

    public void setVideoPlayer(VideoPlayerControl videoPlayerControl) {
        mVideoPlayerControl = videoPlayerControl;
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setImage(String url) {
        mUrl = url;
//        Glide.with(mContext)
//                .load(url)
//                .placeholder(R.drawable.img_default)
//                .into(mImage);
    }

    @Override
    public void onClick(View v) {
        if (v == mCenterStart) {
//            if(mVideoPlayerControl.isIdle()){
            mVideoPlayerControl.start();
//            }
        } else if (v == this) {
            if (mVideoPlayerControl.isPlaying()
                    || mVideoPlayerControl.isPaused()
                    || mVideoPlayerControl.isBufferingPlaying()
                    || mVideoPlayerControl.isBufferingPaused()) {
                setTopBottomVisible(!topBottomVisible);
            }
        } else if (v == mReplay) {
            mVideoPlayerControl.release();
            mVideoPlayerControl.start();
            mCompleted.setVisibility(GONE);
        } else if (v == mShare) {
            Toast.makeText(mContext, "分享", Toast.LENGTH_SHORT).show();
        } else if (v == mRestartPause) {  //暂停或者播放
            if (mVideoPlayerControl.isPlaying() || mVideoPlayerControl.isBufferingPlaying()) {
                mVideoPlayerControl.pause();
            } else if (mVideoPlayerControl.isPaused() || mVideoPlayerControl.isBufferingPaused()) {
                mVideoPlayerControl.restart();
            }
            if (mVideoPlayerControl.isCompleted()) {
                mVideoPlayerControl.restart();
            }
        } else if (v == mFullScreen) {
            if (mVideoPlayerControl.isNormalScreen()) {
                mVideoPlayerControl.enterFullScreen();
            } else if (mVideoPlayerControl.isFullScreen()) {
                mVideoPlayerControl.exitFullScreen();
            }
        } else if (v == mBack) {
            if (mVideoPlayerControl.isFullScreen()) {
                mVideoPlayerControl.exitFullScreen();
            }
        }
    }

    /**
     * 设置头部和底部布局是否隐藏
     */
    private void setTopBottomVisible(boolean topBottomVisible) {
        mTop.setVisibility(topBottomVisible ? VISIBLE : INVISIBLE);
        mBottom.setVisibility(topBottomVisible ? VISIBLE : INVISIBLE);
        this.topBottomVisible = topBottomVisible;

        //3秒显示还没有关闭就自动关闭
        if (topBottomVisible) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    mTop.setVisibility(INVISIBLE);
                    mBottom.setVisibility(INVISIBLE);
                    VideoPlayerController.this.topBottomVisible = false;
                }
            }, 8000);
        }
    }

    public int getCurrentState() {
        return mCurrentState;
    }

    /**
     * 设置播放器工作状态
     */
    public void setControllerState(int playState, int windowState) {
        mCurrentState = playState;
        mWindowState = windowState;
        switch (playState) {
            case GLMediaPlayerWrapper.STATE_IDLE:
                mBottom.setVisibility(GONE);
                break;
            case GLMediaPlayerWrapper.STATE_PREPARING:
                mImage.setVisibility(GONE);
                mLoading.setVisibility(VISIBLE);
                mLoadText.setText("正在准备...");
                mCenterStart.setVisibility(GONE);
                mBack.setVisibility(GONE);
                mTop.setVisibility(GONE);
                mBottom.setVisibility(GONE);
                break;
            case GLMediaPlayerWrapper.STATE_PREPARED:
                startUpdateProgress();
                break;
            case GLMediaPlayerWrapper.STATE_BUFFERING_PLAYING: //播放时缓冲
                mLoading.setVisibility(VISIBLE);
                mLoadText.setText("正在缓冲");
                mRestartPause.setImageResource(R.drawable.ic_player_pause);
                break;
            case STATE_BUFFERING_PAUSED:  //暂停时缓冲
                mLoading.setVisibility(VISIBLE);
                mLoadText.setText("正在缓冲");
                mRestartPause.setImageResource(R.drawable.ic_player_start);
                break;
            case GLMediaPlayerWrapper.STATE_PLAYING:
                mLoading.setVisibility(GONE);
                mRestartPause.setImageResource(R.drawable.ic_player_pause);
                break;
            case GLMediaPlayerWrapper.STATE_PAUSED:
                mLoading.setVisibility(GONE);
                mRestartPause.setImageResource(R.drawable.ic_player_start);
                break;
            case GLMediaPlayerWrapper.STATE_COMPLETED:
                cancelUpdateProgress();
                onReset();
                mCurrentState = GLMediaPlayerWrapper.STATE_PAUSED;
                setControllerState(mCurrentState, mWindowState);
                break;
        }

        switch (windowState) {
            case GLMediaPlayerWrapper.PLAYER_FULL_SCREEN:
                mBack.setVisibility(VISIBLE);
                //mFullScreen.setVisibility(VISIBLE);
                //mFullScreen.setImageResource(R.drawable.ic_player_shrink);
                break;
            case GLMediaPlayerWrapper.PLAYER_NORMAL:
                mBack.setVisibility(GONE);
                // mFullScreen.setVisibility(VISIBLE);
                // mFullScreen.setImageResource(R.drawable.ic_player_enlarge);
                break;
            case GLMediaPlayerWrapper.PLAYER_TINY_WINDOW:
                mBack.setVisibility(GONE);
                mFullScreen.setVisibility(GONE);
                break;
        }
    }

    protected void onReset() {
        topBottomVisible = false;
        mSeek.setProgress(0);
        mSeek.setSecondaryProgress(0);
        mPosition.setText("00:00");
    }

    /**
     * 更新进度
     */
    private void startUpdateProgress() {
        // handler.sendEmptyMessageDelayed(MSG_ID, 300);
        startUpdateProgressTimer();
    }

    /**
     * 开启更新进度的计时器。
     */
    protected void startUpdateProgressTimer() {
        cancelUpdateProgressTimer();
        if (mUpdateProgressTimer == null) {
            mUpdateProgressTimer = new Timer();
        }
        if (mUpdateProgressTimerTask == null) {
            mUpdateProgressTimerTask = new TimerTask() {
                @Override
                public void run() {
                    updateProgress();
                }
            };
        }
        mUpdateProgressTimer.schedule(mUpdateProgressTimerTask, 0, 1000);
    }

    /**
     * 取消更新进度的计时器。
     */
    protected void cancelUpdateProgressTimer() {
        if (mUpdateProgressTimer != null) {
            mUpdateProgressTimer.cancel();
            mUpdateProgressTimer = null;
        }
        if (mUpdateProgressTimerTask != null) {
            mUpdateProgressTimerTask.cancel();
            mUpdateProgressTimerTask = null;
        }
    }

    /**
     * 取消更新
     */
    private void cancelUpdateProgress() {
//        if (handler != null) {
//            handler.removeMessages(MSG_ID);
//        }
        cancelUpdateProgressTimer();
    }

    private void updateProgress() {
//        try {
//            if (mVideoPlayerControl == null) return;
//            int duration = mVideoPlayerControl.getDuration();
//            int currentPosition = mVideoPlayerControl.getCurrentProgress();
//            mSeek.setSecondaryProgress(mVideoPlayerControl.getBufferPercent());
//            mSeek.setProgress((int) (currentPosition * 1.0f / duration * 100));
//            mPosition.setText(formatTime(currentPosition));
//            mDuration.setText(formatTime(duration));
//        } catch (Exception e) {
//
//        }

        post(new Runnable() {
            @Override
            public void run() {
                long position = mVideoPlayerControl.getCurrentProgress();
                long duration = mVideoPlayerControl.getDuration();
                int bufferPercentage = mVideoPlayerControl.getBufferPercent();
                int progress;
                int currentInt = (int) Math.ceil(position * 1.0 / 1000) * 1000;
                progress = (int) Math.ceil(100f * currentInt / duration);
                Log.e("TAG", "position" + position + "///duration : " + duration + "////" + progress);
                int mStartPosition = (int) (Math.round(duration / 10.0) / 10);
                duration = (mStartPosition * 100L);
                mDuration.setText(setmDurationformatTime(duration));
                mSeek.setSecondaryProgress(bufferPercentage);
                // 超过最大显示最大
                if (currentInt >= duration) {
                    progress = 100;
                    mPosition.setText(formatTime(currentInt));
                } else {
                    mPosition.setText(formatTime(currentInt));
                }
                mSeek.setProgress(progress);
            }
        });


    }

    /**
     * 暂停播放器
     */
    public void onPause() {
        if (mVideoPlayerControl.isIdle()) return;
        mVideoPlayerControl.pause();
    }

    /**
     * 播放器继续播放
     */
    public void onRestart() {
        if (mVideoPlayerControl.isIdle()) return;   //播放器还只是空闲状态就不继续播放视频了
        mVideoPlayerControl.restart();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (isTracking) {
            int position = (int) (mVideoPlayerControl.getDuration() * seekBar.getProgress() / 100f);
            mVideoPlayerControl.seekTo(position);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isTracking = true;
        cancelUpdateProgress();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isTracking = false;
        int progress = (int) (mVideoPlayerControl.getDuration() * seekBar.getProgress() / 100f);
        mVideoPlayerControl.seekTo(progress);
        startUpdateProgress();
    }

    public void reset() {
        mError.setVisibility(GONE);
        mLoading.setVisibility(GONE);
        mCompleted.setVisibility(GONE);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!mVideoPlayerControl.isTinyScreen()) return super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getRawX();
                startY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:

                float endX = event.getRawX();
                float endY = event.getRawY();

                float dx = endX - startX;
                float dy = endY - startY;

                LayoutParams params = (LayoutParams) mVideoPlayerControl.getContainer().getLayoutParams();

                int left = (int) (params.leftMargin + dx);
                int top = (int) (params.topMargin + dy);
                int viewHeight = mVideoPlayerControl.getContainer().getHeight() + 50;
                int viewWidth = mVideoPlayerControl.getContainer().getWidth();

                if (left < -1) {
                    left = 0;
                } else if (left > screenWidth - viewWidth) {
                    left = screenWidth - viewWidth;
                }

                if (top < -1) {
                    top = 0;
                } else if (top > screenHeight - viewHeight) {
                    top = screenHeight - viewHeight;
                }

                params.leftMargin = left;
                params.topMargin = top;
                mVideoPlayerControl.getContainer().setLayoutParams(params);

                startX = endX;
                startY = endY;
                break;
        }

        return super.onTouchEvent(event);
    }
}

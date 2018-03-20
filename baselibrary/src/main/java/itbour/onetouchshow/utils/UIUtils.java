package itbour.onetouchshow.utils;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import itbour.onetouchshow.base.BaseApplication;


public class UIUtils {

    public static Context getContext() {
        return BaseApplication.getApplication();
    }

    public static Thread getMainThread() {
        return BaseApplication.getMainThread();
    }

    public static long getMainThreadId() {
        return BaseApplication.getMainThreadId();
    }


    /**
     * 设置视图宽高(含weight属性时无效)
     *
     * @param view
     * @param W
     * @param H
     */
    public static void setViewWH(View view, float W, float H) {
        if (view == null)
            return;
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) {
            return;
        }
        if (W > 0) {
            params.width = (int) W;
        }
        if (H > 0) {
            params.height = (int) H;
        }
        view.setLayoutParams(params);
    }

    /**
     * 获取宽高密度信息0
     *
     * @return [0]宽 [1]高 [2]密度
     */
    public static int[] WHD() {
        DisplayMetrics outMetrics = new DisplayMetrics();
        WindowManager mm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        mm.getDefaultDisplay().getMetrics(outMetrics);
        return new int[]
                {outMetrics.widthPixels, outMetrics.heightPixels,
                        (int) outMetrics.density};
    }


    /**
     * 获取视频宽高密度信息0
     * <p>
     * 本地显示处理角度 改变宽高
     *
     * @return [0]宽 [1]高 [2]时长 秒为单位  【3】 角度
     */
    public static String[] videoWHDA(String videoUrl) {
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(videoUrl);
        String width = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);//宽
        String height = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);//高
        String duration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); //
        duration = String.valueOf(Double.valueOf(duration) / 1000);
        String angle = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        L_.e("width width 角度：" + width);
        L_.e("width height 角度：" + height);
        L_.e("width angle角度：" + angle);
        String tempWidth;
        if (angle.equals("90") || angle.equals("270")) {
            tempWidth = width;
            width = height;
            height = tempWidth;
        }
        metadataRetriever.release();
        return new String[]{width, height, duration, angle};

    }



    /**
     * 获取视频宽高密度信息
     */
    public static String videoDuration(int netWorkType,String videoUrl) {
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        if (netWorkType==1){
            metadataRetriever.setDataSource(videoUrl,new HashMap<String, String>());
        }else {
            metadataRetriever.setDataSource(videoUrl);
        }
        String duration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        metadataRetriever.release();
        return duration;
    }




    /**
     * 获取视频宽高密度信息0
     *
     * @return [0]宽 [1]高 [2]时长 秒为单位  【3】 角度
     * <p>
     * 传给后台真实数据
     */
    public static String[] videoWHDA1(String videoUrl) {
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(videoUrl);
        String width = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);//宽
        String height = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);//高
        String duration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); //
        duration = String.valueOf(Double.valueOf(duration) / 1000);
        String angle = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        metadataRetriever.release();
        return new String[]{width, height, duration, angle};
    }


    /**
     * 获取屏幕所有的宽高
     *
     * @return
     */
    public static int[] WH() {
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int screenWith = wm.getDefaultDisplay().getWidth();
        int screenHeight = wm.getDefaultDisplay().getHeight();
        return new int[]{screenWith, screenHeight};


    }

    //获取状态栏的高度
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            int height = resources.getDimensionPixelSize(resourceId);
            L_.i("dbw", "Status height:" + height);
            return height;
        } else {//如果拿不到返回24dp
            return dp2px(24);
        }

    }


    /**
     * 在不加载图片情况下获取图片大小 适合网络图片
     */

    public static int[] getBitmapWH(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
        /**
         *options.outHeight为原始图片的高
         */
        //L_.e("options:"+bitmap.getWidth()+"options.outHeight :"+bitmap.getHeight());
        L_.e("options:" + options.outWidth + "options.outHeight :" + options.outHeight);
        return new int[]{options.outWidth, options.outHeight};
    }

    public void satusBarColor(Activity activity, int color) {
        //SystemBarHelper.tintStatusBar(activity, color);
    }

    /**
     * dip转换px
     */
    public static int dip2px(int dip) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    /**
     * pxz转换dip
     */
    public static int px2dip(int px) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     **/
    public static int sp2px(float spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getContext().getResources().getDisplayMetrics());
    }


    /**
     * 获取主线程的handler
     */
    public static Handler getHandler() {
        Log.e("BaseApplication", BaseApplication.class.getName());
        return BaseApplication.getMainThreadHandler();
    }


    /**
     * 延时在主线程执行runnable
     */
    public static boolean postDelayed(Runnable runnable, long delayMillis) {
        return getHandler().postDelayed(runnable, delayMillis);
    }

    /**
     * 在主线程执行runnable
     */
    public static boolean post(Runnable runnable) {
        return getHandler().post(runnable);
    }

    /**
     * 从主线程looper里面移除runnable
     */
    public static void removeCallbacks(Runnable runnable) {
        getHandler().removeCallbacks(runnable);
    }

    public static View inflate(int resId) {
        return LayoutInflater.from(getContext()).inflate(resId, null);
    }

    /**
     * 获取资源
     */
    public static Resources getResources() {
        return getContext().getResources();
    }

    /**
     * 获取文字
     */
    public static String getString(int resId) {
        return getResources().getString(resId);
    }

    /**
     * 获取文字数组
     */
    public static String[] getStringArray(int resId) {
        return getResources().getStringArray(resId);
    }

    /**
     * 获取dimen
     */
    public static int getDimens(int resId) {
        return getResources().getDimensionPixelSize(resId);
    }

    /**
     * 获取drawable
     */
    public static Drawable getDrawble(@DrawableRes int id) {
        return ContextCompat.getDrawable(getContext(), id);
    }

    /**
     * 获取颜色
     */
    public static int getColor(@ColorRes int id) {
        return ContextCompat.getColor(getContext(), id);
    }

    public static int getDimen(int dimen) {
        return (int) getResources().getDimension(dimen);
    }

    public static <T extends View> T findViewById(View v, int id) {
        return (T) v.findViewById(id);
    }


    /**
     * 获取颜色选择
     */
    public static ColorStateList getColorStateList(int resId) {
        return getResources().getColorStateList(resId);
    }

    //判断当前的线程是不是在主线程
    public static boolean isRunInMainThread() {
        return android.os.Process.myTid() == getMainThreadId();
    }

    public static View runInWindows(Window window) {
        View decorView = window.getDecorView();
        return decorView;
    }

    public static void runInMainThread(Runnable runnable) {
        if (isRunInMainThread()) {
            runnable.run();
        } else {
            post(runnable);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public static int getColor(int resId, Resources.Theme theme) {
        return getResources().getColor(resId, theme);
    }

    public static int getColor(String color) {
        return Color.parseColor(color);
    }

    /**
     * 获取Drawable
     *
     * @param resTd Drawable资源id
     * @return Drawable
     */
    public static Drawable getDrawable(int resTd) {
        return getResources().getDrawable(resTd);
    }


    public static void setVisibility(int visibility, View... views) {
        for (View view : views) if (view != null) view.setVisibility(visibility);
    }

    public static void setDrawable(ImageView image, int id, Drawable drawable) {
        if (image == null) {
            return;
        }
        if (id == 0) {
            if (drawable != null) {
                image.setImageDrawable(drawable);
            }
        } else {
            image.setImageResource(id);
        }
    }

    public static void setText(TextView textView, int id, String text) {
        if (textView == null) {
            return;
        }
        if (id == 0) {
            if (text != null && !text.equals(textView.getText())) {
                textView.setText(text);
            }
        } else {
            CharSequence oldText = textView.getContext().getResources().getText(id);
            if (!oldText.equals(textView.getText())) {
                textView.setText(id);
            }
        }
    }

    public static void setTextColor(TextView textView, int id, int color) {
        if (textView == null) {
            return;
        }
        if (id == 0) {
            textView.setTextColor(color);
        } else {
            textView.setTextColor(getColor(id));
        }
    }

    public static Drawable getDrawable(View view, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return view.getResources().getDrawable(id, null);
        } else {
            //noinspection deprecation
            return view.getResources().getDrawable(id);
        }
    }

    public static Toast mToast;

    public static void showToast(String msg) {
        showToast(msg, Toast.LENGTH_SHORT);
    }

    public static void showToast(String msg, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(getContext(), "", duration);
        }
        mToast.setText(msg);
        mToast.show();
    }

    /**
     * 用于在线程中执行弹土司操作
     */
    public static void showToastSafely(final String msg) {
        UIUtils.getMainThreadHandler().post(new Runnable() {

            @Override
            public void run() {
                if (mToast == null) {
                    mToast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
                }
                mToast.setText(msg);
                mToast.show();
            }
        });
    }


    /**
     * 得到resources对象
     *
     * @return
     */
    public static Resources getResource() {
        return getContext().getResources();
    }


    /**
     * 得到string.xml中的字符串，带点位符
     *
     * @return
     */
    public static String getString(int id, Object... formatArgs) {
        return getResource().getString(id, formatArgs);
    }

    /**
     * 得到string.xml中和字符串数组
     *
     * @param resId
     * @return
     */
    public static String[] getStringArr(int resId) {
        return getResource().getStringArray(resId);
    }


    /**
     * 得到应用程序的包名
     *
     * @return
     */
    public static String getPackageName() {
        return getContext().getPackageName();
    }

    /**
     * 得到主线程Handler
     *
     * @return
     */
    public static Handler getMainThreadHandler() {
        return BaseApplication.getMainThreadHandler();
    }


    /**
     * 安全的执行一个任务
     *
     * @param task
     */
    public static void postTaskSafely(Runnable task) {
        int curThreadId = android.os.Process.myTid();
        // 如果当前线程是主线程
        if (curThreadId == getMainThreadId()) {
            task.run();
        } else {
            // 如果当前线程不是主线程
            getMainThreadHandler().post(task);
        }
    }

    /**
     * 延迟执行任务
     *
     * @param task
     * @param delayMillis
     */
    public static void postTaskDelay(Runnable task, int delayMillis) {
        getMainThreadHandler().postDelayed(task, delayMillis);
    }

    /**
     * 移除任务
     */
    public static void removeTask(Runnable task) {
        getMainThreadHandler().removeCallbacks(task);
    }


    public static int getNavBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }


    /**
     * 设置EditText的字数限制
     *
     * @param mTextEdit
     * @param maxTextNum 最大字符数
     */
    public static void addEditTextNumChanged(final EditText mTextEdit, final int maxTextNum) {

        mTextEdit.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            private boolean isEdit = true;
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                selectionStart = mTextEdit.getSelectionStart();
                selectionEnd = mTextEdit.getSelectionEnd();
                Log.i("gongbiao1", "" + selectionStart);
                if (temp.length() > maxTextNum) {
                    Toast toast = Toast.makeText(getContext(), "只能输入" + maxTextNum + "个字符哦", Toast.LENGTH_LONG);
                    //T_.showToastReal("只能输入"+maxTextNum+"个字符哦");
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    TextView tv = new TextView(getContext());
                    tv.setText("只能输入" + maxTextNum + "个字符哦");
                    tv.setTextColor(Color.RED);
                    toast.setView(tv);
                    toast.show();
                    s.delete(selectionStart - 1, selectionEnd);
                    int tempSelection = selectionStart;
                    mTextEdit.setText(s);
                    mTextEdit.setSelection(tempSelection);
                }
            }
        });
    }

    /**
     * 判断view是否可见
     *
     * @param view
     * @return
     */
    public static boolean viewIsVisable(@NonNull View view) {
        if (view.getVisibility() == View.VISIBLE) {
            return true;
        }
        return false;
    }

    /**
     * 显示键盘
     *
     * @param view
     */
    public static void showKeyboard(final View view) {
        boolean focused = view.isFocused();
        boolean focusable = view.isFocusable();
        view.requestFocus();
        L_.i("focused===" + focused + "focusable===" + focusable);
        final InputMethodManager inputManager =
                (InputMethodManager) view.getContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {

                inputManager.showSoftInput(view, 0);
            }
        }, 100);
    }

    /**
     * 隐藏键盘
     *
     * @param view
     */
    public static void hideKeyboard(final View view) {
        if (view == null) {
            return;
        }
        InputMethodManager imm =
                (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    /**
     * 获取屏幕尺寸
     */
    @SuppressWarnings("deprecation")
    public static Point getScreenSize() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
            return new Point(display.getWidth(), display.getHeight());
        } else {
            Point point = new Point();
            display.getSize(point);
            return point;
        }
    }


    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha 屏幕透明度0.0-1.0 1表示完全不透明
     */
    public static void setBackgroundAlpha(Activity activity, float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        activity.getWindow().setAttributes(lp);
    }

    public static void ObjectAnimator180(View view, Float animate) {
        ObjectAnimator.ofFloat(view, View.ROTATION.getName(), animate, 0).start();
    }


    public static int getHForScan(double w, double h) {
        int width = UIUtils.WHD()[0];
        int height = (int) (width * h / w);
        return height;
    }

    public static InputFilter[] getInputFilter(int maxLenght) {

        return
                new InputFilter[]{new InputFilter.LengthFilter(maxLenght), new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
                        Matcher matcher = isEmoji().matcher(charSequence);
                        if (matcher.find()) {
                            return "";
                        }
                        return null;
                    }
                }};
    }


    public static Pattern isEmoji() {
        Pattern emoji = Pattern.compile(

                "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",

                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        return emoji;
    }




}



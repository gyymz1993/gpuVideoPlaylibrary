package com.ys.uilibrary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.ys.uilibrary.utils.DisplayUtils;

/**
 *
 * @ClassName: ColorFilterImageView
 * @Description: 实现图像根据按下抬起动作变化颜色
 *
 */
@SuppressLint("AppCompatCustomView")
public class ColorFilterImageView extends ImageView implements OnTouchListener {

    private String text;

    public ColorFilterImageView(Context context) {
        this(context, null, 0);
    }

    public ColorFilterImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorFilterImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:  // 按下时图像变灰
                setColorFilter(Color.GRAY, Mode.MULTIPLY);
                break;
            case MotionEvent.ACTION_UP:   // 手指离开或取消操作时恢复原色
            case MotionEvent.ACTION_CANCEL:
                setColorFilter(Color.TRANSPARENT);
                break;
            default:
                break;
        }
        return false;
    }

    private  String textString;
    public  void setText(String txtStr){
        textString = txtStr;

        drawableStateChanged();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        @SuppressLint("DrawAllocation") Paint paint=new Paint();
       // paint.setColor(w);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(8);
        paint.setTextSize(DisplayUtils.dip2px(getContext(),26));
        @SuppressLint("DrawAllocation") Rect bounds = new Rect();
        if (!TextUtils.isEmpty(textString)){
            setColorFilter(Color.GRAY, Mode.MULTIPLY);
            paint.getTextBounds(textString, 0, textString.length(), bounds);
            canvas.drawText(textString, getMeasuredWidth()/2 - bounds.width()/2, getMeasuredHeight()/2 + bounds.height()/2, paint);
        }

    }
}

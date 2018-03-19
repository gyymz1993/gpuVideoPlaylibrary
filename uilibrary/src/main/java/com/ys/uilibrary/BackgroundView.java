package com.ys.uilibrary;

import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


/**
 * Created by Weiping Huang at 11:47 on 2017/5/15
 * For Personal Open Source
 * Contact me at 2584541288@qq.com or nightonke@outlook.com
 * For more projects: https://github.com/Nightonke
 */

@SuppressLint("ViewConstructor")
public class BackgroundView extends FrameLayout {

    private int dimColor;
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public BackgroundView(Context context) {
        super(context);

        //dimColor = view.getDimColor();
        dimColor = Color.parseColor("#55000000");
        ViewGroup rootView = getParentView(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                rootView.getWidth(),
                rootView.getHeight());
        setLayoutParams(params);
        setBackgroundColor(Color.TRANSPARENT);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               // bmb.onBackgroundClicked();
            }
        });
        setMotionEventSplittingEnabled(false);
        rootView.addView(this);
    }

    protected ViewGroup getParentView(Context context) {
        Activity activity = scanForActivity(context);
        if (activity == null) {
            return (ViewGroup) getParent();
        } else {
            return (ViewGroup) activity.getWindow().getDecorView();
        }
    }


    static Activity scanForActivity(Context context) {
        if (context == null) {
            return null;
        } else if (context instanceof Activity)
            return (Activity)context;
        else if (context instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper)context).getBaseContext());
        return null;
    }
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void dim(long duration, AnimatorListenerAdapter completeListener) {
        setVisibility(VISIBLE);
        animate(
                this, "backgroundColor", 0, duration, new ArgbEvaluator(), completeListener,
                Color.TRANSPARENT, dimColor);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void light(long duration, AnimatorListenerAdapter completeListener) {
        animate(
                this, "backgroundColor", 0, duration, new ArgbEvaluator(), completeListener,
                dimColor, Color.TRANSPARENT);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public static ObjectAnimator animate(Object target, String property, long delay, long duration,
                                         TypeEvaluator evaluator, AnimatorListenerAdapter listenerAdapter, int... values) {
        ObjectAnimator animator = ObjectAnimator.ofInt(target, property, values);
        animator.setStartDelay(delay);
        animator.setDuration(duration);
        animator.setEvaluator(evaluator);
        if (listenerAdapter != null) animator.addListener(listenerAdapter);
        animator.start();
        return animator;
    }
}

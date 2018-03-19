package com.ys.uilibrary.vp;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import static android.R.attr.duration;

/**
 * Created by admin on 2017/5/13.
 */

public class ViewPagerScroll extends Scroller {
    private boolean noDuration;
    private static Interpolator mInterpolator=new Interpolator() {
        @Override
        public float getInterpolation(float input) {
            input-=1.0f;
            return input*input*input*input*input+1.0f;
        }
    };


    public ViewPagerScroll(Context context) {
        this(context,mInterpolator);
    }

    public ViewPagerScroll(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public void setNoDuration(boolean noDuration) {
        this.noDuration = noDuration;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        if(noDuration)
            //界面滑动不需要时间间隔
            super.startScroll(startX, startY, dx, dy, 0);
        else
            super.startScroll(startX, startY, dx, dy,duration);
    }
}

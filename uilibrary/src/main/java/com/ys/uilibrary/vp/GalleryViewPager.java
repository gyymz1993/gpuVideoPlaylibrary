package com.ys.uilibrary.vp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class GalleryViewPager extends ViewPager {
    private static final float DISTANCE = 10.0F;
    private float downX;
    private float downY;
    private ViewGroup parent;

    public void setNestedpParent(ViewGroup parent) {
        this.parent = parent;
    }


    /*放在recycleview中滑动不流畅
    *  卡顿问题galleryViewPager.setNestedpParent((ViewGroup) galleryViewPager.getParent());
    * */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (parent != null){
            parent.requestDisallowInterceptTouchEvent(true);
        }
        return super.onInterceptTouchEvent(ev);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (parent != null){
            parent.requestDisallowInterceptTouchEvent(true);
        }
        return super.onTouchEvent(ev);
    }

    public GalleryViewPager(Context context) {
        super(context);
    }

    public GalleryViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (parent != null){
            parent.requestDisallowInterceptTouchEvent(true);
        }
        if (ev.getAction() == 0) {
            this.downX = ev.getX();
            this.downY = ev.getY();
        } else if (ev.getAction() == 1) {
            float upX = ev.getX();
            float upY = ev.getY();
            if (Math.abs(upX - this.downX) > DISTANCE|| Math.abs(upY - this.downY) > DISTANCE) {
                return super.dispatchTouchEvent(ev);
            }
            View view = this.viewOfClickOnScreen(ev);
            if (view != null) {
//                int index = ((Integer)view.getTag()).intValue();
//                if(this.getCurrentItem() != index) {
//                    this.setCurrentItem(index);
//                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private View viewOfClickOnScreen(MotionEvent ev) {
        int childCount = this.getChildCount();
        int currentIndex = this.getCurrentItem();
        int[] location = new int[2];

        for (int position = 0; position < childCount; ++position) {
            View v = this.getChildAt(position);
            v.getLocationOnScreen(location);
            int minX = location[0];
            int minY = location[1];
            int maxX = location[0] + v.getWidth();
            int maxY = location[1] + v.getHeight();
            if (position < currentIndex) {
                maxX = (int) ((double) maxX - ((double) ((float) v.getWidth() * 0.39999998F) * 0.5D + (double) ((float) v.getWidth() * Math.abs(-0.20000005F)) * 0.5D));
                minX = (int) ((double) minX - ((double) ((float) v.getWidth() * 0.39999998F) * 0.5D + (double) ((float) v.getWidth() * Math.abs(-0.20000005F)) * 0.5D));
            } else if (position == currentIndex) {
                minX = (int) ((float) minX + (float) v.getWidth() * Math.abs(-0.20000005F));
            } else if (position > currentIndex) {
                maxX = (int) ((double) maxX - (double) ((float) v.getWidth() * Math.abs(-0.20000005F)) * 0.5D);
                minX = (int) ((double) minX - (double) ((float) v.getWidth() * Math.abs(-0.20000005F)) * 0.5D);
            }

            float x = ev.getRawX();
            float y = ev.getRawY();
            if (x > (float) minX && x < (float) maxX && y > (float) minY && y < (float) maxY) {
                if (this.getCurrentItem() != position) {
                    this.setCurrentItem(position);
                }
                return v;
            }
        }

        return null;
    }
}

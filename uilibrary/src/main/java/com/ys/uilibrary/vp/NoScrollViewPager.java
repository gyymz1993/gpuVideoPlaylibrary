package com.ys.uilibrary.vp;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;


/*切换不滑动的ViewPager*/
public class NoScrollViewPager extends ViewPager {
    
    private boolean noScroll = true;
    private ViewpagerHelper  helper;

    public NoScrollViewPager(Context context) {
        super(context);
    }


    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        helper=new ViewpagerHelper(this);
    }


    public void setNoScroll(boolean noScroll) {
        this.noScroll = noScroll;
    }
 
    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }
 
    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        if (noScroll)
            return false;
        else
            return super.onTouchEvent(arg0);
    }
 
    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (noScroll)
            return false;
        else
            return super.onInterceptTouchEvent(arg0);
    }
 
    @Override
    public void setCurrentItem(int item) {
        setCurrentItem(item,true);
    }
 
    @Override
    public void setCurrentItem(int item,boolean smoothScroll) {
        ViewPagerScroll scroller=helper.getViewPagerScroll();
        if(Math.abs(getCurrentItem()-item)>1){
            scroller.setNoDuration(true);
            super.setCurrentItem(item, smoothScroll);
            scroller.setNoDuration(false);
        }else{
            scroller.setNoDuration(false);
            super.setCurrentItem(item, smoothScroll);
        }
    }
 
}
package com.ys.uilibrary.vp;

import android.support.v4.view.ViewPager;

import java.lang.reflect.Field;

/**
 * Created by admin on 2017/5/13.
 */

public class ViewpagerHelper {
    ViewPager viewPager;
    ViewPagerScroll viewPagerScroll;

    public ViewpagerHelper(ViewPager viewPager) {
        this.viewPager = viewPager;
        init();
    }

    public ViewPagerScroll  getViewPagerScroll(){
        return viewPagerScroll;
    }
    
    public void setCurrentItem(int item){
        setCurrentItem(item,true);
    }

    private void setCurrentItem(int item, boolean b) {
        int current=viewPager.getCurrentItem();
        if (Math.abs(current-item)>1){
            viewPagerScroll.setNoDuration(true);
            viewPager.setCurrentItem(item,b);
            viewPagerScroll.setNoDuration(false);
        }else {
            viewPagerScroll.setNoDuration(false);
            viewPager.setCurrentItem(item,b);
        }
    }

    private void init() {
        viewPagerScroll=new ViewPagerScroll(viewPager.getContext());
        Class<ViewPager> cl=ViewPager.class;
        try {
            Field field=cl.getDeclaredField("mScroller");
            field.setAccessible(true);
            field.set(viewPager,viewPagerScroll);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

package com.ys.uilibrary;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.RelativeLayout;

/**
 * 创建人：$ gyymz1993
 * 创建时间：2017/6/24 11:20
 */

public class CheckableRelativeLyout extends RelativeLayout implements Checkable{

    private boolean mChecked=false;
    public CheckableRelativeLyout(Context context) {
        this(context,null);
    }

    public CheckableRelativeLyout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CheckableRelativeLyout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked!=checked){
            mChecked=checked;
            refreshDrawableState();
            for (int i=0,len=getChildCount();i<len;i++){
                View view=getChildAt(i);
                if (view instanceof Checkable){
                    ((Checkable) view).setChecked(checked);
                }
            }
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }
}

/**
 * Copyright (C) 2015 nshmura
 * Copyright (C) 2015 The Android Open Source Project
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ys.uilibrary.rv;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * 点击自动切换   像导航栏Tab
 */
public class AutoRecycleLayout extends RecyclerView {

    protected static final long DEFAULT_SCROLL_DURATION = 200;
    protected static final float DEFAULT_POSITION_THRESHOLD = 0.6f;
    protected static final float POSITION_THRESHOLD_ALLOWABLE = 0.001f;
    protected int mTabMinWidth;
    protected int mTabMaxWidth;
    protected int mIndicatorHeight;
    protected LinearLayoutManager mLinearLayoutManager;
    protected RecyclerOnScrollListener mRecyclerOnScrollListener;
    protected int mIndicatorPosition;
    protected int mIndicatorOffset;
    protected int mScrollOffset;
    protected float mOldPositionOffset;
    protected float mPositionThreshold;
    protected boolean mRequestScrollToTab;
    protected boolean mScrollEanbled = true;

    public AutoRecycleLayout(Context context) {
        this(context, null);
    }

    public AutoRecycleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoRecycleLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setWillNotDraw(false);

        mLinearLayoutManager = new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollHorizontally() {
                return mScrollEanbled;
            }
        };
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        setLayoutManager(mLinearLayoutManager);
        setItemAnimator(null);
        mPositionThreshold = DEFAULT_POSITION_THRESHOLD;
      //  setAutoSelectionMode(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mRecyclerOnScrollListener != null) {
            removeOnScrollListener(mRecyclerOnScrollListener);
            mRecyclerOnScrollListener = null;
        }
        super.onDetachedFromWindow();
    }


    public void setAutoSelectionMode(boolean autoSelect) {
        if (mRecyclerOnScrollListener != null) {
            removeOnScrollListener(mRecyclerOnScrollListener);
            mRecyclerOnScrollListener = null;
        }
        if (autoSelect) {
            mRecyclerOnScrollListener = new RecyclerOnScrollListener(this, mLinearLayoutManager);
            addOnScrollListener(mRecyclerOnScrollListener);
        }
    }

    public void setCurrentItem(int position, boolean smoothScroll) {

        if (smoothScroll && position != mIndicatorPosition) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                startAnimation(position);
            } else {
                scrollToTab(position); //FIXME add animation
            }

        } else {
            scrollToTab(position);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void startAnimation(final int position) {

        float distance = 1;

        View view = mLinearLayoutManager.findViewByPosition(position);
        if (view != null) {
            float currentX = view.getX() + view.getMeasuredWidth() / 2.f;
            float centerX = getMeasuredWidth() / 2.f;
            distance = Math.abs(centerX - currentX) / view.getMeasuredWidth();
        }

        ValueAnimator animator;
        if (position < mIndicatorPosition) {
            animator = ValueAnimator.ofFloat(distance, 0);
        } else {
            animator = ValueAnimator.ofFloat(-distance, 0);
        }
        animator.setDuration(DEFAULT_SCROLL_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scrollToTab(position, (float) animation.getAnimatedValue(), true);
            }
        });
        animator.start();
    }

    public void scrollToTab(int position) {
        scrollToTab(position, 0, false);
    }

    protected void scrollToTab(int position, float positionOffset, boolean fitIndicator) {
        int scrollOffset = 0;

        View selectedView = mLinearLayoutManager.findViewByPosition(position);
        View nextView = mLinearLayoutManager.findViewByPosition(position + 1);

        if (selectedView != null) {
            int width = getMeasuredWidth();
            float scroll1 = width / 2.f - selectedView.getMeasuredWidth() / 2.f;

            if (nextView != null) {
                float scroll2 = width / 2.f - nextView.getMeasuredWidth() / 2.f;

                float scroll = scroll1 + (selectedView.getMeasuredWidth() - scroll2);
                float dx = scroll * positionOffset;
                scrollOffset = (int) (scroll1 - dx);

                mScrollOffset = (int) dx;
                mIndicatorOffset = (int) ((scroll1 - scroll2) * positionOffset);

            } else {
                scrollOffset = (int) scroll1;
                mScrollOffset = 0;
                mIndicatorOffset = 0;
            }
            if (fitIndicator) {
                mScrollOffset = 0;
                mIndicatorOffset = 0;
            }

            mIndicatorPosition = position;

        } else {
            if (getMeasuredWidth() > 0 && mTabMaxWidth > 0 && mTabMinWidth == mTabMaxWidth) { //fixed size
                int width = mTabMinWidth;
                int offset = (int) (positionOffset * -width);
                int leftOffset = (int) ((getMeasuredWidth() - width) / 2.f);
                scrollOffset = offset + leftOffset;
            }
            mRequestScrollToTab = true;
        }

        stopScroll();
        mLinearLayoutManager.scrollToPositionWithOffset(position, scrollOffset);

        if (mIndicatorHeight > 0) {
            invalidate();
        }

        mOldPositionOffset = positionOffset;
    }



    protected static class RecyclerOnScrollListener extends OnScrollListener {

        AutoRecycleLayout mRecyclerTabLayout;
        LinearLayoutManager mLinearLayoutManager;

        RecyclerOnScrollListener(AutoRecycleLayout autoRecycleLayout,
                                 LinearLayoutManager linearLayoutManager) {
            mRecyclerTabLayout = autoRecycleLayout;
            mLinearLayoutManager = linearLayoutManager;
        }

        public int mDx;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            mDx += dx;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            switch (newState) {
                case SCROLL_STATE_IDLE:
                    if (mDx > 0) {
                        selectCenterTabForRightScroll();
                    } else {
                        selectCenterTabForLeftScroll();
                    }
                    mDx = 0;
                    break;
                case SCROLL_STATE_DRAGGING:
                case SCROLL_STATE_SETTLING:
            }
        }

        void selectCenterTabForRightScroll() {
            int first = mLinearLayoutManager.findFirstVisibleItemPosition();
            int last = mLinearLayoutManager.findLastVisibleItemPosition();
            int center = mRecyclerTabLayout.getWidth() / 2;
            for (int position = first; position <= last; position++) {
                View view = mLinearLayoutManager.findViewByPosition(position);
                if (view.getLeft() + view.getWidth() >= center) {
                    mRecyclerTabLayout.setCurrentItem(position, false);
                    break;
                }
            }
        }

        void selectCenterTabForLeftScroll() {
            int first = mLinearLayoutManager.findFirstVisibleItemPosition();
            int last = mLinearLayoutManager.findLastVisibleItemPosition();
            int center = mRecyclerTabLayout.getWidth() / 2;
            for (int position = last; position >= first; position--) {
                View view = mLinearLayoutManager.findViewByPosition(position);
                if (view.getLeft() <= center) {
                    mRecyclerTabLayout.setCurrentItem(position, false);
                    break;
                }
            }
        }
    }


}

package com.ys.uilibrary.manager;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * 创建人：$ gyymz1993
 * 创建时间：2017/7/7 15:02
 */

public class CardLyoutManager extends RecyclerView.LayoutManager {

    private RecyclerView mRecyclerView;
    private OnItemTouchHelper onItemTouchHelper;

    public class OnItemTouchHelper {

    }

    public CardLyoutManager(RecyclerView mRecyclerView, OnItemTouchHelper onItemTouchHelper) {
        this.mRecyclerView = mRecyclerView;
        this.onItemTouchHelper = onItemTouchHelper;
    }

    /**
     * Create a default <code>LayoutParams</code> object for a child of the RecyclerView.
     * <p>
     * <p>LayoutManagers will often want to use a custom <code>LayoutParams</code> type
     * to store extra information specific to the layout. Client code should subclass
     * {@link RecyclerView.LayoutParams} for this purpose.</p>
     * <p>
     * <p><em>Important:</em> if you use your own custom <code>LayoutParams</code> type
     * you must also override
     * {@link #checkLayoutParams(RecyclerView.LayoutParams)},
     * {@link #generateLayoutParams(android.view.ViewGroup.LayoutParams)} and
     * {@link #generateLayoutParams(android.content.Context, android.util.AttributeSet)}.</p>
     * 一般情况下，这样写即可
     *
     * @return A new LayoutParams for a child view
     */
    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /*
     * @param recycler         Recycler to use for fetching potentially cached views for a
     *                         position
     * @param state            Transient state of RecyclerView
     */
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);

        removeAllViews();
        detachAndScrapAttachedViews(recycler);
        int itemCount = getItemCount();
        if (itemCount > 3) {
            for (int position = 3; position >= 0; position--) {
                View view = recycler.getViewForPosition(position);
                // 将 Item View 加入到 RecyclerView 中
                addView(view);
                // 测量 Item View
                measureChildWithMargins(view, 0, 0);
                // getDecoratedMeasuredWidth(view) 可以得到 Item View 的宽度
                // 所以 widthSpace 就是除了 Item View 剩余的值
                int widthSpace = getWidth() - getDecoratedMeasuredWidth(view);
                int heightSpcae = getHeight() - getBottomDecorationHeight(view);
                // 将 Item View 放入 RecyclerView 中布局
                // 在这里默认布局是放在 RecyclerView 中心
                layoutDecoratedWithMargins(view, widthSpace / 2,
                        heightSpcae / 2
                        , widthSpace / 2 + getDecoratedMeasuredWidth(view),
                        heightSpcae / 2 + getMinimumHeight());
                // 其实屏幕上有四张卡片，但是我们把第三张和第四张卡片重叠在一起，这样看上去就只有三张
                // 第四张卡片主要是为了保持动画的连贯性
                if (position == 3) {
                    view.setScaleX(1 - (position - 1) * 1.2f);
                    view.setScaleY(1 - (position - 1) * 1.2f);
                    view.setTranslationX(position * view.getMeasuredHeight() / 5);
                } else {
                    //
                    view.setOnTouchListener(mOnTouchListener);
                }
            }
        } else {
            /* 当数据源个数小于或等于最大显示数时 */
            for (int position = itemCount - 1; position >= 0; position--) {
                final View view = recycler.getViewForPosition(position);
                addView(view);
                measureChildWithMargins(view, 0, 0);
                int widthSpace = getWidth() - getDecoratedMeasuredWidth(view);
                int heightSpace = getHeight() - getDecoratedMeasuredWidth(view);
                //recyclerview   布局
                layoutDecoratedWithMargins(view, widthSpace / 2, heightSpace / 2,
                        widthSpace / 2 + getDecoratedMeasuredWidth(view),
                        heightSpace / 2 + getDecoratedMeasuredHeight(view));
                if (position > 0) {
                    view.setScaleX(1 - position * 1.0f);
                    view.setScaleY(1 - position * 1.0f);
                    view.setTranslationY(position * view.getMeasuredHeight() / 1.0f);
                } else {
                    view.setOnTouchListener(mOnTouchListener);
                }
            }
        }
    }

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            RecyclerView.ViewHolder childViewHolder = mRecyclerView.getChildViewHolder(v);
            if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                //onItemTouchHelper.startSwipe(childViewHolder);
            }
            return false;
        }
    };

    public interface OnSwipeListener<T> {

        /**
         *
         * @param viewHolder 卡片还在滑动时回调
         * @param ratio  滑动的速度的比例
         * @param direction 卡片滑动的方向
         */
        void  onSwiping(RecyclerView.ViewHolder viewHolder,float ratio,int direction);


        /**
         *  卡片完全画出回调
         * @param viewHolder  该画出卡的ViewHolder
         * @param t    卡片的数据
         * @param direction  卡变得额方向   lefr  or  right
         */
        void onSwiped(RecyclerView.ViewHolder viewHolder,T t,int direction);


        /**
         * 所有卡片全部划出的回调
         */
        void onSwipedClean();
    }
}

package com.ys.uilibrary.rv;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.bumptech.glide.Glide;

/**
 * 创建人：$ gyymz1993
 * 创建时间：2017/6/17 16:05
 * 快速滑动不加载图片
 */

public class LazyLoadRecycleView extends RecyclerView {
    private boolean isLoadingMore = false;

    public LazyLoadRecycleView(Context context) {
        this(context, null);
    }

    public LazyLoadRecycleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LazyLoadRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnScrollListener(new LazyScrollChangeListener(true,true));
    }

    public class LazyScrollChangeListener extends OnScrollListener {
        private boolean pauseOnScroll;
        private boolean pauseOnFling;

        public LazyScrollChangeListener(boolean pauseOnScroll, boolean pauseOnFling) {
            this.pauseOnScroll = pauseOnScroll;
            this.pauseOnFling = pauseOnFling;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (getLayoutManager() instanceof LinearLayoutManager) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                int totalCount = getAdapter().getItemCount();
                if (onLoadMoreListener != null && dy > 0 && lastVisibleItemPosition >= totalCount - 2) {
                    onLoadMoreListener.onLoadMore();
                    isLoadingMore = true;
                }
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            switch (newState) {
                case SCROLL_STATE_IDLE: // The RecyclerView is not currently scrolling.
                    //当屏幕停止滚动，加载图片
                    Glide.with(getContext()).resumeRequests();
                    break;
                case SCROLL_STATE_DRAGGING: // The RecyclerView is currently being dragged by outside input such as user touch input.
                    //当屏幕滚动且用户使用的触碰或手指还在屏幕上，停止加载图片
                    if (pauseOnScroll) {
                        Glide.with(getContext()).pauseRequests();
                    } else {
                        Glide.with(getContext()).resumeRequests();
                    }
                    break;
                case SCROLL_STATE_SETTLING: // The RecyclerView is currently animating to a final position while not under outside control.
                    //由于用户的操作，屏幕产生惯性滑动，停止加载图片
                    if (pauseOnFling) {
                        Glide.with(getContext()).pauseRequests();
                    } else {
                        Glide.with(getContext()).resumeRequests();
                    }
                    break;
            }
        }
    }

    public LoadMoreListener getOnLoadMoreListener() {
        return onLoadMoreListener;
    }

    public void setOnLoadMoreListener(LoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    LoadMoreListener onLoadMoreListener;

    //加载更多的回调接口
    public interface LoadMoreListener {
        void onLoadMore();
    }


}

package com.ys.uilibrary.rv;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/*
* 增加头部和底部的RecycleView
* */
public class WrapRecyclerView extends RecyclerView {
    private ArrayList<View> mHeaderViewInfos = new ArrayList<>();
    private ArrayList<View> mFooterViewInfos = new ArrayList<>();
    private Adapter mAdapter;

    public WrapRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void addHeaderView(View v) {
        mHeaderViewInfos.add(v);
        // Wrap the adapter if it wasn't already wrapped.
        if (mAdapter != null) {
            if (!(mAdapter instanceof HeaderViewRecyclerAdapter)) {
                mAdapter = new HeaderViewRecyclerAdapter(mHeaderViewInfos, mFooterViewInfos, mAdapter);
            }
        }
    }

    public void addFooterView(View v) {
        mFooterViewInfos.add(v);

        // Wrap the adapter if it wasn't already wrapped.
        if (mAdapter != null) {
            if (!(mAdapter instanceof HeaderViewRecyclerAdapter)) {
                mAdapter = new HeaderViewRecyclerAdapter(mHeaderViewInfos, mFooterViewInfos, mAdapter);
            }
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (mHeaderViewInfos.size() > 0 || mFooterViewInfos.size() > 0) {
            mAdapter = new HeaderViewRecyclerAdapter(mHeaderViewInfos, mFooterViewInfos, adapter);
        } else {
            mAdapter = adapter;
        }
        super.setAdapter(mAdapter);
    }

    class HeaderViewRecyclerAdapter extends Adapter {
        private Adapter mAdapter;

        ArrayList<View> mHeaderViewInfos;
        ArrayList<View> mFooterViewInfos;

        public HeaderViewRecyclerAdapter(ArrayList<View> headerViewInfos,
                                         ArrayList<View> footerViewInfos, Adapter adapter) {
            mAdapter = adapter;

            if (headerViewInfos == null) {
                mHeaderViewInfos = new ArrayList<View>();
            } else {
                mHeaderViewInfos = headerViewInfos;
            }

            if (footerViewInfos == null) {
                mFooterViewInfos = new ArrayList<View>();
            } else {
                mFooterViewInfos = footerViewInfos;
            }
        }

        @Override
        public int getItemCount() {
            if (mAdapter != null) {
                return getFootersCount() + getHeadersCount() + mAdapter.getItemCount();
            } else {
                return getFootersCount() + getHeadersCount();
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int numHeaders = getHeadersCount();
            if (position < numHeaders) {
                return;
            }
            //adapter body
            final int adjPosition = position - numHeaders;
            int adapterCount = 0;
            if (mAdapter != null) {
                adapterCount = mAdapter.getItemCount();
                if (adjPosition < adapterCount) {
                    mAdapter.onBindViewHolder(holder, adjPosition);
                    return;
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            int numHeaders = getHeadersCount();
            if (position < numHeaders) {
                return RecyclerView.INVALID_TYPE;
            }
            // Adapter
            final int adjPosition = position - numHeaders;
            int adapterCount = 0;
            if (mAdapter != null) {
                adapterCount = mAdapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.getItemViewType(adjPosition);
                }
            }
            return RecyclerView.INVALID_TYPE - 1;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //header
            if (viewType == RecyclerView.INVALID_TYPE) {
                return new HeaderViewHolder(mHeaderViewInfos.get(0));
            } else if (viewType == RecyclerView.INVALID_TYPE - 1) {//footer
                return new HeaderViewHolder(mFooterViewInfos.get(0));
            }
            // Footer (off-limits positions will throw an IndexOutOfBoundsException)
            return mAdapter.onCreateViewHolder(parent, viewType);
        }

        public int getHeadersCount() {
            return mHeaderViewInfos.size();
        }

        public int getFootersCount() {
            return mFooterViewInfos.size();
        }

        private class HeaderViewHolder extends ViewHolder {

            public HeaderViewHolder(View view) {
                super(view);
            }
        }
    }

}

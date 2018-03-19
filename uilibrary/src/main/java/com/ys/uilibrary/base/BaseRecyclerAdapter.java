package com.ys.uilibrary.base;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerHolder> {


    private Context context;
    private int layouId;
    private List<T> mDatas;

    public BaseRecyclerAdapter(Context context,List<T> datas, int itemLayoutId) {
        this.context = context;
        this.mDatas = datas;
        this.layouId = itemLayoutId;
        setListData(mDatas);
    }


    /**
     * Recycler适配器填充方法
     *
     * @param holder viewholder
     * @param item   javabean
     *               isScrolling RecyclerView是否正在滚动
     */
    protected abstract void convert(BaseRecyclerHolder holder, T item, int position);

    @Override
    public BaseRecyclerHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(context).inflate(layouId, viewGroup, false);
        return new BaseRecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseRecyclerHolder viewHolder, int position) {
        convert(viewHolder, mDatas.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }



    /**
     * 设置数据。<BR>
     * 会清空原集合所有数据,后添加。
     *
     * @param list
     */
    public void setListData(List<T> list) {
        if (list == null) {
            list = new ArrayList<T>(0);
        }
        this.mDatas = list;
        notifyDataSetChanged();
    }


}

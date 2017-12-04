package com.duke.dkskin.common;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: duke
 * DateTime: 2017-08-02 13:57
 * Description: recyclerView adapter的适配器包装类
 */
public abstract class BaseRecyclerViewAdapter<T extends RecyclerView.ViewHolder, D> extends RecyclerView.Adapter<T> {
    public final String TAG = getClass().getSimpleName();
    private Context mContext;
    private List<D> mDataList = new ArrayList<>();
    private OnItemClickListener<D> onItemClickListener;
    private OnItemLongClickListener<D> onItemLongClickListener;


    public void setOnItemClickListener(OnItemClickListener<D> l) {
        this.onItemClickListener = l;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<D> l) {
        this.onItemLongClickListener = l;
    }

    public List<D> getDataList() {
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        }
        return mDataList;
    }

    public Context getContext() {
        return mContext;
    }

    public BaseRecyclerViewAdapter(Context context) {
        this(context, null);
    }

    public BaseRecyclerViewAdapter(Context context, List<D> list) {
        this.mContext = context;
        if (this.mContext == null) {
            throw new IllegalArgumentException("context is null");
        }
        setDatas(list);
    }

    public void clear() {
        getDataList().clear();
        notifyDataSetChanged();
    }

    public void setDatas(List<D> data) {
        getDataList().clear();
        if (data != null) {
            getDataList().addAll(data);
        }
        notifyDataSetChanged();
    }

    public void addDatas(List<D> data) {
        if (data != null) {
            getDataList().addAll(data);
        }
        notifyDataSetChanged();
    }

    public void addDatas(int position, List<D> data) {
        if (data != null) {
            getDataList().addAll(position, data);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return getDataList().size();
    }

    public D getItem(int position) {
        if (position < 0 || position >= getDataList().size()) {
            return null;
        }
        return getDataList().get(position);
    }

    public View newView(int layoutId, ViewGroup viewGroup) {
        if (viewGroup == null) {
            return null;
        }
        return LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
    }

    public void invokeOnClickEvent(final View view, final int position) {
        if (view == null || onItemClickListener == null) {
            return;
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onClick(view, getItem(position), position);
            }
        });
    }

    public void invokeOnLongClickEvent(final View view, final int position) {
        if (view == null || onItemLongClickListener == null) {
            return;
        }
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemLongClickListener.onLongClick(view, getItem(position), position);
                return true;
            }
        });
    }

    public interface OnItemClickListener<M> {
        void onClick(View view, M data, int position);
    }

    public interface OnItemLongClickListener<M> {
        void onLongClick(View view, M data, int position);
    }
}
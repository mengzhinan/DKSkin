package com.duke.dkskin.common;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.duke.dkskin.R;

import java.util.List;

/**
 * Author: duke
 * Date: 2017-12-03 15:36
 * Description:
 */
public class MyAdapter extends BaseRecyclerViewAdapter<MyAdapter.MyHolderView, String> {
    private List<String> list;
    private OnItemClickCallback clickCallback;

    public MyAdapter(Context context, List<String> list) {
        super(context, list);
        this.list = list;
    }

    public OnItemClickCallback getClickCallback() {
        return clickCallback;
    }

    public void setClickCallback(OnItemClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    @Override
    public MyHolderView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main_item, parent, false);
        return new MyHolderView(view);
    }

    @Override
    public void onBindViewHolder(final MyHolderView holder, final int position) {
        if (list == null || list.size() <= 0) {
            return;
        }
        holder.textView.setText(list.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickCallback != null) {
                    clickCallback.onItemClick(holder.itemView, position, list.get(position));
                }
            }
        });
    }

    static class MyHolderView extends RecyclerView.ViewHolder {
        private TextView textView;

        public MyHolderView(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_tv);
        }
    }

    public interface OnItemClickCallback {
        void onItemClick(View view, int position, Object value);
    }
}

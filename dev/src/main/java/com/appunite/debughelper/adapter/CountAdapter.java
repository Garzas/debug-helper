package com.appunite.debughelper.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.appunite.debughelper.R;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Nonnull;

public class CountAdapter extends RecyclerView.Adapter<CountAdapter.ViewHolder> {

    @Nonnull
    private final ArrayList<Map.Entry<String,Integer>> mData;

    public CountAdapter(@Nonnull final Map<String, Integer> map) {
        mData = new ArrayList();
        mData.addAll(map.entrySet());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.count_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@Nonnull final ViewHolder holder, final int position) {
        final Map.Entry<String, Integer> item = mData.get(position);

        holder.request.setText(item.getKey());
        holder.count.setText(String.format("%s", item.getValue()));
        holder.request.setSelected(true);

    }

    public void updateData(@Nonnull final Map<String, Integer> map) {
        mData.clear();
        mData.addAll(map.entrySet());
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView request;
        public TextView count;

        public ViewHolder(@Nonnull final View itemView) {
            super(itemView);
            request = (TextView) itemView.findViewById(R.id.count_name);
            count = (TextView) itemView.findViewById(R.id.count_value);
        }
    }

}
package com.skypayjm.app.locate.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Sky on 16/7/2015.
 */
abstract public class BaseRecyclerAdapter<T extends Object, VH extends BaseViewHolder> extends RecyclerView.Adapter<BaseViewHolder> {

    @Override
    public abstract BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(BaseViewHolder holder, int position);

    @Override
    public abstract int getItemCount();

    public abstract <T extends Object> void updateItems(List<T> items);

    public abstract <T extends Object> T getItemAtPosition(int position);
}

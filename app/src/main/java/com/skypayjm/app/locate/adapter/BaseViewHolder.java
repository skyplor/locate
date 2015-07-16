package com.skypayjm.app.locate.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.skypayjm.app.locate.R;

/**
 * Created by Sky on 16/7/2015.
 */
abstract public class BaseViewHolder extends RecyclerView.ViewHolder {


    private final View baseItemView;
    TextView tvName;
    ImageView ivAddSearch;


    public BaseViewHolder(View itemView) {
        super(itemView);
        baseItemView = itemView;
        tvName = (TextView) itemView.findViewById(R.id.tvName);
        ivAddSearch = (ImageView) itemView.findViewById(R.id.ivAddSearch);
    }

}
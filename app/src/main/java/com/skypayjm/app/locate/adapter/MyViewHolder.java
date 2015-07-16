package com.skypayjm.app.locate.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.skypayjm.app.locate.R;

/**
 * Created by Sky on 16/7/2015.
 */
public class MyViewHolder extends BaseViewHolder{

    View categoryItemView;
    TextView tvName;
    ImageView ivAddSearch;
    private RecyclerViewListener recyclerViewListener;

    public MyViewHolder(View itemView, RecyclerViewListener recyclerViewListener) {
        super(itemView);
        categoryItemView = itemView;
        tvName = (TextView) itemView.findViewById(R.id.tvName);
        ivAddSearch = (ImageView) itemView.findViewById(R.id.ivAddSearch);
        this.recyclerViewListener = recyclerViewListener;
    }
}

package com.skypayjm.app.locate.adapter;

import android.view.View;

/**
 * Created by Sky on 16/7/2015.
 */

public interface RecyclerViewListener<T extends Object> {
    void onItemClickListener(View view, T item);
}
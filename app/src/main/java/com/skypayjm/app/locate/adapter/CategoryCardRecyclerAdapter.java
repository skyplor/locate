package com.skypayjm.app.locate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skypayjm.app.locate.R;
import com.skypayjm.app.locate.model.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sky on 14/7/2015.
 */
public class CategoryCardRecyclerAdapter extends BaseRecyclerAdapter<Category, MyViewHolder> {

    private RecyclerViewListener<Category> recyclerViewListener;
    private List<Category> categories;

    public CategoryCardRecyclerAdapter(List<Category> categories, RecyclerViewListener<Category> recyclerViewListener) {
        this.recyclerViewListener = recyclerViewListener;
        this.categories = new ArrayList<>(categories);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_venue, parent, false);
        return new MyViewHolder(itemView, recyclerViewListener);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        final Category category = categories.get(position);
        holder.tvName.setText(category.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewListener.onItemClickListener(v, category);
            }
        });
        holder.ivAddSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewListener.onItemClickListener(v, category);
            }
        });

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    @Override
    public void updateItems(List<Category> categories) {
        this.categories.clear();

        this.categories.addAll(categories);
    }

    public Category getItemAtPosition(int position) {
        return categories.get(position);
    }
}

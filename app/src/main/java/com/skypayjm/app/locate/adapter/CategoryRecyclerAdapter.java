package com.skypayjm.app.locate.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.skypayjm.app.locate.R;
import com.skypayjm.app.locate.model.Category;

import java.util.List;

/**
 * Created by Sky on 14/7/2015.
 */
public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.ViewHolder> {

    public interface RecyclerViewListener {
        void onItemClickListener(View view, int position);
    }

    private List<Category> categories;
    private RecyclerViewListener recyclerViewListener;

    public CategoryRecyclerAdapter(List<Category> categories, RecyclerViewListener recyclerViewListener) {
        this.categories = categories;
        this.recyclerViewListener = recyclerViewListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final Category rowData = categories.get(position);
        viewHolder.fillData(rowData);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public Category getItemAtPosition(int position) {
        return categories.get(position);
    }


    public void addItemsToList(List<Category> categories) {
        this.categories.clear();
        this.categories.addAll(categories);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final View venueItemView;
        TextView itemTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            venueItemView = itemView;
            itemTitle = (TextView) itemView.findViewById(R.id.itemTitle);
            venueItemView.setOnClickListener(this);
        }

        public void fillData(Category category) {
            itemTitle.setText(category.getName());
        }

        @Override
        public void onClick(View v) {
            recyclerViewListener.onItemClickListener(v, getLayoutPosition());
        }
    }
}

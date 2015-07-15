package com.skypayjm.app.locate.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.skypayjm.app.locate.R;
import com.skypayjm.app.locate.model.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sky on 14/7/2015.
 */
public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.CategoryViewHolder> {
    //extends RealmRecyclerViewAdapter<Category> {

    public interface RecyclerViewListener {
        void onItemClickListener(View view, Category item);
    }

    private RecyclerViewListener recyclerViewListener;
    private List<Category> categories;

    public CategoryRecyclerAdapter(List<Category> categories, RecyclerViewListener recyclerViewListener) {
        this.recyclerViewListener = recyclerViewListener;
        this.categories = new ArrayList<>(categories);
    }

//    public CategoryRecyclerAdapter(RecyclerViewListener recyclerViewListener) {
//        this.recyclerViewListener = recyclerViewListener;
//    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(itemView);
    }
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
//        return new CategoryViewHolder(itemView);
//    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.fillData(category);
    }
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;
//        Category category = getItem(position);
//        categoryViewHolder.fillData(category);
//    }

    /* The inner RealmBaseAdapter
     * view count is applied here.
     *
     * getRealmAdapter is defined in RealmRecyclerViewAdapter.
     */
//    @Override
//    public int getItemCount() {
//        if (getRealmAdapter() != null) {
//            return getRealmAdapter().getCount();
//        }
//        return 0;
//    }
    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void updateItems(List<Category> categories) {
        this.categories.clear();

        this.categories.addAll(categories);
    }

    //    public Category getItemAtPosition(int position) {
//        return getItem(position);
//    }
    public Category getItemAtPosition(int position) {
        return categories.get(position);
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final View categoryItemView;
        TextView tvName;
        ImageView ivAddCategory;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            categoryItemView = itemView;
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            ivAddCategory = (ImageView) itemView.findViewById(R.id.ivAddCategory);
            categoryItemView.setOnClickListener(this);
            ivAddCategory.setOnClickListener(this);
        }

        public void fillData(Category category) {
            tvName.setText(category.getName());
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            recyclerViewListener.onItemClickListener(v, categories.get(position));
        }
    }
}

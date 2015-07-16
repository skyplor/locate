package com.skypayjm.app.locate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skypayjm.app.locate.R;
import com.skypayjm.app.locate.model.Venue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sky on 14/7/2015.
 */
public class VenueRecyclerAdapter extends BaseRecyclerAdapter<Venue, MyViewHolder> {

    private RecyclerViewListener recyclerViewListener;
    private List<Venue> venues;

    public VenueRecyclerAdapter(List<Venue> venues, RecyclerViewListener recyclerViewListener) {
        this.recyclerViewListener = recyclerViewListener;
        this.venues = new ArrayList<>(venues);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_venue, parent, false);
        return new MyViewHolder(itemView, recyclerViewListener);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        final Venue venue = venues.get(position);

        holder.tvName.setText(venue.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewListener.onItemClickListener(v, venue);
            }
        });
        holder.ivAddSearch.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return venues.size();
    }

    @Override
    public void updateItems(List venues) {
        this.venues.clear();

        this.venues.addAll(venues);

    }

    public Venue getItemAtPosition(int position) {
        return venues.get(position);
    }

}

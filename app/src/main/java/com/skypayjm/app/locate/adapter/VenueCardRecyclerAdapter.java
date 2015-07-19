package com.skypayjm.app.locate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.skypayjm.app.locate.R;
import com.skypayjm.app.locate.model.Venue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sky on 14/7/2015.
 */
public class VenueCardRecyclerAdapter extends BaseRecyclerAdapter<Venue, MyViewHolder> {

    private RecyclerViewListener<Venue> recyclerViewListener;
    private List<Venue> venues;
    Context context;

    public VenueCardRecyclerAdapter(Context context, List<Venue> venues, RecyclerViewListener<Venue> recyclerViewListener) {
        this.context = context;
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
        // Get params:
        LinearLayout.LayoutParams loparams = (LinearLayout.LayoutParams) holder.tvName.getLayoutParams();
        if (loparams != null) {
            // Set only target params:
            loparams.weight = context.getResources().getInteger(R.integer.card_view_weightsum);
            holder.tvName.setLayoutParams(loparams);
        }
        holder.ivAddSearch.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return venues.size();
    }

    @Override
    public void updateItems(List<Venue> venues) {
        this.venues.clear();

        this.venues.addAll(venues);

    }

    public Venue getItemAtPosition(int position) {
        return venues.get(position);
    }

}

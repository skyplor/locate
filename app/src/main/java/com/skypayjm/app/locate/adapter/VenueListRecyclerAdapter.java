package com.skypayjm.app.locate.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.skypayjm.app.locate.R;
import com.skypayjm.app.locate.model.Venue;

import java.util.List;

/**
 * Created by Sky on 18/7/2015.
 */
public class VenueListRecyclerAdapter extends RecyclerView.Adapter<VenueListRecyclerAdapter.ViewHolder> {

    List<Venue> venueList;
    RecyclerViewListener<Venue> recyclerViewListener;

    public VenueListRecyclerAdapter(List<Venue> venueList, RecyclerViewListener<Venue> recyclerViewListener) {
        this.venueList = venueList;
        this.recyclerViewListener = recyclerViewListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_venue_list, parent, false);
        return new ViewHolder(itemView, recyclerViewListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Venue venue = getVenue(position);
        holder.itemTitle.setText(venue.getName());
        String address = venue.getFoursquareLocation().getAddress();
        holder.itemAddress.setText(address);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewListener.onItemClickListener(v, venue);
            }
        });
    }

    private Venue getVenue(int position) {
        return venueList.get(position);
    }

    @Override
    public int getItemCount() {
        return venueList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mView;
        TextView itemTitle;
        TextView itemAddress;
        RecyclerViewListener recyclerViewListener;

        public ViewHolder(View v, RecyclerViewListener recyclerViewListener) {
            super(v);
            mView = v;
            itemTitle = (TextView) v.findViewById(R.id.itemTitle);
            itemAddress = (TextView) v.findViewById(R.id.itemAddress);
            this.recyclerViewListener = recyclerViewListener;
        }
    }

    public void addItemsToList(Venue venue) {
        venueList.add(venue);
    }

    public void addItemsToList(List<Venue> venues) {
        venueList.clear();
        venueList.addAll(venues);
    }

}

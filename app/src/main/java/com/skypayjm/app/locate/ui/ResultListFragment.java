package com.skypayjm.app.locate.ui;


import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.skypayjm.app.locate.R;
import com.skypayjm.app.locate.adapter.RecyclerViewListener;
import com.skypayjm.app.locate.adapter.VenueListRecyclerAdapter;
import com.skypayjm.app.locate.model.ResultEvent;
import com.skypayjm.app.locate.model.Venue;
import com.skypayjm.app.locate.model.VenueDetailsEvent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_result_list)
public class ResultListFragment extends Fragment {

    private EventBus bus;
    @ViewById
    RecyclerView search_resultList;
    private VenueListRecyclerAdapter mVenueListRecyclerAdapter;
    private List<Venue> venueList;

    public ResultListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onStart() {
        super.onStart();
        bus.registerSticky(this);
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        super.onStop();
    }

    @AfterViews
    void init() {
        bus = EventBus.getDefault();
        venueList = new ArrayList<>();
        setUpRecyclerView();
    }

    // This method will be called when a ResultEvent is posted
    public void onEvent(ResultEvent resultEvent) {
        if (resultEvent != null) {
            List<Venue> venues = resultEvent.getResults();
            if (venues != null && !venues.isEmpty()) {
                venueList = venues;
                display();
            }
        }
    }

    private void display() {
        mVenueListRecyclerAdapter.addItemsToList(venueList);
        mVenueListRecyclerAdapter.notifyDataSetChanged();
    }

    private void setUpRecyclerView() {
        search_resultList.setHasFixedSize(true);
        // Define 3 column grid layout
        final LinearLayoutManager layout = new LinearLayoutManager(getActivity());
        layout.setOrientation(LinearLayoutManager.VERTICAL);
        search_resultList.setLayoutManager(layout);
        InitializeAdapters();
        search_resultList.setAdapter(mVenueListRecyclerAdapter);
    }

    private void InitializeAdapters() {
        mVenueListRecyclerAdapter = new VenueListRecyclerAdapter(venueList, new RecyclerViewListener<Venue>() {
            @Override
            public void onItemClickListener(View view, Venue item) {
                VenueActivity_.intent(getActivity()).start();
                VenueDetailsEvent venueDetailsEvent = new VenueDetailsEvent();
                venueDetailsEvent.setVenue(item);
                bus.postSticky(venueDetailsEvent);
            }
        });
    }
}

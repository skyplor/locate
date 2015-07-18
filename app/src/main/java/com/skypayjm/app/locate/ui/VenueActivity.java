package com.skypayjm.app.locate.ui;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.skypayjm.app.locate.R;
import com.skypayjm.app.locate.model.FoursquareVenueDetail;
import com.skypayjm.app.locate.model.event.VenueDetailsEvent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import de.greenrobot.event.EventBus;

@EActivity(R.layout.activity_venue)
public class VenueActivity extends AppCompatActivity {

    private EventBus bus;
    @ViewById
    TextView venue_details;
    @ViewById
    Toolbar venue_toolbar;

    @Override
    protected void onStart() {
        bus.registerSticky(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        bus.unregister(this);
        super.onStop();
    }

    @AfterViews
    void init() {
        setupUI();
        initializeVariables();
    }

    private void initializeVariables() {
        bus = EventBus.getDefault();
    }

    private void setupUI() {
        setSupportActionBar(venue_toolbar);
        venue_details.setText("Bummer! We couldn't get any details of this place");
    }

    public void onEvent(VenueDetailsEvent venueDetailsEvent) {
        if (venueDetailsEvent != null) {
            FoursquareVenueDetail venueDetail = venueDetailsEvent.getVenue();
            if (getSupportActionBar() != null && venueDetail != null)
                getSupportActionBar().setTitle(venueDetail.getName());
            venue_details.setText(venueDetailsEvent.getVenue().toString());
            bus.removeStickyEvent(venueDetailsEvent);
        }
    }
}

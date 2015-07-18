package com.skypayjm.app.locate.ui;

import android.app.Activity;

import com.skypayjm.app.locate.R;
import com.skypayjm.app.locate.model.VenueDetailsEvent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

@EActivity(R.layout.activity_venue)
public class VenueActivity extends Activity {

    private EventBus bus;

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
        bus = EventBus.getDefault();


    }

    public void onEvent(VenueDetailsEvent venueDetailsEvent) {
        if (venueDetailsEvent != null) {
            Timber.i("Inside venue activity: " + venueDetailsEvent.getVenue().getName());
        }

        bus.removeStickyEvent(venueDetailsEvent);
    }
}

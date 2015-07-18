package com.skypayjm.app.locate.model.event;

import com.skypayjm.app.locate.model.FoursquareVenueDetail;

/**
 * Created by Sky on 17/7/2015.
 */
public class VenueDetailsEvent {
    FoursquareVenueDetail venue;

    public FoursquareVenueDetail getVenue() {
        return venue;
    }

    public void setVenue(FoursquareVenueDetail venue) {
        this.venue = venue;
    }
}

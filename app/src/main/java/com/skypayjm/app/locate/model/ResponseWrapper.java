package com.skypayjm.app.locate.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sky on 14/7/2015.
 */
public class ResponseWrapper {
    public static final String FIELD_CATEGORIES = "categories";
    public static final String FIELD_MINIVENUES = "minivenues";
    public static final String FIELD_VENUES = "venues";
    public static final String FIELD_VENUE = "venue";

    @SerializedName(FIELD_CATEGORIES)
    @Nullable
    private List<Category> categories;

    @Nullable
    public List<Category> getCategories() {
        return categories;
    }

    @SerializedName(FIELD_MINIVENUES)
    @Nullable
    private List<Venue> suggestedVenues;

    @Nullable
    public List<Venue> getSuggestedVenues() {
        return suggestedVenues;
    }

    @SerializedName(FIELD_VENUES)
    @Nullable
    private List<Venue> searchedVenues;

    @Nullable
    public List<Venue> getSearchedVenues() {
        return searchedVenues;
    }

    @SerializedName(FIELD_VENUE)
    @Nullable
    private VenueDetail venueDetail;

    @Nullable
    public VenueDetail getVenueDetail() {
        return venueDetail;
    }
}

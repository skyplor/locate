package com.skypayjm.app.locate.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sky on 14/7/2015.
 */
public class Venue {
    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_LOCATION = "location";
    public static final String FIELD_CATEGORIES = "categories";

    @SerializedName(FIELD_ID)
    @NonNull
    private String id;

    @SerializedName(FIELD_NAME)
    @NonNull
    private String name;

    @SerializedName(FIELD_LOCATION)
    @NonNull
    private FoursquareLocation foursquareLocation;

    @SerializedName(FIELD_CATEGORIES)
    @NonNull
    private List<Category> categories;

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public FoursquareLocation getFoursquareLocation() {
        return foursquareLocation;
    }

    public void setFoursquareLocation(@NonNull FoursquareLocation foursquareLocation) {
        this.foursquareLocation = foursquareLocation;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }


    @NonNull
    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(@NonNull List<Category> categories) {
        this.categories = categories;
    }
}

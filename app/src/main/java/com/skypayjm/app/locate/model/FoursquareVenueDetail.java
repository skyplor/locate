package com.skypayjm.app.locate.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.skypayjm.app.locate.util.Utility;

import java.util.List;

/**
 * Created by Sky on 14/7/2015.
 */
public class FoursquareVenueDetail {
    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_CONTACT = "contact";
    public static final String FIELD_LOCATION = "location";
    public static final String FIELD_CATEGORIES = "categories";
    public static final String FIELD_HOURS = "hours";

    @SerializedName(FIELD_ID)
    @NonNull
    private String id;

    @SerializedName(FIELD_NAME)
    @NonNull
    private String name;

    @SerializedName(FIELD_CONTACT)
    @NonNull
    private FoursquareContact contact;

    @SerializedName(FIELD_LOCATION)
    @NonNull
    private FoursquareLocation foursquareLocation;

    @SerializedName(FIELD_CATEGORIES)
    @NonNull
    private List<Category> categories;

    @SerializedName(FIELD_HOURS)
    @Nullable
    private FoursquareHours hours;

    @Nullable
    public FoursquareHours getHours() {
        return hours;
    }

    public void setHours(@Nullable FoursquareHours hours) {
        this.hours = hours;
    }

    @NonNull
    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(@NonNull List<Category> categories) {
        this.categories = categories;
    }

    @NonNull
    public FoursquareLocation getFoursquareLocation() {
        return foursquareLocation;
    }

    public void setFoursquareLocation(@NonNull FoursquareLocation foursquareLocation) {
        this.foursquareLocation = foursquareLocation;
    }

    @NonNull
    public FoursquareContact getContact() {
        return contact;
    }

    public void setContact(@NonNull FoursquareContact contact) {
        this.contact = contact;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String toString() {
        return String.format("%s\n%s: %s\n%s: %s", Utility.isNullorEmpty(contact), FIELD_LOCATION, Utility.isNullorEmpty(foursquareLocation.getAddress()), FIELD_HOURS, Utility.isNullorEmpty(hours));
    }
}

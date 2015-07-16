package com.skypayjm.app.locate.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sky on 14/7/2015.
 */
public class FoursquareLocation {
    public static final String FIELD_ADDRESS = "address";
    public static final String FIELD_CROSSSTREET = "crossStreet";
    public static final String FIELD_CITY = "city";
    public static final String FIELD_STATE = "state";
    public static final String FIELD_POSTALCODE = "postalCode";
    public static final String FIELD_COUNTRY = "country";
    public static final String FIELD_LATITUDE = "lat";
    public static final String FIELD_LONGITUDE = "lng";
    public static final String FIELD_DISTANCE = "distance";

    @SerializedName(FIELD_ADDRESS)
    @NonNull
    private String address;

    @SerializedName(FIELD_CROSSSTREET)
    @NonNull
    private String crossStreet;

    @SerializedName(FIELD_CITY)
    @NonNull
    private String city;

    @SerializedName(FIELD_STATE)
    @NonNull
    private String state;

    @SerializedName(FIELD_POSTALCODE)
    @NonNull
    private String postalCode;

    @SerializedName(FIELD_COUNTRY)
    @NonNull
    private String country;
    @SerializedName(FIELD_LATITUDE)
    @NonNull
    private double lat;
    @SerializedName(FIELD_LONGITUDE)
    @NonNull
    private double lng;


    @SerializedName(FIELD_DISTANCE)
    @NonNull
    private double distance;

    @NonNull
    public double getDistance() {
        return distance;
    }

    public void setDistance(@NonNull double distance) {
        this.distance = distance;
    }

    @NonNull
    public String getAddress() {
        return address;
    }

    public void setAddress(@NonNull String address) {
        this.address = address;
    }

    @NonNull
    public String getCrossStreet() {
        return crossStreet;
    }

    public void setCrossStreet(@NonNull String crossStreet) {
        this.crossStreet = crossStreet;
    }

    @NonNull
    public String getCity() {
        return city;
    }

    public void setCity(@NonNull String city) {
        this.city = city;
    }

    @NonNull
    public String getState() {
        return state;
    }

    public void setState(@NonNull String state) {
        this.state = state;
    }

    @NonNull
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(@NonNull String postalCode) {
        this.postalCode = postalCode;
    }

    @NonNull
    public String getCountry() {
        return country;
    }

    public void setCountry(@NonNull String country) {
        this.country = country;
    }

    @NonNull
    public double getLat() {
        return lat;
    }

    public void setLat(@NonNull double lat) {
        this.lat = lat;
    }

    @NonNull
    public double getLng() {
        return lng;
    }

    public void setLng(@NonNull double lng) {
        this.lng = lng;
    }

}

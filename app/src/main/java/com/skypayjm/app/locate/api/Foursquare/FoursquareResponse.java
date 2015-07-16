package com.skypayjm.app.locate.api.Foursquare;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.skypayjm.app.locate.model.Meta;
import com.skypayjm.app.locate.model.ResponseWrapper;

/**
 * Created by Sky on 14/7/2015.
 */
public class FoursquareResponse {
    public static final String FIELD_RESPONSE = "response";
    public static final String FIELD_META = "meta";

    @SerializedName(FIELD_META)
    @NonNull
    private Meta meta;
    @SerializedName(FIELD_RESPONSE)
    @Nullable
    private ResponseWrapper response;

    @NonNull
    public Meta getMeta() {
        return meta;
    }

    public ResponseWrapper getResponse() {
        return response;
    }
}
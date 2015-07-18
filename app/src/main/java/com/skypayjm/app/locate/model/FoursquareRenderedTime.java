package com.skypayjm.app.locate.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.skypayjm.app.locate.util.Utility;

/**
 * Created by Sky on 18/7/2015.
 */
public class FoursquareRenderedTime {
    public static final String FIELD_RENDEREDTIME = "renderedTime";

    @SerializedName(FIELD_RENDEREDTIME)
    @NonNull
    private String renderedTime;

    @NonNull
    public String getRenderedTime() {
        return renderedTime;
    }

    public void setRenderedTime(@NonNull String renderedTime) {
        this.renderedTime = renderedTime;
    }

    public String toString() {
        return Utility.isNullorEmpty(renderedTime);
    }
}

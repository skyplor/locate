package com.skypayjm.app.locate.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.skypayjm.app.locate.util.Utility;

import java.util.List;

/**
 * Created by Sky on 18/7/2015.
 */
public class FoursquareTimeframes {
    public static final String FIELD_DAYS = "days";
    public static final String FIELD_OPEN = "open";

    @SerializedName(FIELD_DAYS)
    @NonNull
    private String days;

    @SerializedName(FIELD_OPEN)
    @NonNull
    private List<FoursquareRenderedTime> open;

    @NonNull
    public List<FoursquareRenderedTime> getOpen() {
        return open;
    }

    public void setOpen(@NonNull List<FoursquareRenderedTime> open) {
        this.open = open;
    }

    @NonNull
    public String getDays() {
        return days;
    }

    public void setDays(@NonNull String days) {
        this.days = days;
    }

    public String toString() {
        StringBuilder renderedTimesStringBuilder = new StringBuilder();
        for (FoursquareRenderedTime foursquareRenderedTime : open) {
            renderedTimesStringBuilder.append(foursquareRenderedTime.toString()).append("\n");
        }
        return String.format("%s: %s\n%s: %s\n", FIELD_DAYS, days, FIELD_OPEN, Utility.isNullorEmpty(renderedTimesStringBuilder.toString()));
    }
}

package com.skypayjm.app.locate.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.skypayjm.app.locate.util.Utility;

import java.util.List;

/**
 * Created by Sky on 18/7/2015.
 */
public class FoursquareHours {

    public static final String FIELD_STATUS = "status";
    public static final String FIELD_ISOPEN = "isOpen";
    public static final String FIELD_TIMEFRAMES = "timeframes";

    @SerializedName(FIELD_STATUS)
    @NonNull
    private String status;

    @SerializedName(FIELD_ISOPEN)
    @NonNull
    private String isOpen;

    @SerializedName(FIELD_TIMEFRAMES)
    @NonNull
    private List<FoursquareTimeframes> timeframes;

    @NonNull
    public List<FoursquareTimeframes> getTimeframes() {
        return timeframes;
    }

    public void setTimeframes(@NonNull List<FoursquareTimeframes> timeframes) {
        this.timeframes = timeframes;
    }

    @NonNull
    public String getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(@NonNull String isOpen) {
        this.isOpen = isOpen;
    }

    @NonNull
    public String getStatus() {
        return status;
    }

    public void setStatus(@NonNull String status) {
        this.status = status;
    }

    public String toString() {
        StringBuilder timeframesStringBuilder = new StringBuilder();
        for (FoursquareTimeframes foursquareTimeframes : timeframes) {
            timeframesStringBuilder.append(foursquareTimeframes.toString()).append("\n");
        }
        return String.format("\n%s: %s\n%s: %s\n%s", FIELD_STATUS, status, FIELD_ISOPEN, isOpen, Utility.isNullorEmpty(timeframesStringBuilder.toString()));
    }
}

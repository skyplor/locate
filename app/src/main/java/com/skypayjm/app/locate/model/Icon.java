package com.skypayjm.app.locate.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sky on 14/7/2015.
 */
public class Icon {
    public static final String FIELD_PREFIX = "prefix";
    public static final String FIELD_SUFFIX = "suffix";

    @SerializedName(FIELD_PREFIX)
    @NonNull
    private String prefix;
    @SerializedName(FIELD_SUFFIX)
    @NonNull
    private String suffix;

    @NonNull
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(@NonNull String prefix) {
        this.prefix = prefix;
    }

    @NonNull
    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(@NonNull String suffix) {
        this.suffix = suffix;
    }
}

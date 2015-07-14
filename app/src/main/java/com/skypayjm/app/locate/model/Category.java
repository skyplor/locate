package com.skypayjm.app.locate.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sky on 14/7/2015.
 */
public class Category {
    public static final String FIELD_ID = "id";
    public static final String FIELD_ICON = "icon";
    public static final String FIELD_SHORT_NAME = "shortName";
    public static final String FIELD_PLURAL_NAME = "pluralName";
    public static final String FIELD_NAME = "name";

    @SerializedName(FIELD_ID) @NonNull private String id;
    @SerializedName(FIELD_ICON) @NonNull private Icon icon;
    @SerializedName(FIELD_SHORT_NAME) @NonNull private String shortName;
    @SerializedName(FIELD_PLURAL_NAME) @NonNull private String pluralName;
    @SerializedName(FIELD_NAME) @NonNull private String name;

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public Icon getIcon() {
        return icon;
    }

    public void setIcon(@NonNull Icon icon) {
        this.icon = icon;
    }

    @NonNull
    public String getShortName() {
        return shortName;
    }

    public void setShortName(@NonNull String shortName) {
        this.shortName = shortName;
    }

    @NonNull
    public String getPluralName() {
        return pluralName;
    }

    public void setPluralName(@NonNull String pluralName) {
        this.pluralName = pluralName;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }
}

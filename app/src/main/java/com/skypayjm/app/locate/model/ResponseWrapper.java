package com.skypayjm.app.locate.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sky on 14/7/2015.
 */
public class ResponseWrapper {
    public static final String FIELD_CATEGORIES = "categories";

    @SerializedName(FIELD_CATEGORIES)
    @Nullable
    private List<Category> categories;

    @Nullable
    public List<Category> getCategories() {
        return categories;
    }
}

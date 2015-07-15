package com.skypayjm.app.locate.model;

import android.support.annotation.NonNull;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sky on 15/7/2015.
 */
public class CategoryLevel extends RealmObject {
    public static String ID = "id";
    public static String CATEGORY = "category";
    public static String LEVEL = "level";

    @PrimaryKey
    @NonNull
    private String id;
    @NonNull
    private Category category;
    @NonNull
    private int level;

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public Category getCategory() {
        return category;
    }

    public void setCategory(@NonNull Category category) {
        this.category = category;
    }

    @NonNull
    public int getLevel() {
        return level;
    }

    public void setLevel(@NonNull int level) {
        this.level = level;
    }
}

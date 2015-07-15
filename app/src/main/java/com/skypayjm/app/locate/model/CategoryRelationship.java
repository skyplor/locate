package com.skypayjm.app.locate.model;

import android.support.annotation.NonNull;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sky on 16/7/2015.
 */
public class CategoryRelationship extends RealmObject {
    public static String ID = "id";
    public static String CHILDCATEGORY = "childCategory";
    public static String PARENTCATEGORY = "parentCategory";
    public static String PARENTID = "parentID";

    @PrimaryKey
    @NonNull
    private String id;

    @NonNull
    private Category childCategory;

    @NonNull
    private String parentID;

    @NonNull
    private Category parentCategory;

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public Category getChildCategory() {
        return childCategory;
    }

    public void setChildCategory(@NonNull Category childCategory) {
        this.childCategory = childCategory;
    }

    @NonNull
    public String getParentID() {
        return parentID;
    }

    public void setParentID(@NonNull String parentID) {
        this.parentID = parentID;
    }

    @NonNull
    public Category getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(@NonNull Category parentCategory) {
        this.parentCategory = parentCategory;
    }
}

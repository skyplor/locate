package com.skypayjm.app.locate.adapter;

import android.content.Context;

import com.skypayjm.app.locate.model.Category;

import io.realm.RealmResults;

/**
 * Created by Sky on 15/7/2015.
 */
public class RealmCategoryAdapter extends RealmModelAdapter<Category> {
    public RealmCategoryAdapter(Context context, RealmResults<Category> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }
}
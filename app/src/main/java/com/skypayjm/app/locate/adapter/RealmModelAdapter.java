package com.skypayjm.app.locate.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import io.realm.RealmBaseAdapter;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by Sky on 15/7/2015.
 */
public class RealmModelAdapter<T extends RealmObject> extends RealmBaseAdapter<T> {
    RealmResults<T> realmResults;

    public RealmModelAdapter(Context context, RealmResults<T> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
        this.realmResults = realmResults;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    public RealmResults<T> getRealmResults() {
        return realmResults;
    }
}
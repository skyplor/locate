package com.skypayjm.app.locate;

import android.app.Application;

import org.androidannotations.annotations.EApplication;

import timber.log.Timber;

/**
 * Created by Sky on 12/7/2015.
 */
@EApplication
public class LocateApplication extends Application {

    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}

package com.skypayjm.app.locate;

import android.app.Application;
import android.content.Context;

import net.grandcentrix.tray.TrayAppPreferences;

import org.androidannotations.annotations.EApplication;

import timber.log.Timber;

/**
 * Created by Sky on 12/7/2015.
 */
@EApplication
public class LocateApplication extends Application {
    // create a preference accessor. This is for global app preferences.
    public static TrayAppPreferences appPreferences;
    private static Context mContext;

    public void onCreate() {
        super.onCreate();
        mContext = this;
        appPreferences = new TrayAppPreferences(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public static Context getContext() {
        return mContext;
    }
}

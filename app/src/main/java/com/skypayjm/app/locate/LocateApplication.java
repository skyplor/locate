package com.skypayjm.app.locate;

import android.app.Application;
import android.content.Context;

import org.androidannotations.annotations.EApplication;

import ru.noties.simpleprefs.SimplePref;
import timber.log.Timber;

/**
 * Created by Sky on 12/7/2015.
 */
@EApplication
public class LocateApplication extends Application {
    private static Context mContext;

    public void onCreate() {
        super.onCreate();
        mContext = this;

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public static Context getContext() {
        return mContext;
    }
}

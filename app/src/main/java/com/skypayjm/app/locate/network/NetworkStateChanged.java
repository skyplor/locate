package com.skypayjm.app.locate.network;

/**
 * Created by Sky on 16/7/2015.
 */
public class NetworkStateChanged {

    private boolean mIsInternetConnected;

    public NetworkStateChanged(boolean isInternetConnected) {
        this.mIsInternetConnected = isInternetConnected;
    }

    public boolean isInternetConnected() {
        return this.mIsInternetConnected;
    }
}
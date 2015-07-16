package com.skypayjm.app.locate.api.Foursquare;

import com.skypayjm.app.locate.LocateApplication_;
import com.skypayjm.app.locate.R;

import retrofit.RequestInterceptor;

/**
 * Created by Sky on 14/7/2015.
 */
public class FoursquareRequestInterceptor implements RequestInterceptor {
    @Override
    public void intercept(RequestFacade request) {
        request.addQueryParam("client_id", LocateApplication_.getContext().getResources().getString(R.string.foursquare_client_id));
        request.addQueryParam("client_secret", LocateApplication_.getContext().getResources().getString(R.string.foursquare_client_secret));
        request.addQueryParam("v", "20150714");
        request.addQueryParam("m", "foursquare");
    }
}
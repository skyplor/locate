package com.skypayjm.app.locate.api;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;

/**
 * Created by Sky on 14/7/2015.
 */
public interface FoursquareService {

    @GET("/venues/categories")
    FoursquareResponse venuesCategories() throws FoursquareException;

    @GET("/venues/categories")
    void venuesCategories(
            Callback<FoursquareResponse> callback);

    class Implementation {
        public static FoursquareService get() {
            return getBuilder()
                    .build()
                    .create(FoursquareService.class);
        }

        static RestAdapter.Builder getBuilder() {
            return new RestAdapter.Builder()
                    .setEndpoint("https://api.foursquare.com/v2")
                    .setRequestInterceptor(new FoursquareRequestInterceptor())
                    .setErrorHandler(new FoursquareErrorHandler());
        }
    }
}

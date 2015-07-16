package com.skypayjm.app.locate.api.Foursquare;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.skypayjm.app.locate.BuildConfig;

import io.realm.RealmObject;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Sky on 14/7/2015.
 */
public interface FoursquareService {

    // Synchronized
    @GET("/venues/categories")
    FoursquareResponse venuesCategories() throws FoursquareException;

    @GET("/venues/suggestcompletion")
    void suggestCompletion(@Query(value = "ll", encodeValue = false) String location, @Query("query") String query) throws FoursquareException;

    //Non-blocking, asynchronous
    @GET("/venues/categories")
    void venuesCategories(Callback<FoursquareResponse> callback);

    @GET("/venues/suggestcompletion")
    void suggestCompletion(@Query(value = "ll", encodeValue = false) String location, @Query("query") String query, Callback<FoursquareResponse> callback);


    @GET("/venues/search")
    void search(@Query(value = "ll", encodeValue = false) String location, @Query("radius") int radius, @Query("query") String query, @Query("categoryId") String categoryIdCommaSeparated, @Query("intent") String intent) throws FoursquareException;

    @GET("/venues/search")
    void search(@Query(value = "ll", encodeValue = false) String location, @Query("radius") int radius, @Query("query") String query, @Query("categoryId") String categoryIdCommaSeparated, @Query("intent") String intent, Callback<FoursquareResponse> callback);


    class Implementation {
        public static FoursquareService get() {
            return getBuilder()
                    .build()
                    .create(FoursquareService.class);
        }

        static RestAdapter.Builder getBuilder() {
            // Allows GSON with Realm to go hand-in-hand
            Gson gson = new GsonBuilder()
                    .setExclusionStrategies(new ExclusionStrategy() {
                        @Override
                        public boolean shouldSkipField(FieldAttributes f) {
                            return f.getDeclaringClass().equals(RealmObject.class);
                        }

                        @Override
                        public boolean shouldSkipClass(Class<?> clazz) {
                            return false;
                        }
                    })
                    .create();
            return new RestAdapter.Builder()
                    .setEndpoint("https://api.foursquare.com/v2")
                    .setRequestInterceptor(new FoursquareRequestInterceptor())
                    .setConverter(new GsonConverter(gson))
                    .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                    .setErrorHandler(new FoursquareErrorHandler());
        }
    }
}

package com.skypayjm.app.locate.api;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;

/**
 * Created by Sky on 14/7/2015.
 */
public class FoursquareErrorHandler implements ErrorHandler {
    @Override
    public Throwable handleError(RetrofitError cause) {
        if (cause.getResponse() != null && cause.getSuccessType() == FoursquareResponse.class) {
            FoursquareResponse response = (FoursquareResponse) cause.getBody();
            if (response.getMeta().getErrorDetail() != null) {
                return new FoursquareException(response.getMeta().getErrorDetail(), cause);
            }
        }
        return cause;
    }
}

package com.skypayjm.app.locate.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sky on 14/7/2015.
 */
public class Meta {
    public static final String FIELD_CODE = "code";
    public static final String FIELD_ERROR_TYPE = "errorType";
    public static final String FIELD_ERROR_DETAIL = "errorDetail";
    public static final String FIELD_MESSAGE = "message";

    @SerializedName(FIELD_CODE)
    private int code;
    @SerializedName(FIELD_ERROR_TYPE)
    @Nullable
    private String errorType;
    @SerializedName(FIELD_ERROR_DETAIL)
    @Nullable
    private String errorDetail;
    @SerializedName(FIELD_MESSAGE)
    @Nullable
    private String message;

    public int getCode() {
        return code;
    }

    @Nullable
    public String getErrorType() {
        return errorType;
    }

    @Nullable
    public String getErrorDetail() {
        return errorDetail;
    }

    @Nullable
    public String getMessage() {
        return message;
    }
}

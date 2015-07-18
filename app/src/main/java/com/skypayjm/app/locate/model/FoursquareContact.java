package com.skypayjm.app.locate.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.skypayjm.app.locate.util.Utility;

/**
 * Created by Sky on 18/7/2015.
 */
public class FoursquareContact {
    public static final String FIELD_PHONE = "phone";
    public static final String FIELD_FORMATTEDPHONE = "formattedPhone";

    @SerializedName(FIELD_PHONE)
    @NonNull
    private String phone;

    @SerializedName(FIELD_FORMATTEDPHONE)
    @NonNull
    private String formattedPhone;

    @NonNull
    public String getFormattedPhone() {
        return formattedPhone;
    }

    public void setFormattedPhone(@NonNull String formattedPhone) {
        this.formattedPhone = formattedPhone;
    }

    @NonNull
    public String getPhone() {
        return phone;
    }

    public void setPhone(@NonNull String phone) {
        this.phone = phone;
    }

    public String toString() {
        return String.format("%s: %s", FIELD_PHONE, Utility.isNullorEmpty(formattedPhone));
    }
}

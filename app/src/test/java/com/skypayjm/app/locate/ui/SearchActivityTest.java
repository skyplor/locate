package com.skypayjm.app.locate.ui;

import android.support.v7.widget.Toolbar;

import com.skypayjm.app.locate.BuildConfig;
import com.skypayjm.app.locate.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertNotNull;

/**
 * Created by Sky on 15/7/2015.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 18, reportSdk = 18)
public class SearchActivityTest {
    private SearchActivity_ activity;
    private Toolbar mToolbar;

    @Before
    public void setup() throws Exception {
        activity = Robolectric.setupActivity(SearchActivity_.class);
        mToolbar = (Toolbar) activity.findViewById(R.id.search_toolbar);
    }

    @Test
    public void validateLayout() throws Exception {

        assertNotNull("MainActivity is not instantiated", activity);
        assertNotNull("Toolbar could not be found", mToolbar);
    }
}

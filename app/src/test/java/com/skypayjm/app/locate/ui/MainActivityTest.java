package com.skypayjm.app.locate.ui;

import android.app.Activity;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuView;
import com.quinny898.library.persistentsearch.SearchBox;
import com.skypayjm.app.locate.BuildConfig;
import com.skypayjm.app.locate.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by Sky on 13/7/2015.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 18, reportSdk = 18)
public class MainActivityTest {
    private MainActivity_ activity;
    private Toolbar tbMain;
    private ActionMenuItemView map_list;
    private SearchBox searchBox;
    private ImageView drawerLogo;
    private EditText searchText;
    private TextView searchTextLogo;
    private ImageView mic;
    private MaterialMenuView material_menu_button;

    @Before
    public void setup() throws Exception {
        activity = Robolectric.setupActivity(MainActivity_.class);
        tbMain = (Toolbar) activity.findViewById(R.id.main_toolbar);
        map_list = (ActionMenuItemView) tbMain.findViewById(R.id.action_map_list);
        searchBox = (SearchBox) activity.findViewById(R.id.searchbox);
        drawerLogo = (ImageView) searchBox.findViewById(R.id.drawer_logo);
        searchTextLogo = (TextView) searchBox.findViewById(R.id.logo);
        searchText = (EditText) searchBox.findViewById(R.id.search);
        mic = (ImageView) searchBox.findViewById(R.id.mic);
        material_menu_button = (MaterialMenuView) searchBox.findViewById(R.id.material_menu_button);
    }

    @Test
    public void validateLayout() throws Exception {
        assertNotNull("MainActivity is not instantiated", activity);
        assertNotNull("Toolbar could not be found", tbMain);
        assertNotNull("toolbar_map_list could not be found", map_list);
        assertNotNull("Searchbox could not be found", searchBox);
        assertNotNull("drawerLogo could not be found", drawerLogo);
        assertNotNull("SearchTextLogo could not be found", searchTextLogo);
        assertNotNull("SearchText could not be found", searchText);
        assertNotNull("Mic could not be found", mic);
        assertNotNull("material_menu_button could not be found", material_menu_button);
    }

    @Test
    public void validateToolbarContent() throws Exception {
        assertTrue("Toolbar contains incorrect title", "Locate".equals(tbMain.getTitle().toString()));
    }

    @Test
    public void titleIsCorrect() throws Exception {
        Activity activity = Robolectric.setupActivity(MainActivity_.class);
        assertTrue(activity.getTitle().toString().equals("Locate"));
    }

    @Test
    public void validateSearchboxContent() throws Exception {
        assertTrue("SearchTextLogo doesn't have a onclicklistener assigned", searchTextLogo.performClick());
        assertTrue("Material Menu View is not visible", material_menu_button.getVisibility() == View.VISIBLE);
        assertTrue("SearchTextLogo is still showing", searchTextLogo.getVisibility() == View.GONE);
        searchText.setText("h");
        assertTrue("search text is not set", searchText.getText().equals("h"));
        assertTrue("mic doesn't have a onclicklistener assigned", mic.performClick());
        assertTrue("search text not cleared", searchText.getText().equals(""));
        assertTrue("material_menu_button doesn't have a onclicklistener assigned", material_menu_button.performClick());
        assertTrue("Drawerlogo is not visible", drawerLogo.getVisibility() == View.VISIBLE);
        assertTrue("Material Menu View is not invisible", material_menu_button.getVisibility() == View.INVISIBLE);
        assertTrue("SearchTextLogo is not shown", searchTextLogo.getVisibility() == View.VISIBLE);
    }

    @Test
    public void validateMap_ListView() throws Exception {
        // Click menu
//        assertTrue("map_list doesn't have a onclicklistener assigned", map_list.performClick());
    }
}

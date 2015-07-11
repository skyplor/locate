package com.skypayjm.app.locate.view;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.quinny898.library.persistentsearch.SearchBox;
import com.skypayjm.app.locate.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.menu_main)
public class MainActivity extends AppCompatActivity {

    boolean isMapMode;

    @ViewById
    Toolbar main_toolbar;
    @ViewById
    SearchBox searchbox;

    @OptionsMenuItem
    MenuItem toolbar_map_list;

    @AfterViews
    protected void init() {
        setSupportActionBar(main_toolbar);
        isMapMode = true;
        searchbox.enableVoiceRecognition(this);
        searchbox.setSearchListener(new SearchBox.SearchListener() {
            @Override
            public void onSearchOpened() {

            }

            @Override
            public void onSearchCleared() {

            }

            @Override
            public void onSearchClosed() {

            }

            @Override
            public void onSearchTermChanged() {

            }

            @Override
            public void onSearch(String searchTerm) {
                Toast.makeText(MainActivity.this, searchTerm + " Searched", Toast.LENGTH_LONG).show();
                searchGooglePlaces(searchTerm);
            }
        });
    }

    private void searchGooglePlaces(String searchTerm) {

    }

    @OptionsItem
    void toolbar_map_listSelected() {
        isMapMode = !isMapMode;
        if (isMapMode) {
            Toast.makeText(MainActivity.this, "Now showing Map View with List icon", Toast.LENGTH_SHORT).show();
            loadMapFragment();
            //This has to be opposite as the current view
            toolbar_map_list.setIcon(R.drawable.ic_list_white_24dp);
        } else {
            Toast.makeText(MainActivity.this, "Now showing List View with Map icon", Toast.LENGTH_SHORT).show();
            loadListFragment();
            //This has to be opposite as the current view
            toolbar_map_list.setIcon(R.drawable.ic_map_white_24dp);
        }
    }

    private void loadListFragment() {

    }

    private void loadMapFragment() {

    }
}

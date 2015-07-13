package com.skypayjm.app.locate.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.quinny898.library.persistentsearch.SearchBox;
import com.skypayjm.app.locate.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import timber.log.Timber;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.menu_main)
public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG_MAP = "MAP", TAG_LIST = "LIST";
    boolean isMapMode;
    private GoogleApiClient mGoogleApiClient;

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;

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
        initializeSearchbox();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, new ResultMapFragment_(), TAG_MAP);
        fragmentTransaction.add(R.id.fragment_container, new ResultListFragment_(), TAG_LIST);
        fragmentTransaction.commit();
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public void onResume() {
        super.onResume();
        if (this.isMapMode) {
            switchToMap();
        } else {
            switchToList();
        }
    }

    private void initializeSearchbox() {
        searchbox.enableVoiceRecognition(this);
        searchbox.setLogoText(getResources().getString(R.string.abc_search_hint));
        searchbox.setLogoTextColor(Color.parseColor("#b3000000"));
        searchbox.setMenuVisibility(View.INVISIBLE);
        setDrawerLogo(R.drawable.ic_search_black_24dp);
        searchbox.setSearchListener(new SearchBox.SearchListener() {
            @Override
            public void onSearchOpened() {
                searchbox.setMenuVisibility(View.VISIBLE);
            }

            @Override
            public void onSearchCleared() {

            }

            @Override
            public void onSearchClosed() {
                searchbox.setMenuVisibility(View.INVISIBLE);
            }

            @Override
            public void onSearchTermChanged() {
                // We will show suggestions by grabbing the last word and see if it matches the list of words.
                // If yes, we show the suggestions relating to that word
                String s = searchbox.getSearchText();
                if (s.length() > 0 && s.substring(s.length() - 1).equals(" ")) {
                    String[] strArray = s.split(" ");
                    if (strArray.length > 0)
                        Timber.i("Last word is '%s'", strArray[strArray.length - 1]);
                }
            }

            @Override
            public void onSearch(String searchTerm) {
                Toast.makeText(MainActivity.this, searchTerm + " Searched", Toast.LENGTH_LONG).show();
                searchGooglePlaces(searchTerm);
            }
        });
    }

    private void setDrawerLogo(int drawerLogo) {
        searchbox.setDrawerLogo(ContextCompat.getDrawable(this, drawerLogo));
    }

    private void searchGooglePlaces(String searchTerm) {

    }

    @OptionsItem
    void toolbar_map_listSelected() {
        isMapMode = !isMapMode;
        if (isMapMode) {
            Toast.makeText(MainActivity.this, "Now showing Map View with List icon", Toast.LENGTH_SHORT).show();
            switchToMap();
            //This has to be opposite as the current view
            toolbar_map_list.setIcon(R.drawable.ic_list_white_24dp);
        } else {
            Toast.makeText(MainActivity.this, "Now showing List View with Map icon", Toast.LENGTH_SHORT).show();
            switchToList();
            //This has to be opposite as the current view
            toolbar_map_list.setIcon(R.drawable.ic_map_white_24dp);
        }
    }

    private void switchToMap() {
        ResultMapFragment fragMap = (ResultMapFragment_) getSupportFragmentManager().findFragmentByTag(TAG_MAP);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.detach(getSupportFragmentManager().findFragmentByTag(TAG_LIST));
        fragmentTransaction.attach(fragMap);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commitAllowingStateLoss();
        getSupportFragmentManager().executePendingTransactions();
    }

    private void switchToList() {
        ResultListFragment fragList = (ResultListFragment_) getSupportFragmentManager().findFragmentByTag(TAG_LIST);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.detach(getSupportFragmentManager().findFragmentByTag(TAG_MAP));
        fragmentTransaction.attach(fragList);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commitAllowingStateLoss();
        getSupportFragmentManager().executePendingTransactions();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SearchBox.VOICE_RECOGNITION_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            searchbox.populateEditText(matches);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Connected to Google Play services!
        // The good stuff goes here.
        Timber.i("Google connected!");
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
        Timber.i("Connection to Google is disconnected!");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // This callback is important for handling errors that
        // may occur while attempting to connect with Google.
        //
        // More about this in the 'Handle Connection Failures' section.
        Timber.i("Connection to Google failed...");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
}

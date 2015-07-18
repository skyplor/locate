package com.skypayjm.app.locate.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.skypayjm.app.locate.R;
import com.skypayjm.app.locate.api.Foursquare.FoursquareResponse;
import com.skypayjm.app.locate.api.Foursquare.FoursquareService;
import com.skypayjm.app.locate.model.FoursquareVenueDetail;
import com.skypayjm.app.locate.model.Venue;
import com.skypayjm.app.locate.model.event.ResultEvent;
import com.skypayjm.app.locate.model.event.VenueDetailsEvent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

@EActivity(R.layout.activity_result)
@OptionsMenu(R.menu.menu_main)
public class ResultsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG_MAP = "MAP", TAG_LIST = "LIST";
    private static final String STATE_RESOLVING_ERROR = "resolving_error";
    private static final String DIALOG_ERROR = "dialog_error";
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Map<Marker, Venue> markerVenueMap;
    private boolean isMapMode;

    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError;
    private boolean isListResultEnabled;
    private EventBus bus;
    private ProgressDialog mSearchProgressDialog;


    @ViewById
    Toolbar main_toolbar;
    @ViewById
    TextView tvSearch;

    @OptionsMenuItem
    MenuItem action_map_list;

    MapFragment mMapFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResolvingError = savedInstanceState != null && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bus.registerSticky(this);
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        bus.unregister(this); // unregister EventBus
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.isMapMode) {
            switchToMap();
        } else {
            switchToList();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (isListResultEnabled) {
            action_map_list.setEnabled(true);
            action_map_list.getIcon().setAlpha(255);
        } else {
            // disabled
            action_map_list.setEnabled(false);
            action_map_list.getIcon().setAlpha(130);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @OptionsItem
    void action_map_listSelected() {
        isMapMode = !isMapMode;
        if (isMapMode) {
            switchToMap();
            //This has to be opposite as the current view
            action_map_list.setIcon(R.drawable.ic_list_white_24dp);
        } else {
            switchToList();
            //This has to be opposite as the current view
            action_map_list.setIcon(R.drawable.ic_map_white_24dp);
        }
    }

    @AfterViews
    protected void init() {
        initializeVariables();
        setupUI();
        setUpFragments();
        buildGoogleApiClient();
    }

    private void initializeVariables() {
        bus = EventBus.getDefault();
        isMapMode = true;
    }

    private void setupUI() {
        setSupportActionBar(main_toolbar);
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSearchActivity();
            }
        });
    }

    private void goToSearchActivity() {
        SearchActivity_.intent(ResultsActivity.this).start();
        finish();
    }

    private void setUpFragments() {
        if (mMapFragment == null)
            mMapFragment = MapFragment.newInstance();
        mMapFragment.getMapAsync(this);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, mMapFragment, TAG_MAP);
        fragmentTransaction.add(R.id.fragment_container, new ResultListFragment_(), TAG_LIST);
        fragmentTransaction.commit();
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private void displayResult() {
        // if we have results, enable the icon
        isListResultEnabled = true;
        invalidateOptionsMenu();
    }

    private void switchToMap() {
        MapFragment fragMap = (MapFragment) getFragmentManager().findFragmentByTag(TAG_MAP);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.detach(getFragmentManager().findFragmentByTag(TAG_LIST));
        fragmentTransaction.attach(fragMap);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
        getSupportFragmentManager().executePendingTransactions();
    }

    private void switchToList() {
        ResultListFragment fragList = (ResultListFragment_) getFragmentManager().findFragmentByTag(TAG_LIST);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.detach(getFragmentManager().findFragmentByTag(TAG_MAP));
        fragmentTransaction.attach(fragList);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
        getSupportFragmentManager().executePendingTransactions();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        mMap.setPadding(0, 100, 0, 0);
        initializeUiSettings();
        initializeMapLocationSettings();
        initializeMapTraffic();
        initializeMapType();
        initializeMapViewSettings();
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                goToVenueActivity(marker);

            }
        });
        getEvent();
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Connected to Google Play services!
        Timber.i("Google connected!");
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
        Timber.i("Connection to Google is disconnected!");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // This callback is important for handling errors that
        // may occur while attempting to connect with Google.

        Timber.i("Connection to Google failed...");
        if (!mResolvingError) {
            if (connectionResult.hasResolution()) {
                try {
                    mResolvingError = true;
                    connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
                } catch (IntentSender.SendIntentException e) {
                    // There was an error with the resolution intent. Try again.
                    mGoogleApiClient.connect();
                }
            } else {
                // Show dialog using GooglePlayServicesUtil.getErrorDialog()
                showErrorDialog(connectionResult.getErrorCode());
                mResolvingError = true;
            }
        }
    }

    private void getEvent() {
        ResultEvent resultEvent = bus.getStickyEvent(ResultEvent.class);
        if (resultEvent != null) {
            tvSearch.setText(resultEvent.getSearchTerm());
            List<Venue> venues = resultEvent.getResults();
            double latitude = 1.3667;
            double longitude = 103.8;
            boolean firstMarker = true;
            for (Venue venue : venues) {
                addMarker(venue);
                if (firstMarker) {
                    latitude = venue.getFoursquareLocation().getLat();
                    longitude = venue.getFoursquareLocation().getLng();
                    firstMarker = false;
                }
            }
            zoomToMarkers(latitude, longitude);
            displayResult();
        }
    }

    // Creates a dialog for an error message
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getFragmentManager(), "errordialog");
    }

    // Called from ErrorDialogFragment when the dialog is dismissed.
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    //  A fragment to display an error dialog
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode, this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((ResultsActivity_) getActivity()).onDialogDismissed();
        }
    }

    // This method will be called when a ResultEvent is posted
    public void onEvent(ResultEvent resultEvent) {
        if (resultEvent != null) {
            tvSearch.setText(resultEvent.getSearchTerm());
            displayResult();
        }
    }

    public void initializeUiSettings() {
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    public void initializeMapLocationSettings() {
        mMap.setMyLocationEnabled(true);
    }

    public void initializeMapTraffic() {
        mMap.setTrafficEnabled(true);
    }

    public void initializeMapType() {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }


    public void initializeMapViewSettings() {
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(false);
    }

    //this is method to help us set up a Hashmap that stores the Markers we want to plot on the map
    public void setUpMarkersHashMap() {
        if (markerVenueMap == null) {
            markerVenueMap = new HashMap<>();
        }
    }

    //this is method to help us add a Marker to the map
    public void addMarker(Venue venue) {
        double latitude = venue.getFoursquareLocation().getLat();
        double longitude = venue.getFoursquareLocation().getLng();
        String name = venue.getName();
        MarkerOptions markerOption = new MarkerOptions().position(
                new LatLng(latitude, longitude)).icon(BitmapDescriptorFactory.defaultMarker()).title(name);

        Marker marker = mMap.addMarker(markerOption);
        addMarkerToHashMap(marker, venue);
    }

    //this is method to help us add a Marker into the hashmap that stores the Markers
    public void addMarkerToHashMap(Marker marker, Venue venue) {
        setUpMarkersHashMap();
        markerVenueMap.put(marker, venue);
    }

    public void zoomToMarkers(double latitude, double longitude) {
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(12).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void goToVenueActivity(Marker marker) {
        if (markerVenueMap != null && markerVenueMap.containsKey(marker)) {
            showProgressDialog();
            getFoursquareVenue(markerVenueMap.get(marker).getId());
        }
    }

    @UiThread
    public void showProgressDialog() {
        // Setup the progress dialog that will be displayed
        if (mSearchProgressDialog == null)
            mSearchProgressDialog = new ProgressDialog(this);
        mSearchProgressDialog.setTitle("Loading");
        mSearchProgressDialog.setMessage("Searching Foursquare...");
        mSearchProgressDialog.setCancelable(false);
        mSearchProgressDialog.show();
    }

    @Background
    public void getFoursquareVenue(String venueId) {
        FoursquareService.Implementation.get().venueDetail(venueId, new Callback<FoursquareResponse>() {
            @Override
            public void success(FoursquareResponse foursquareResponse, Response response) {
                mSearchProgressDialog.dismiss();
                FoursquareVenueDetail venueDetail = foursquareResponse.getResponse().getFoursquareVenueDetail();
                VenueDetailsEvent venueDetailsEvent = new VenueDetailsEvent();
                venueDetailsEvent.setVenue(venueDetail);
                bus.postSticky(venueDetailsEvent);
                VenueActivity_.intent(ResultsActivity.this).start();
            }

            @Override
            public void failure(RetrofitError error) {
                mSearchProgressDialog.dismiss();
                Timber.e("Failed to get venue: '%s'", error);
                try {
                    throw (error.getCause());
                } catch (Throwable e) {
                    Timber.e("Venue request failed: '%s'", e);
                }
            }
        });
    }
}

package com.skypayjm.app.locate.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.realmbrowser.RealmBrowser;
import com.dd.realmbrowser.RealmFilesActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.skypayjm.app.locate.R;
import com.skypayjm.app.locate.model.Category;
import com.skypayjm.app.locate.model.CategoryRelationship;
import com.skypayjm.app.locate.model.Icon;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

import timber.log.Timber;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.menu_main)
public class ResultsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG_MAP = "MAP", TAG_LIST = "LIST";
    private boolean isMapMode;
    private GoogleApiClient mGoogleApiClient;

    // Request code to use when launching the resolution activity and search activity
    private static final int REQUEST_RESOLVE_ERROR = 1001, REQUEST_SEARCH = 1000;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    private boolean isListResultEnabled = false;
    private static final String STATE_RESOLVING_ERROR = "resolving_error";

    @ViewById
    Toolbar main_toolbar;
    @ViewById
    TextView tvSearch;

    @OptionsMenuItem
    MenuItem action_map_list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResolvingError = savedInstanceState != null && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);
    }

    @AfterViews
    protected void init() {
        setSupportActionBar(main_toolbar);
        isMapMode = true;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, new ResultMapFragment_(), TAG_MAP);
        fragmentTransaction.add(R.id.fragment_container, new ResultListFragment_(), TAG_LIST);
        fragmentTransaction.commit();
        buildGoogleApiClient();
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity_.intent(ResultsActivity.this).startForResult(REQUEST_SEARCH);
            }
        });
        RealmBrowser.getInstance().addRealmModel(Category.class, Icon.class, CategoryRelationship.class);
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

    public void onResume() {
        super.onResume();
        if (this.isMapMode) {
            switchToMap();
        } else {
            switchToList();
        }
    }

    private void displayResult() {
//        LatLngBounds mBounds = new LatLngBounds()
//        PendingResult result = Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, searchTerm,
//                mBounds, mAutocompleteFilter);
        // if we have results, enable the icon
        isListResultEnabled = true;
        invalidateOptionsMenu();
    }

    @OptionsItem
    void action_map_listSelected() {
        isMapMode = !isMapMode;
        if (isMapMode) {
            Toast.makeText(ResultsActivity.this, "Now showing Map View with List icon", Toast.LENGTH_SHORT).show();
            switchToMap();
            //This has to be opposite as the current view
            action_map_list.setIcon(R.drawable.ic_list_white_24dp);
        } else {
            Toast.makeText(ResultsActivity.this, "Now showing List View with Map icon", Toast.LENGTH_SHORT).show();
            switchToList();
            //This has to be opposite as the current view
            action_map_list.setIcon(R.drawable.ic_map_white_24dp);
        }
    }

    @OptionsItem
    void action_realm_dbSelected() {
        RealmFilesActivity.start(this);
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
        if (requestCode == REQUEST_SEARCH) {
            if (resultCode == RESULT_OK) {
                // We populate the results and display
                isListResultEnabled = true;
                invalidateOptionsMenu();
                displayResult();
            } else {
                isListResultEnabled = false;
                invalidateOptionsMenu();
            }
        } else if (requestCode == REQUEST_RESOLVE_ERROR) {
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

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    /* A fragment to display an error dialog */
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

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
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

}

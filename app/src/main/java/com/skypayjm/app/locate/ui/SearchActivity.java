package com.skypayjm.app.locate.ui;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.quinny898.library.persistentsearch.SearchBox;
import com.skypayjm.app.locate.R;
import com.skypayjm.app.locate.adapter.BaseRecyclerAdapter;
import com.skypayjm.app.locate.adapter.CategoryRecyclerAdapter;
import com.skypayjm.app.locate.adapter.RecyclerViewListener;
import com.skypayjm.app.locate.adapter.VenueRecyclerAdapter;
import com.skypayjm.app.locate.api.Foursquare.FoursquareResponse;
import com.skypayjm.app.locate.api.Foursquare.FoursquareService;
import com.skypayjm.app.locate.db.Migration;
import com.skypayjm.app.locate.model.Category;
import com.skypayjm.app.locate.model.CategoryRelationship;
import com.skypayjm.app.locate.model.FoursquareLocation;
import com.skypayjm.app.locate.model.ResponseWrapper;
import com.skypayjm.app.locate.model.Venue;
import com.skypayjm.app.locate.network.NetworkStateChanged;
import com.skypayjm.app.locate.util.FallbackLocationTracker;
import com.skypayjm.app.locate.util.Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import ru.noties.simpleprefs.SimplePref;
import timber.log.Timber;

@EActivity(R.layout.activity_search)
public class SearchActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String NEAR = " near ";
    public static final String IN = " in ";
    public static final String AT = " at ";
    public static final String AROUND = " around ";
    public static final int SEARCHQUERY_LENGTH = 3;
    public static final int SPAN_COUNT = 3;
    public static final int SEARCH_STARTPOSITION = 0;
    public static final String FOURSQUARE_INTENT = "browse";
    private CategoryRecyclerAdapter mCategoryRecyclerAdapter;
    private VenueRecyclerAdapter mVenueRecyclerAdapter;
    private List<String> selectedIds = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private List<Venue> venues = new ArrayList<>();
    private List<Venue> searchedResults = new ArrayList<>();
    private SimplePref pref;
    private final String foursquareCategoriesLastUpdated = "4squareLastUpdate";
    private final String categoriesID = "parentCategoriesID";
    private boolean atBaseCategories;
    private boolean isSearchOpened;
    private boolean userSelectLocation;
    private Realm realm;
    private FoursquareLocation searchFoursquareLocation;
    private double userlatitude;
    private double userlongitude;
    private GoogleApiClient mGoogleApiClient;
    private int endPosition;
    protected Location mLastLocation;
    @ViewById
    Toolbar search_toolbar;
    @ViewById
    RecyclerView category_resultList;
    @ViewById
    SearchBox searchbox;
    private boolean autoCompleteModeOn;

    @Override
    public void onPause() {
        Utility.hide_keyboard(this);
        super.onPause();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this); // unregister EventBus
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
    public void onBackPressed() {
        if (atBaseCategories) {
            setResult(RESULT_CANCELED);
            realm.close();
            finish();
        } else {
            showParentCategories();
        }
    }

    @AfterViews
    void init() {
        setSupportActionBar(search_toolbar);
        initializeSearchbox();
        EventBus.getDefault().register(this); // register EventBus

        atBaseCategories = true;
        category_resultList.setHasFixedSize(true);
        // Define 3 column grid layout
        final GridLayoutManager layout = new GridLayoutManager(SearchActivity.this, SPAN_COUNT);
        category_resultList.setLayoutManager(layout);
        pref = new SimplePref(this, "LocatePref");
        buildGoogleApiClient();
        mCategoryRecyclerAdapter = new CategoryRecyclerAdapter(categories, new RecyclerViewListener<Category>() {
            @Override
            public void onItemClickListener(View view, Category category) {
                if (view.getId() == R.id.ivAddSearch)
                    addToSearch(searchbox.getSearchText().length() - 1, category.getName(), category.getId(), null);
                goToChildCategory(category);
            }
        });
        mVenueRecyclerAdapter = new VenueRecyclerAdapter(venues, new RecyclerViewListener<Venue>() {
            @Override
            public void onItemClickListener(View view, Venue item) {
                userSelectLocation = true;
                if (item.getName().equals("Current Location")) {
                    getCurrentLocation();
                    addToSearch(endPosition, "me", "", null);
                } else {
                    addToSearch(endPosition, item.getName(), "", item.getFoursquareLocation());
                }
            }
        });
        RealmConfiguration config1 = new RealmConfiguration.Builder(this)
                .schemaVersion(1)
                .migration(new Migration())
                .build();

        realm = Realm.getInstance(config1);
        category_resultList.setAdapter(mCategoryRecyclerAdapter);
        // Only update once a week
        if (hasNotUpdated(-7))
            getFoursquareCategories();
        else loadBaseCategories();
    }

    private void getCurrentLocation() {
        if (mLastLocation == null) {
            // ask if user wants to enable gps location
            FallbackLocationTracker gps = new FallbackLocationTracker(this);

            // Check if GPS enabled
            if (gps.hasLocation()) {
                userlatitude = gps.getLocation().getLatitude();
                userlongitude = gps.getLocation().getLongitude();
            } else {
                // Can't get location.
                // GPS or network is not enabled.
                // Ask user to enable GPS/network in settings.
                Utility.showSettingsAlert(this);
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void addToSearch(int endPosition, String item_name, String categoryId, FoursquareLocation foursquareLocation) {
        if (!isSearchOpened) searchbox.toggleSearch();
        String search = searchbox.getSearchText();
        Timber.i("inside addToSearch: " + search);
        if (search.length() != 0 && endPosition > 0) {
            search = search.substring(SEARCH_STARTPOSITION, endPosition).trim() + " ";
            Timber.i("Search now: " + search);
        }
        searchbox.setSearchString(search.concat(item_name).concat(" "));
        if (!categoryId.equals(""))
            selectedIds.add(categoryId);
        if (foursquareLocation != null) searchFoursquareLocation = foursquareLocation;
        Timber.i("inside addToSearch2: " + searchbox.getSearchText());

        userSelectLocation = false;
    }

    private boolean hasNotUpdated(int nDays) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, nDays);
        Date nDaysAgo = cal.getTime();
        return pref.get(foursquareCategoriesLastUpdated, 0L) < nDaysAgo.getTime();
    }

    private long getCurrentDateTime() {
        return System.currentTimeMillis();
    }

    @Background
    public void getFoursquareCategories() {
        FoursquareService.Implementation.get().venuesCategories(new Callback<FoursquareResponse>() {
            @Override
            public void success(FoursquareResponse foursquareResponse, Response response) {
                List<Category> categories = foursquareResponse.getResponse().getCategories();
                onCategoriesReceived(categories);
                Timber.i("Loaded categories");
                updateSharedPref(getCurrentDateTime());
                loadBaseCategories();
            }

            @Override
            public void failure(RetrofitError error) {
//                if (isDestroyed()) return;
                Timber.e("Failed to load categories: '%s'", error);
                try {
                    throw (error.getCause());
                } catch (Throwable e) {
                    Timber.e("Venue request failed: '%s'", e);
                }
            }
        });
    }

    private void onCategoriesReceived(final List<Category> categories) {
        final StringBuilder stringBuilder = new StringBuilder();

        // Copy the object to Realm. Any further changes must happen on realmUser
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (Category category : categories) {
                    stringBuilder.append(category.getId()).append(" ");
                    Category realmCategory = realm.copyToRealmOrUpdate(category);
                    storeIntoCategoryRelationshipDFS(realmCategory);
                }
            }
        });
        pref.set(categoriesID, stringBuilder.toString());
    }

    // Using Depth-First Search as BFS wouldn't have the parent attribute for us to use. And category's depth is pretty shallow so its ok to use DFS
    private void storeIntoCategoryRelationshipDFS(Category realmCategory) {
        if (realmCategory.getCategories().isEmpty())
            return;
        for (Category child : realmCategory.getCategories()) {
            storeIntoCategoryRelationshipDFS(child);
            final CategoryRelationship categoryRelationship = new CategoryRelationship();
            categoryRelationship.setId(child.getId());
            categoryRelationship.setChildCategory(child);
            categoryRelationship.setParentID(realmCategory.getId());
            categoryRelationship.setParentCategory(realmCategory);
            realm.copyToRealmOrUpdate(categoryRelationship);
        }
    }

    public void loadBaseCategories() {
        String parentIDstring = pref.get(categoriesID, "");
        if (parentIDstring.equals("")) {
            getFoursquareCategories();
            parentIDstring = pref.get(categoriesID, "");
        }
        String[] parentIDs = parentIDstring.split("\\s+");
        if (parentIDs.length > 0) {
            RealmQuery<Category> query = realm.where(Category.class);
            query = query.equalTo(Category.FIELD_ID, parentIDs[0]);
            for (int i = 1; i < parentIDs.length; i++) {
                query = query.or();
                query = query.equalTo(Category.FIELD_ID, parentIDs[i]);
            }
            RealmResults<Category> result = query.findAllSorted(Category.FIELD_NAME);
            categories = new ArrayList<>(result);
            display(mCategoryRecyclerAdapter, categories);
        }

    }

    public void display(BaseRecyclerAdapter mRecyclerAdapter, List items) {
        if (items != null && !items.isEmpty()) {
            mRecyclerAdapter.updateItems(items);
            category_resultList.setAdapter(mRecyclerAdapter);
            mRecyclerAdapter.notifyDataSetChanged();
        }
    }

    private void updateSharedPref(long timeUpdated) {
        pref.set(foursquareCategoriesLastUpdated, timeUpdated);
    }

    public void goToChildCategory(Category category) {
        List<Category> subcategories = category.getCategories();
        if (!subcategories.isEmpty()) {
            atBaseCategories = false;
            categories = subcategories;
            display(mCategoryRecyclerAdapter, categories);
        }
    }

    private void initializeSearchbox() {
        search_toolbar.setTitle("");
        searchbox.enableVoiceRecognition(this);
        searchbox.setMenuVisibility(View.INVISIBLE);
        searchbox.setLogoText(getResources().getString(R.string.abc_search_hint));
        searchbox.setLogoTextColor(Color.parseColor("#b3000000"));
        setSearchDrawerLogo(R.drawable.ic_search_black_24dp);
        searchbox.setSearchListener(new SearchBox.SearchListener() {
            @Override
            public void onSearchOpened() {
                searchbox.setMenuVisibility(View.VISIBLE);
                isSearchOpened = true;
            }

            @Override
            public void onSearchCleared() {

            }

            @Override
            public void onSearchClosed() {
                searchbox.setMenuVisibility(View.INVISIBLE);
                isSearchOpened = false;
            }

            @Override
            public void onSearchTermChanged() {
                // We will show suggestions by grabbing the last word and see if it matches the list of words.
                // If yes, we show the suggestions relating to that word
                if (!autoCompleteModeOn) {
                    String s = searchbox.getSearchText();
                    if (s.length() > 0 && s.substring(s.length() - 1).equals(" ")) {
                        String[] strArray = s.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
                        if (strArray.length > 0) {
                            Timber.i("Last word is '%s'", strArray[strArray.length - 1]);
                            //if last word is location keywords e.g. in/near/around/at
                            // autocomplete mode on and we send the words after that to foursquare api.
                            if (strArray[strArray.length - 1].equalsIgnoreCase("near") ||
                                    strArray[strArray.length - 1].equalsIgnoreCase("in") ||
                                    strArray[strArray.length - 1].equalsIgnoreCase("at") ||
                                    strArray[strArray.length - 1].equalsIgnoreCase("around")) {
                                autoCompleteModeOn = true;
                            }
                        }
                    }
                } else {
                    if (!userSelectLocation) {
                        searchFoursquareLocation = null;
                    }
                    String s = searchbox.getSearchText();
                    Timber.i("Searched string now is: " + s);
                    int minPos = getMinimumPosition(s, true);
                    // we get the position to start the autocomplete by getting the position of the location keywords
                    // if we don't find any of the keywords, we switch autocompletemode off.
                    if (minPos == Integer.MAX_VALUE) {
                        autoCompleteModeOn = false;
                        Timber.i("Displaying categories");
                        display(mCategoryRecyclerAdapter, categories);
                    } else {
                        // update the adapter to display autocomplete results
                        // first get user lastknownlocation and we search from there for autocomplete suggestions
                        if (userlatitude != 0.0 && userlongitude != 0.0) {
                            endPosition = minPos;
                            if (isMinCharLong(minPos, SEARCHQUERY_LENGTH)) {
                                getFoursquareSuggestLocations(s.substring(minPos));
                            } else {
                                venues = new ArrayList<>();
                                Venue currentLocation = Utility.createCurrentVenue(userlatitude, userlongitude);
                                venues.add(currentLocation);
                                display(mVenueRecyclerAdapter, venues);
                            }
                        }
                    }
                }
            }

            @Override
            public void onSearch(String searchTerm) {
                Toast.makeText(SearchActivity.this, searchTerm + " Searched", Toast.LENGTH_LONG).show();
                searchFoursquare(searchTerm);
            }
        });
    }

    /**
     * This is a method which takes in a query and returns either the last position right before the
     * conjunction or right after the conjunction.
     *
     * @param query     This is the string which consists of 3 parts i.e. category, conjunction, location
     * @param including If including is true, the position returned will be right after the conjunction
     * @return The ending position of the string which can be used to further process the query
     */
    private int getMinimumPosition(String query, boolean including) {
        Timber.i("Query is: " + query);
        int nearPos = (!query.contains(NEAR)) ? Integer.MAX_VALUE : (including ? query.indexOf(NEAR) + NEAR.length() : query.indexOf(NEAR));
        int inPos = (!query.contains(IN)) ? Integer.MAX_VALUE : (including ? query.indexOf(IN) + IN.length() : query.indexOf(IN));
        int atPos = (!query.contains(AT)) ? Integer.MAX_VALUE : (including ? query.indexOf(AT) + AT.length() : query.indexOf(AT));
        int aroundPos = (!query.contains(AROUND)) ? Integer.MAX_VALUE : (including ? query.indexOf(AROUND) + AROUND.length() : query.indexOf(AROUND));
        return Math.min(nearPos, Math.min(inPos, Math.min(atPos, aroundPos)));
    }

    private boolean isMinCharLong(int start, int searchqueryLength) {
        return searchbox.getSearchText().length() - start >= searchqueryLength;
    }

    @Background
    public void getFoursquareSuggestLocations(String query) {
        String location = userlatitude + "," + userlongitude;
        FoursquareService.Implementation.get().suggestCompletion(location, query, new Callback<FoursquareResponse>() {
            @Override
            public void success(FoursquareResponse foursquareResponse, Response response) {
                venues = new ArrayList<>();
                Venue currentLocation = Utility.createCurrentVenue(userlatitude, userlongitude);
                venues.add(currentLocation);
                if (foursquareResponse != null) {
                    ResponseWrapper responseWrapper = foursquareResponse.getResponse();
                    if (responseWrapper != null && responseWrapper.getSuggestedVenues() != null)
                        venues.addAll(responseWrapper.getSuggestedVenues());
                }
                Timber.i("Got foursquare response: ");
                for (Venue venue : venues) {
                    Timber.i("\n" + venue.getName());
                    Timber.i("Coordinates: " + venue.getFoursquareLocation().getLat() + "," + venue.getFoursquareLocation().getLng());
                }
                display(mVenueRecyclerAdapter, venues);
            }

            @Override
            public void failure(RetrofitError error) {
//                if (isDestroyed()) return;
                Timber.e("Failed to load suggested locations: '%s'", error);
                try {
                    throw (error.getCause());
                } catch (Throwable e) {
                    Timber.e("Venue request failed: '%s'", e);
                }
            }
        });
    }

    @Background
    public void searchFoursquare(final String searchTerm) {

        String location = userlatitude + "," + userlongitude;
        int radius = 5000;
        String query = "";
        String categoryIds = "";

        /*
            Several use cases on searching:
            1.	User selected a suggestion
                    *have to check if user deletes it after selecting (use searchFoursquareLocation which switch back to null when search term changes)
            2.	User never select from suggestion
                2.1	User input a location and there's suggest completion result, we select the first result
                2.2 User input a location but there's no result, we use user's current location and make it search a wider radius
                2.3 User did not input a location, same as use case 2.
        */
        // This means user selected a suggestion and never deletes it
        if (searchFoursquareLocation != null) {
            location = searchFoursquareLocation.getLat() + "," + searchFoursquareLocation.getLng();
        } else if (!venues.isEmpty() && venues.size() > 1) {
            double lat = venues.get(1).getFoursquareLocation().getLat();
            double lng = venues.get(1).getFoursquareLocation().getLng();
            location = lat + "," + lng;
            Timber.i("Venues is not empty." + location);
        } else {
            radius = 20000;
        }
        // If user does not select any category, we will search from the input string directly
        if (selectedIds == null || selectedIds.isEmpty()) {
            query = searchTerm.substring(0, getMinimumPosition(query, false));
        } else {
            categoryIds = selectedIds.get(0);
            for (int index = 1; index < selectedIds.size(); index++) {
                categoryIds = categoryIds + "," + selectedIds.get(index);
            }
        }
        Timber.i("Yishun MRT: " + location);
        FoursquareService.Implementation.get().search(location, radius, query, categoryIds, FOURSQUARE_INTENT, new Callback<FoursquareResponse>() {
            @Override
            public void success(FoursquareResponse foursquareResponse, Response response) {
                if (foursquareResponse != null) {
                    ResponseWrapper responseWrapper = foursquareResponse.getResponse();
                    if (responseWrapper != null && responseWrapper.getSearchedVenues() != null)
                        searchedResults.addAll(responseWrapper.getSearchedVenues());
                }
                Timber.i("Got foursquare response: ");
                for (Venue venue : searchedResults) {
                    Timber.i("\n" + venue.getName());
                }
                goToResults(searchTerm);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void goToResults(String searchTerm) {
        //if search successful, setresult ok with bundle data and finish this activity

//        setResult(RESULT_OK);
        // Pass the searchedResults over as well
        ResultsActivity_.intent(SearchActivity.this).extra("searchterm", searchTerm).start();
        Utility.hide_keyboard(SearchActivity.this);
        realm.close();
        finish();
    }

    private void setSearchDrawerLogo(int drawerLogo) {
        searchbox.setDrawerLogo(ContextCompat.getDrawable(this, drawerLogo));
    }

    private void showParentCategories() {
        RealmQuery query = realm.where(CategoryRelationship.class);
        if (categories != null && !categories.isEmpty())
            query = query.equalTo(CategoryRelationship.ID, categories.get(0).getId());
        CategoryRelationship tempresult = (CategoryRelationship) query.findFirst();
        if (tempresult == null) {
            atBaseCategories = true; // table will only consist of subcategories
        } else {
            atBaseCategories = false;
            query = realm.where(CategoryRelationship.class);
            CategoryRelationship parentResult = (CategoryRelationship) query.equalTo(CategoryRelationship.ID, tempresult.getParentID()).findFirst();
            if (parentResult == null) {
                // means our parent is the base
                loadBaseCategories();
            } else {
                // Now to find all those with the same parent
                RealmResults<CategoryRelationship> results
                        = realm.where(CategoryRelationship.class)
                        .equalTo(CategoryRelationship.PARENTID, parentResult.getParentID())
                        .findAll();
                List<Category> parentCategories = new ArrayList<>();
                for (CategoryRelationship categoryRelationship : results) {
                    parentCategories.add(categoryRelationship.getChildCategory());
                }
                categories = parentCategories;
                display(mCategoryRecyclerAdapter, categories);

            }
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            userlatitude = mLastLocation.getLatitude();
            userlongitude = mLastLocation.getLongitude();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Timber.i("Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Timber.i("Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Subscribe
    // method that will be called when someone posts an event NetworkStateChanged
    public void onEventMainThread(NetworkStateChanged event) {
        if (!event.isInternetConnected()) {
            Toast.makeText(this, "No Internet connection!", Toast.LENGTH_SHORT).show();
            Timber.i("No Internet connection");
        }
    }
}

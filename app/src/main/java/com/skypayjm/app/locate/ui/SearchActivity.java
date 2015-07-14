package com.skypayjm.app.locate.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.speech.RecognizerIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.quinny898.library.persistentsearch.SearchBox;
import com.skypayjm.app.locate.R;
import com.skypayjm.app.locate.adapter.CategoryRecyclerAdapter;
import com.skypayjm.app.locate.api.FoursquareException;
import com.skypayjm.app.locate.api.FoursquareResponse;
import com.skypayjm.app.locate.api.FoursquareService;
import com.skypayjm.app.locate.model.Category;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import io.realm.Realm;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import ru.noties.simpleprefs.SimplePref;
import timber.log.Timber;

@EActivity(R.layout.activity_search)
public class SearchActivity extends AppCompatActivity {

    private CategoryRecyclerAdapter mRecyclerAdapter;
    private List<Category> categories = new ArrayList<>();
    private SimplePref pref;
    private final String foursquareCategoriesLastUpdated = "4squareLastUpdate";
    @ViewById
    Toolbar search_toolbar;
    @ViewById
    RecyclerView category_resultList;
    @ViewById
    SearchBox searchbox;

    @AfterViews
    void init() {
        setSupportActionBar(search_toolbar);
        initializeSearchbox();
        category_resultList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        category_resultList.setLayoutManager(llm);
        pref = new SimplePref(this, "LocatePref");
        // Only update once a week
        if (hasNotUpdated(-7))
            getFoursquareCategories();
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
        mRecyclerAdapter = new CategoryRecyclerAdapter(categories, new CategoryRecyclerAdapter.RecyclerViewListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                showWhichItemClicked(position);
            }
        });
        category_resultList.setAdapter(mRecyclerAdapter);
        FoursquareService.Implementation.get().venuesCategories(new Callback<FoursquareResponse>() {
            @Override
            public void success(FoursquareResponse foursquareResponse, Response response) {
                List<Category> categories = foursquareResponse.getResponse().getCategories();
                onCategoriesReceived(categories);
                Timber.i("Loaded categories");
                updateSharedPref(getCurrentDateTime());
            }

            @Override
            public void failure(RetrofitError error) {
//                if (isDestroyed()) return;
                Timber.e("Failed to load categories: '%s'", error);
                String message = "Loading failed :(";
                try {
                    throw (error.getCause());
                } catch (FoursquareException e) {
                    Timber.e("Venue request failed: '%s'", e);
                } catch (Throwable e) {
                    Timber.e("Venue request failed: '%s'", e);
                }
            }
        });
    }

    private void updateSharedPref(long timeUpdated) {
        pref.set(foursquareCategoriesLastUpdated, timeUpdated);
    }

    @UiThread
    public void showWhichItemClicked(int position) {
        Toast.makeText(SearchActivity.this, "Item clicked: " + categories.get(position).getName(), Toast.LENGTH_LONG).show();
        StringBuilder sb = new StringBuilder();
        sb.append("Item subcategories: ");
        List<Category> subcategories = categories.get(position).getCategories();
        if (subcategories != null) {
            for (int i = 0; i < subcategories.size(); i++) {
                sb.append("\n");
                sb.append(subcategories.get(i).getName());
            }
            Toast.makeText(SearchActivity.this, sb.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void onCategoriesReceived(final List<Category> categories) {
        mRecyclerAdapter.addItemsToList(categories);
        mRecyclerAdapter.notifyDataSetChanged();

        // Obtain a Realm instance
        Realm realm = Realm.getInstance(this);
        // Copy the object to Realm. Any further changes must happen on realmUser
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (Category category : categories) {
                    Category realmCategory = realm.copyToRealmOrUpdate(category);
                }
            }
        });
    }

    private void initializeSearchbox() {
        search_toolbar.setTitle("");
        searchbox.enableVoiceRecognition(this);
        searchbox.toggleSearch();
        searchbox.setLogoText(getResources().getString(R.string.abc_search_hint));
        searchbox.setLogoTextColor(Color.parseColor("#b3000000"));
        setSearchDrawerLogo(R.drawable.ic_search_black_24dp);
        searchbox.getMaterialMenu().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null)
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                setResult(RESULT_CANCELED);
                finish();
            }
        });
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
                    String[] strArray = s.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
                    if (strArray.length > 0)
                        Timber.i("Last word is '%s'", strArray[strArray.length - 1]);
                }
            }

            @Override
            public void onSearch(String searchTerm) {
                Toast.makeText(SearchActivity.this, searchTerm + " Searched", Toast.LENGTH_LONG).show();
                searchGooglePlaces(searchTerm);
            }
        });
    }

    private void searchGooglePlaces(String searchTerm) {

        //if search successful, setresult ok with bundle data and finish this activity

//        setResult(RESULT_OK);
//        finish();
    }

    private void setSearchDrawerLogo(int drawerLogo) {
        searchbox.setDrawerLogo(ContextCompat.getDrawable(this, drawerLogo));
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
        setResult(RESULT_CANCELED);
        finish();
    }
}

package com.skypayjm.app.locate.ui;

import android.content.Intent;
import android.graphics.Color;
import android.speech.RecognizerIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.quinny898.library.persistentsearch.SearchBox;
import com.skypayjm.app.locate.R;
import com.skypayjm.app.locate.adapter.CategoryRecyclerAdapter;
import com.skypayjm.app.locate.api.FoursquareException;
import com.skypayjm.app.locate.api.FoursquareResponse;
import com.skypayjm.app.locate.api.FoursquareService;
import com.skypayjm.app.locate.db.Migration;
import com.skypayjm.app.locate.model.Category;
import com.skypayjm.app.locate.model.CategoryRelationship;
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
public class SearchActivity extends AppCompatActivity {

    private CategoryRecyclerAdapter mRecyclerAdapter;
    private List<Category> categories = new ArrayList<>();
    private SimplePref pref;
    private final String foursquareCategoriesLastUpdated = "4squareLastUpdate";
    private final String categoriesID = "parentCategoriesID";
    private boolean atBaseCategories;
    private Realm realm;
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

        atBaseCategories = true;
        category_resultList.setHasFixedSize(true);
        // Define 3 column grid layout
        final GridLayoutManager layout = new GridLayoutManager(SearchActivity.this, 3);
        category_resultList.setLayoutManager(layout);
        pref = new SimplePref(this, "LocatePref");
        mRecyclerAdapter = new CategoryRecyclerAdapter(categories, new CategoryRecyclerAdapter.RecyclerViewListener() {
            @Override
            public void onItemClickListener(View view, Category category) {
                if (view.getId() == R.id.ivAddCategory)
                    addCategoryToSearch(category);
                goToChildCategory(category);
            }
        });
        RealmConfiguration config1 = new RealmConfiguration.Builder(this)
                .schemaVersion(2)
                .migration(new Migration())
                .build();

        realm = Realm.getInstance(config1);
        category_resultList.setAdapter(mRecyclerAdapter);
        // Only update once a week
        if (hasNotUpdated(-7))
            getFoursquareCategories();
        else loadBaseCategories();
    }

    private void addCategoryToSearch(Category category) {

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

    private void onCategoriesReceived(final List<Category> categories) {
        final StringBuilder stringBuilder = new StringBuilder();

        // Copy the object to Realm. Any further changes must happen on realmUser
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (Category category : categories) {
                    stringBuilder.append(category.getId() + " ");
                    Category realmCategory = realm.copyToRealmOrUpdate(category);
                    storeIntoCategoryRelationshipDFS(realmCategory);
                }
            }
        });
//        storeIntoCategoryLevelBFS(categories);
        pref.set(categoriesID, stringBuilder.toString());
    }

    // Using Depth-First Search as BFS wouldn't have the parent attribute for us to use. And category's depth is pretty shallow so its ok to use DFS
    private void storeIntoCategoryRelationshipDFS(Category realmCategory) {
        if (realmCategory.getCategories() == null || realmCategory.getCategories().isEmpty())
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

    // Method to put each category into the categorylevel table, using breadth-first search
//    private void storeIntoCategoryLevelBFS(final List<Category> categories) {
//        realm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                int level = 0;
//                Queue<Category> currentLevel = new LinkedList<>(categories);
//                Queue<Category> nextLevel = new LinkedList<>();
//                while (!currentLevel.isEmpty()) {
//                    Category c = currentLevel.remove();
//                    CategoryLevel categoryLevel = new CategoryLevel();
//                    categoryLevel.setId(c.getId());
//                    categoryLevel.setCategory(c);
//                    categoryLevel.setLevel(level);
//                    realm.copyToRealmOrUpdate(categoryLevel);
//                    // put the children of this category into the nextLevel queue
//                    if (c.getCategories() != null)
//                        nextLevel.addAll(c.getCategories());
//                    if (currentLevel.isEmpty()) {
//                        currentLevel = nextLevel;
//                        nextLevel = new LinkedList<>();
//                        level++;
//                    }
//                }
//            }
//        });
//    }

    public void loadBaseCategories() {
        String parentIDstring = pref.get(categoriesID, "");
        if (parentIDstring.equals("")) {
            getFoursquareCategories();
            parentIDstring = pref.get(categoriesID, "");
        }
        String[] parentIDs = parentIDstring.split("\\s+");
        if (parentIDs.length > 0) {
            RealmQuery query = realm.where(Category.class);
            query = query.equalTo(Category.FIELD_ID, parentIDs[0]);
            for (int i = 1; i < parentIDs.length; i++) {
                query = query.or();
                query = query.equalTo(Category.FIELD_ID, parentIDs[i]);
            }
            RealmResults<Category> result = query.findAllSorted(Category.FIELD_NAME);
            List<Category> tempCategories = new ArrayList<>(result);
            categories = tempCategories;
            displayCategories();
        }

    }

    public void displayCategories() {
        mRecyclerAdapter.updateItems(categories);
        mRecyclerAdapter.notifyDataSetChanged();
    }

    private void updateSharedPref(long timeUpdated) {
        pref.set(foursquareCategoriesLastUpdated, timeUpdated);
    }

    public void goToChildCategory(Category category) {
//        Toast.makeText(SearchActivity.this, "Item clicked: " + category.getName(), Toast.LENGTH_LONG).show();
//        StringBuilder sb = new StringBuilder();
//        sb.append("Item subcategories: ");
        List<Category> subcategories = category.getCategories();
        if (!subcategories.isEmpty()) {
            atBaseCategories = false;

//            for (int i = 0; i < subcategories.size(); i++) {
//                sb.append("\n");
//                sb.append(subcategories.get(i).getName());
//            }
//            Toast.makeText(SearchActivity.this, sb.toString(), Toast.LENGTH_SHORT).show();
            categories = subcategories;
            displayCategories();
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
        ResultsActivity_.intent(this).extra("searchterm", searchTerm).start();
        Utility.hide_keyboard(this);
        realm.close();
        finish();
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
        if (atBaseCategories) {
            setResult(RESULT_CANCELED);
            realm.close();
            finish();
        } else {
            showParentCategories();
        }
    }

    private void showParentCategories() {
//        RealmQuery query = realm.where(CategoryLevel.class);
//        if (categories.size() > 0)
//            query = query.equalTo(CategoryLevel.ID, categories.get(0).getId());
//        final CategoryLevel result = (CategoryLevel) query.findFirst();
//        if (result != null) {
//            if (result.getLevel() == 0)
//                atBaseCategories = true; // level 0 is the base categories
//            else {
//                atBaseCategories = false;
//                final int parentLevel = result.getLevel() - 1;
//                realm.executeTransaction(new Realm.Transaction() {
//                    @Override
//                    public void execute(Realm realm) {
//
//                        RealmResults<CategoryLevel> tempresults = realm.where(CategoryLevel.class).equalTo(CategoryLevel.LEVEL, parentLevel).findAll();
//                        for (CategoryLevel categoryLevel : tempresults) {
//
//                        }
//
//                        List<Category> parentCategories = new ArrayList<>();
//                        for (CategoryLevel categoryLevel : results) {
//                            parentCategories.add(categoryLevel.getCategory());
//                        }
//                        categories = parentCategories;
//                        displayCategories();
//                    }
//                });
//
//            }
//        }
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
                displayCategories();

            }
        }
    }

    @Override
    public void onPause() {
        Utility.hide_keyboard(this);
        super.onPause();
    }
}

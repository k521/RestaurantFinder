package videodemos.example.restaurantinspector.UI;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import videodemos.example.restaurantinspector.Model.RestaurantManager;
import videodemos.example.restaurantinspector.R;
import videodemos.example.restaurantinspector.UI.Adapters.RestaurantsAdapter;

/**
 * Main Activity displays all the restaurants.
 */

public class ListRestaurantActivity extends AppCompatActivity implements RestaurantsAdapter.OnRestaurantListener{

    private static final String TAG = "ListRestaurantActivity";

    private final String PREFERENCES = "data";
    private final String TAG_TRACKING_NUMBER_LIST = "list of tracking numbers";

    private SharedPreferences preferences;
    private RestaurantManager manager;
    private RestaurantsAdapter restaurantsAdapter;

    public static Intent makeIntent(Context c) {
        Intent intent = new Intent(c, ListRestaurantActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_restaurant);

        setupToolbar();

        manager = RestaurantManager.getInstance();

        setUpRestaurantsRecylerView();

        setupSearchView();

        updateNewInspectionMap();
        checkForNewFavInspections();

        Log.d("Favorites", getFavouriteRestaurantsTrackingNumbers());

        Toast.makeText(this, getFavouriteRestaurantsTrackingNumbers(), Toast.LENGTH_LONG).show();
        //clearFavouriteSharedPreferences();

    }

    private void setupSearchView() {
        SearchView searchView = findViewById(R.id.sv_restaurant_list);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                restaurantsAdapter.filter(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                restaurantsAdapter.filter(newText);
                return true;
            }
        });

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setIconified(false);
            }
        });
    }

    private void checkForNewFavInspections() {
        if (!manager.isFavouritesMapEmpty()){
            //TODO: inflate fragment here
        }
    }

    private void updateNewInspectionMap() {
        manager.removeNonNewRestaurantInspections();
    }

    private void clearFavouriteSharedPreferences(){
        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TAG_TRACKING_NUMBER_LIST, "");
        editor.apply();
    }

    private String getFavouriteRestaurantsTrackingNumbers(){
        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        return preferences.getString(TAG_TRACKING_NUMBER_LIST, "");
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.restaurant_list_toolbar);
        setSupportActionBar(toolbar);

        ImageButton helpButton = findViewById(R.id.ib_restaurant_help_icon);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = InfoScreenActivity.makeLaunchIntent(ListRestaurantActivity.this);
                startActivity(intent);
            }
        });

        ImageView mapButton = findViewById(R.id.iv_go_to_maps);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapsActivity = MapsActivity.makeIntent(ListRestaurantActivity.this);
                startActivity(mapsActivity);
                manager.getRestaurantList().clear();
                finish();
            }
        });
    }

    private void setUpRestaurantsRecylerView() {
        RecyclerView restaurantsRecyclerView = findViewById(R.id.rv_restaurant_list);

        restaurantsRecyclerView.setHasFixedSize(true);
        restaurantsRecyclerView.setItemViewCacheSize(20);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        restaurantsRecyclerView.setLayoutManager(layoutManager);

        restaurantsAdapter = new RestaurantsAdapter(manager.getRestaurantList(), this, this);
        restaurantsRecyclerView.setAdapter(restaurantsAdapter);

    }

    @Override
    public void onRestaurantClick(int position) {
        Intent intent = RestaurantReportActivity.makeIntent(this, position);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        manager.getRestaurantList().clear();
    }

}

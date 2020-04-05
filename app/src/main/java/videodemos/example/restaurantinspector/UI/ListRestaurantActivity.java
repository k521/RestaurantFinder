package videodemos.example.restaurantinspector.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import videodemos.example.restaurantinspector.Model.DataHandling.Restaurant;
import videodemos.example.restaurantinspector.Model.RestaurantManager;
import videodemos.example.restaurantinspector.R;
import videodemos.example.restaurantinspector.UI.Adapters.RestaurantsAdapter;

import android.view.KeyEvent;

import android.widget.ToggleButton;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.util.ArrayList;
import java.util.List;


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
    private RecyclerView.LayoutManager layoutManager;
    private ConstraintLayout filtersLayout;
    private ConstraintLayout backgroundLayout;

    List<Restaurant> restaurantsFullList = new ArrayList<>();

    public static Intent makeIntent(Context c, String searchQuery, boolean isFavouriteFilterOn, String hazardLevelFilter, boolean isGreaterThan, String criticalFilter){
        Intent intent = new Intent(c, ListRestaurantActivity.class);

        intent.putExtra(MapsActivity.TAG_FAVOURITE, isFavouriteFilterOn);
        intent.putExtra(MapsActivity.TAG_QUERY, searchQuery);
        intent.putExtra(MapsActivity.TAG_GREATER_THAN, isGreaterThan);
        intent.putExtra(MapsActivity.TAG_CRITICAL_FILTER, criticalFilter);
        intent.putExtra(MapsActivity.TAG_HAZARD_LEVER, hazardLevelFilter);

        return intent;
    }


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
        restaurantsFullList.addAll(manager.getRestaurantList());


        setUpRestaurantsRecylerView();

        setupSearchView();

        setupSavedFilters();

        setupShowFiltersButton();
        setupCriticalFilter();
        setupFavouriteFilter();







        Log.d("order", "End of onCreate");


        //clearFavouriteSharedPreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpRestaurantsRecylerView();
    }

    private void setupShowFiltersButton() {
        ImageButton showFilters = findViewById(R.id.ib_show_filters);
        filtersLayout = findViewById(R.id.cl_filters);
        backgroundLayout = findViewById(R.id.cl_restaurant_list_main);

        showFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filtersLayout.getVisibility() == View.INVISIBLE){
                    filtersLayout.setVisibility(View.VISIBLE);
                    showFilters.setImageResource(R.drawable.ic_expand_less_black_24dp);

                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(backgroundLayout);
                    constraintSet.connect(R.id.v_separator, ConstraintSet.TOP, R.id.cl_filters, ConstraintSet.BOTTOM);
                    constraintSet.applyTo(backgroundLayout);

                } else {
                    filtersLayout.setVisibility(View.INVISIBLE);
                    showFilters.setImageResource(R.drawable.ic_expand_more_black_24dp);

                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(backgroundLayout);
                    constraintSet.connect(R.id.v_separator, ConstraintSet.TOP, R.id.sv_restaurant_list, ConstraintSet.BOTTOM);
                    constraintSet.applyTo(backgroundLayout);

                }

            }
        });
    }

    private void setupFavouriteFilter() {
        Switch favouritesSwitch = findViewById(R.id.sw_filter_favourites_map);
        favouritesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                restaurantsAdapter.filterByFavourites(isChecked);
            }
        });
    }

    private void setupSavedFilters() {
        Intent intent = getIntent();
        boolean isFavouriteFilterOn = intent.getBooleanExtra(MapsActivity.TAG_FAVOURITE, false);
        String searchQuery = intent.getStringExtra(MapsActivity.TAG_QUERY);
        boolean isGreaterThan = intent.getBooleanExtra(MapsActivity.TAG_GREATER_THAN, false);
        String criticalFilter = intent.getStringExtra(MapsActivity.TAG_CRITICAL_FILTER);
        String hazardLevelFilter = intent.getStringExtra(MapsActivity.TAG_HAZARD_LEVER);



        if (hazardLevelFilter == null){
            return;
        }


        Switch favSwitch = findViewById(R.id.sw_filter_favourites_map);
        favSwitch.setChecked(isFavouriteFilterOn);
        restaurantsAdapter.filterByFavourites(isFavouriteFilterOn);


        ToggleButton tbGreater = findViewById(R.id.tb_greater_or_lesser_map);
        tbGreater.setChecked(isGreaterThan);

        TextView tvCriticalFilter = findViewById(R.id.filterInput_map);
        tvCriticalFilter.setText(criticalFilter);
        restaurantsAdapter.filterByCriticalViolations(criticalFilter, isGreaterThan);


        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        switch (hazardLevelFilter){
            case "Low":
                radioGroup.check(R.id.filterLow_map);
                restaurantsAdapter.filterByHazardLevel("Low");
                break;
            case "Moderate":
                radioGroup.check(R.id.filterModerate_map);
                restaurantsAdapter.filterByHazardLevel("Moderate");
                break;
            case "High":
                radioGroup.check(R.id.filterHigh_map);
                restaurantsAdapter.filterByHazardLevel("High");
                break;
            default:
                radioGroup.check(R.id.filterNone_map);
                restaurantsAdapter.filterByHazardLevel("None");
        }



        SearchView searchView = findViewById(R.id.sv_restaurant_list);
        searchView.setQuery(searchQuery, false);
        searchView.setIconified(false);
        searchView.clearFocus();



    }

    private void setupCriticalFilter() {
        TextView filterText = findViewById(R.id.filterInput_map);

        filterText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE){

                    filterEditText();
                }
                return false;
            }
        });

        ToggleButton toggleCompare = findViewById(R.id.tb_greater_or_lesser_map);
        toggleCompare.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                filterEditText();
            }
        });

    }

    private void filterEditText(){
        TextView filterText = findViewById(R.id.filterInput_map);
        String content = filterText.getText().toString();

        boolean isGreaterThan = true;
        ToggleButton toggleComparison = findViewById(R.id.tb_greater_or_lesser_map);


        if (toggleComparison.isChecked()){
            isGreaterThan = false;
        }

        restaurantsAdapter.filterByCriticalViolations(content, isGreaterThan);
    }

    private void setupSearchView() {
        SearchView searchView = findViewById(R.id.sv_restaurant_list);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                restaurantsAdapter.filterByName(query);
                //Toast.makeText(ListRestaurantActivity.this, query, Toast.LENGTH_LONG).show();
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(ListRestaurantActivity.this, newText, Toast.LENGTH_LONG).show();
                restaurantsAdapter.filterByName(newText);
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
                SearchView searchView = findViewById(R.id.sv_restaurant_list);
                Switch favSwitch = findViewById(R.id.sw_filter_favourites_map);
                ToggleButton tbGreater = findViewById(R.id.tb_greater_or_lesser_map);
                TextView criticalFilter = findViewById(R.id.filterInput_map);
                RadioGroup radioGroup = findViewById(R.id.radioGroup);
                int radioInt = radioGroup.getCheckedRadioButtonId();


                String hazardFilter = "";
                switch(radioInt) {
                    case R.id.filterLow_map:
                        hazardFilter = "Low";
                        break;
                    case R.id.filterModerate_map:
                        hazardFilter = "Moderate";
                        break;
                    case R.id.filterHigh:
                        hazardFilter = "High";
                        break;
                    case R.id.filterNone_map:
                        hazardFilter = "none";
                        break;
                }


                Intent mapsActivity = MapsActivity.makeIntent(ListRestaurantActivity.this,
                        searchView.getQuery().toString(), favSwitch.isChecked(), hazardFilter,
                        tbGreater.isChecked(), criticalFilter.getText().toString());

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
        layoutManager = new LinearLayoutManager(this);
        restaurantsRecyclerView.setLayoutManager(layoutManager);



        restaurantsAdapter = new RestaurantsAdapter(manager.getRestaurantList(), restaurantsFullList, this, this);
        restaurantsRecyclerView.setAdapter(restaurantsAdapter);


    }

    @Override
    public void onRestaurantClick(String trackingNumber) {
        Intent intent = RestaurantReportActivity.makeIntent(this, trackingNumber);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        manager.getRestaurantList().clear();
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.filterLow_map:
                if (checked)
                    restaurantsAdapter.filterByHazardLevel("Low");
                    break;
            case R.id.filterModerate_map:
                if (checked)
                    restaurantsAdapter.filterByHazardLevel("Moderate");
                    break;
            case R.id.filterHigh:
                if(checked)
                    restaurantsAdapter.filterByHazardLevel("High");
                    break;
            case R.id.filterNone_map:
                if (checked){
                    restaurantsAdapter.filterByHazardLevel("None");
                    break;
                }
        }

    }




}

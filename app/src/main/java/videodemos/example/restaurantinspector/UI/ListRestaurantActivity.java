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

import videodemos.example.restaurantinspector.Model.DataHandling.DateCalculations;
import videodemos.example.restaurantinspector.Model.DataHandling.Inspection;
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

public class ListRestaurantActivity extends AppCompatActivity
        implements RestaurantsAdapter.OnRestaurantListener{

    private final String LOW_HAZARD = "Low";
    private final String MODERATE_HAZARD = "Moderate";
    private final String HIGH_HAZARD = "High";
    private final String NONE_HAZARD = "None";

    private RestaurantManager manager;
    private RestaurantsAdapter restaurantsAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ConstraintLayout filtersLayout;
    private ConstraintLayout backgroundLayout;

    private boolean[] restaurantsHazardFilter;
    private boolean[] restaurantsTextFilter;
    private boolean[] restaurantsCriticalFilter;
    private boolean[] restaurantsFavourites;

    List<Restaurant> restaurantDataset = new ArrayList<>();

    public static Intent makeIntent(Context c, String searchQuery, boolean isFavouriteFilterOn,
                                    String hazardLevelFilter, boolean isGreaterThan,
                                    String criticalFilter){

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

        setupArrayFilters();
        setUpRestaurantsRecylerView();
        setupSearchView();
        setupSavedFilters();
        setupShowFiltersButton();
        setupCriticalFilter();
        setupFavouriteFilter();

    }

    private void setupArrayFilters() {
        restaurantsHazardFilter = new boolean[manager.getRestaurantList().size()];
        restaurantsTextFilter = new boolean[manager.getRestaurantList().size()];
        restaurantsCriticalFilter = new boolean[manager.getRestaurantList().size()];
        restaurantsFavourites = new boolean[manager.getRestaurantList().size()];

        setAllValuesToTrue(restaurantsHazardFilter);
        setAllValuesToTrue(restaurantsTextFilter);
        setAllValuesToTrue(restaurantsCriticalFilter);
        setAllValuesToTrue(restaurantsFavourites);
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
                    constraintSet.connect(R.id.v_separator,
                            ConstraintSet.TOP, R.id.cl_filters,
                            ConstraintSet.BOTTOM);
                    constraintSet.applyTo(backgroundLayout);

                } else {
                    filtersLayout.setVisibility(View.INVISIBLE);
                    showFilters.setImageResource(R.drawable.ic_expand_more_black_24dp);

                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(backgroundLayout);
                    constraintSet.connect(R.id.v_separator, ConstraintSet.TOP,
                            R.id.sv_restaurant_list,
                            ConstraintSet.BOTTOM);
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
                filterByFavourites(isChecked);
            }
        });
    }

    private void setupSavedFilters() {
        Intent intent = getIntent();
        boolean isFavouriteFilterOn = intent.getBooleanExtra(MapsActivity.TAG_FAVOURITE,
                false);
        String searchQuery = intent.getStringExtra(MapsActivity.TAG_QUERY);
        boolean isGreaterThan = intent.getBooleanExtra(MapsActivity.TAG_GREATER_THAN,
                false);
        String criticalFilter = intent.getStringExtra(MapsActivity.TAG_CRITICAL_FILTER);
        String hazardLevelFilter = intent.getStringExtra(MapsActivity.TAG_HAZARD_LEVER);


        if (hazardLevelFilter == null){
            return;
        }

        Switch favSwitch = findViewById(R.id.sw_filter_favourites_map);
        favSwitch.setChecked(isFavouriteFilterOn);
        filterByFavourites(isFavouriteFilterOn);


        ToggleButton tbGreater = findViewById(R.id.tb_greater_or_lesser_map);
        tbGreater.setChecked(isGreaterThan);

        TextView tvCriticalFilter = findViewById(R.id.filterInput_map);
        tvCriticalFilter.setText(criticalFilter);
        filterByCriticalViolations(criticalFilter, !isGreaterThan);


        RadioGroup radioGroup = findViewById(R.id.radioGroup);


        switch (hazardLevelFilter){
            case LOW_HAZARD:
                radioGroup.check(R.id.filterLow_map);
                filterByHazardLevel(LOW_HAZARD);
                break;
            case MODERATE_HAZARD:
                radioGroup.check(R.id.filterModerate_map);
                filterByHazardLevel(MODERATE_HAZARD);
                break;
            case HIGH_HAZARD:
                radioGroup.check(R.id.filterHigh);
                filterByHazardLevel(HIGH_HAZARD);
                break;
            default:
                radioGroup.check(R.id.filterNone_map);
                filterByHazardLevel(NONE_HAZARD);
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

        filterByCriticalViolations(content, isGreaterThan);
    }

    private void setupSearchView() {
        SearchView searchView = findViewById(R.id.sv_restaurant_list);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterByName(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterByName(newText);
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
                        hazardFilter = LOW_HAZARD;
                        break;
                    case R.id.filterModerate_map:
                        hazardFilter = MODERATE_HAZARD;
                        break;
                    case R.id.filterHigh:
                        hazardFilter = HIGH_HAZARD;
                        break;
                    case R.id.filterNone_map:
                        hazardFilter = NONE_HAZARD;
                        break;
                }


                Intent mapsActivity = MapsActivity.makeIntent(ListRestaurantActivity.this,
                        searchView.getQuery().toString(), favSwitch.isChecked(), hazardFilter,
                        tbGreater.isChecked(), criticalFilter.getText().toString(), true);

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

        restaurantsAdapter = new RestaurantsAdapter(restaurantDataset,this,
                this);
        restaurantsRecyclerView.setAdapter(restaurantsAdapter);

    }

    //region Filters

    public void filterByName(String text) {

        restaurantsTextFilter = new boolean[manager.getRestaurantList().size()];

        if(text.isEmpty()){
            setAllValuesToTrue(restaurantsTextFilter);
        } else{
            text = text.toLowerCase();
            for (int i = 0; i < manager.getRestaurantList().size(); i++){
                Restaurant r = manager.getRestaurantList().get(i);

                if(r.getName().toLowerCase().contains(text)){
                    restaurantsTextFilter[i] = true;
                }
            }
        }

        filterAll();
    }

    public void filterByFavourites(boolean doFilter){
        restaurantsFavourites = new boolean[manager.getRestaurantList().size()];

        if (!doFilter){
            setAllValuesToTrue(restaurantsFavourites);
            filterAll();
            return;
        }

        for (int i = 0; i < manager.getRestaurantList().size(); i++){
            if (manager.getRestaurantList().get(i).isFavourite()){
                restaurantsFavourites[i] = true;
            }
        }

        filterAll();
    }


    public void filterByHazardLevel(String hazardLevel){

        restaurantsHazardFilter = new boolean[manager.getRestaurantList().size()];

        if (hazardLevel.equals(NONE_HAZARD)){
            setAllValuesToTrue(restaurantsHazardFilter);
            filterAll();
            return;
        }

        for (int i = 0; i < manager.getRestaurantList().size(); i++){
            Restaurant r = manager.getRestaurantList().get(i);

            if(r.getInspections().isEmpty()){
                continue;
            }
            Inspection mostRecentInspection = r.getInspections().get(0);
            if(mostRecentInspection.getHazardRating().equals(hazardLevel)){
                //r.setVisible(false);
                restaurantsHazardFilter[i] = true;
            }
        }

        filterAll();

    }

    public void filterByCriticalViolations(String criticalViolations, boolean isGreaterThan){

        restaurantsCriticalFilter = new boolean[manager.getRestaurantList().size()];

        if (criticalViolations.isEmpty()) {
            setAllValuesToTrue(restaurantsCriticalFilter);
            filterAll();
            return;
        }

        int criticalViolation = Integer.parseInt(criticalViolations);

        for (int j = 0; j < manager.getRestaurantList().size(); j++){
            Restaurant r = manager.getRestaurantList().get(j);
            int numOfCriticalViolationsFound = 0;
            for(Inspection i : r.getInspections()){
                String dateOfInspection = i.getInspectionDate();
                DateCalculations dc = new DateCalculations();
                int numOfDays = dc.daysInBetween(dateOfInspection);
                if(numOfDays <= 365){
                    numOfCriticalViolationsFound += i.getNumCritical();
                }
                else{
                    break;
                }
            }
            if(isGreaterThan && numOfCriticalViolationsFound >= criticalViolation){
                restaurantsCriticalFilter[j] = true;
            } else if (!isGreaterThan && numOfCriticalViolationsFound <= criticalViolation){
                restaurantsCriticalFilter[j] = true;
            }
        }

        filterAll();
    }


    private void setAllValuesToTrue(boolean[] list){
        for (int i = 0; i < list.length; i++){
            list[i] = true;
        }
    }

    public void filterAll(){

        restaurantDataset.clear();
        for (int i = 0; i < manager.getRestaurantList().size(); i++){
            if (restaurantsHazardFilter[i] && restaurantsTextFilter[i]
                    && restaurantsCriticalFilter[i] && restaurantsFavourites[i]){
                restaurantDataset.add(manager.getRestaurantList().get(i));
            }
        }

        restaurantsAdapter.notifyDataSetChanged();
    }
    //endregion Filters

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
                    filterByHazardLevel(LOW_HAZARD);
                    break;
            case R.id.filterModerate_map:
                if (checked)
                    filterByHazardLevel(MODERATE_HAZARD);
                    break;
            case R.id.filterHigh:
                if(checked)
                    filterByHazardLevel(HIGH_HAZARD);
                    break;
            case R.id.filterNone_map:
                if (checked){
                    filterByHazardLevel(NONE_HAZARD);
                    break;
                }
        }

    }




}

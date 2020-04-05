package videodemos.example.restaurantinspector.UI;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;


import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import videodemos.example.restaurantinspector.Model.ClusterMarker;
import videodemos.example.restaurantinspector.Model.DataHandling.DateCalculations;
import videodemos.example.restaurantinspector.Model.DataHandling.Inspection;
import videodemos.example.restaurantinspector.Model.DataHandling.Restaurant;
import videodemos.example.restaurantinspector.Model.Network.HttpHandler;
import videodemos.example.restaurantinspector.Model.RestaurantManager;
import videodemos.example.restaurantinspector.R;
import videodemos.example.restaurantinspector.UI.Dialogs.NewDataFragment;
import videodemos.example.restaurantinspector.UI.Dialogs.NewFavouriteInspectionFragment;
import videodemos.example.restaurantinspector.Utilities.MyClusterManagerRenderer;
import android.view.View.OnKeyListener;
import android.view.View;
import android.view.KeyEvent;
import android.widget.ToggleButton;


/**
 * A class that setups data and shows a map for restaurants.
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        ClusterManager.OnClusterClickListener<ClusterMarker>,
        ClusterManager.OnClusterItemInfoWindowClickListener<ClusterMarker>
{

    public final static String TAG_FAVOURITE = "fav";
    public final static String TAG_QUERY = "query";
    public final static String TAG_GREATER_THAN = "greater than";
    public final static String TAG_CRITICAL_FILTER = "critical filter";
    public final static String TAG_HAZARD_LEVER = "hazard level";

    public static final String TAG_EXTRA_TRACKING_NUMBER = "tracking number";
    public static boolean comeFromInspectionList = false;
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private final String RESTAURANT_URL = "https://data.surrey.ca/api/3/action/package_show?id=restaurants";
    private final String INSPECTION_URL = "https://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";
    private final int HOURS_FOR_UPDATE = 20;
    private final String PREFERENCES = "data";
    private final String TAG_TUTORIAL = "tutorial";
    private final String TAG_UPDATE_DATE = "last_update_date";
    private final String TAG_SERVER_METADATA_DATE = "last_server_date";
    private final String TAG_DEFAULT_DATA = "default_data";
    private final String TAG_DIALOG = "new data dialog tag";

    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    public static final float DEFAULT_ZOOM =15f;

    private GoogleMap map;
    private ClusterManager<ClusterMarker> clusterManager;
    private List<ClusterMarker> clusterMarkers = new ArrayList<>();

    //widgets
    private EditText searchText;
    private ImageView gps;
    //vars
    private Boolean locationPermissionsGranted = false;
    private FusedLocationProviderClient fusedLocationClient;

    private boolean isComingFromGPS = false;

    private SharedPreferences preferences;
    private RestaurantManager manager = RestaurantManager.getInstance();

    private Marker customMarker;
    private String customMarkerTrackingNumber;

    //TODO: Add favourite restaurants to the HashMap before updating the csv


    private boolean[] restaurantsHazardFilter;
    private boolean[] restaurantsTextFilter;
    private boolean[] restaurantsCriticalFilter;
    private boolean[] restaurantsFavourites;


    private ConstraintLayout filtersLayout;


    private List<Restaurant> restaurantDataset = new ArrayList<>();
    private List<Restaurant> restaurantsFullList = new ArrayList<>();



    public static Intent makeGPSIntent(Context c, String trackingNumber){
        Intent intent = new Intent(c,MapsActivity.class);
        intent.putExtra(TAG_EXTRA_TRACKING_NUMBER, trackingNumber);
        return intent;
    }

    public static Intent makeIntent(Context c, String searchQuery, boolean isFavouriteFilterOn, String hazardLevelFilter, boolean isGreaterThan, String criticalFilter){
        Intent intent = new Intent(c, MapsActivity.class);

        intent.putExtra(TAG_FAVOURITE, isFavouriteFilterOn);
        intent.putExtra(TAG_QUERY, searchQuery);
        intent.putExtra(TAG_GREATER_THAN, isGreaterThan);
        intent.putExtra(TAG_CRITICAL_FILTER, criticalFilter);
        intent.putExtra(TAG_HAZARD_LEVER, hazardLevelFilter);

        return intent;
    }

    public static Intent makeIntent(Context c){
        Intent intent = new Intent(c, MapsActivity.class);

        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //restaurantDataset = manager.getRestaurantList();


        checkIfTimeToUpdate();
        setupRestaurantManager();

        restaurantsHazardFilter = new boolean[manager.getRestaurantList().size()];
        restaurantsCriticalFilter = new boolean[manager.getRestaurantList().size()];
        restaurantsTextFilter = new boolean[manager.getRestaurantList().size()];
        restaurantsFavourites = new boolean[manager.getRestaurantList().size()];

        setAllValuesToTrue(restaurantsHazardFilter);
        setAllValuesToTrue(restaurantsTextFilter);
        setAllValuesToTrue(restaurantsCriticalFilter);
        setAllValuesToTrue(restaurantsFavourites);

        restaurantDataset.addAll(manager.getRestaurantList());

        setupSearchView();

        Log.d("Size", "size = " + restaurantDataset.size() + "Original = " + manager.getRestaurantList().size());

        if(!isServicesOK()){
            Intent mainActivity = ListRestaurantActivity.makeIntent(MapsActivity.this);
            startActivity(mainActivity);
            finish();
        }

        Log.d("Got_here", "211");

        setupToolbar();

        setupShowFiltersButton();
        Log.d("Got_here", "216");

        getLocationPermission();

        setupCriticalFilter();
        setupFavouriteFilter();


        //searchText = findViewById(R.id.input_search);
        gps = findViewById(R.id.ic_gps);

        showTutorial();

        Log.d("Got_here", "229");
        updateNewInspectionMap();
        checkForNewFavInspections();
        Log.d("Got_here", "232");

    }

    private void updateNewInspectionMap() {
        manager.removeNonNewRestaurantInspections();
    }

    private void checkForNewFavInspections() {
        if (!manager.isFavouritesMapEmpty()){
            FragmentManager manager = getSupportFragmentManager();
            NewFavouriteInspectionFragment dialog = new NewFavouriteInspectionFragment();
            dialog.show(manager, "new fav dialog");
        }
    }

    private void showTutorial() {
        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        boolean isFirstTime = preferences.getBoolean(TAG_TUTORIAL, true);

        if (isFirstTime){
            Target target = new ViewTarget(R.id.ib_show_filters, this);
            ShowcaseView showcaseView = new ShowcaseView.Builder(this)
                    .setTarget(target)
                    .setContentTitle("See more filters")
                    .setContentText("Click here to show/hide the filters")
                    .hideOnTouchOutside()
                    .setStyle(R.style.CustomShowcaseTheme)
                    .build();

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(TAG_TUTORIAL, false);
            editor.apply();
        }
    }

    private void setupSavedFilters() {
        Intent intent = getIntent();
        boolean isFavouriteFilterOn = intent.getBooleanExtra(TAG_FAVOURITE, false);
        String searchQuery = intent.getStringExtra(TAG_QUERY);
        boolean isGreaterThan = intent.getBooleanExtra(TAG_GREATER_THAN, false);
        String criticalFilter = intent.getStringExtra(TAG_CRITICAL_FILTER);
        String hazardLevelFilter = intent.getStringExtra(TAG_HAZARD_LEVER);

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
        filterByCriticalViolations(criticalFilter, isGreaterThan);

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        switch (hazardLevelFilter){
            case "Low":
                radioGroup.check(R.id.filterLow_map);
                filterByHazardLevel("Low");
                break;
            case "Moderate":
                radioGroup.check(R.id.filterModerate_map);
                filterByHazardLevel("Moderate");
                break;
            case "High":
                radioGroup.check(R.id.filterHigh_map);
                filterByHazardLevel("High");
                break;
            default:
                radioGroup.check(R.id.filterNone_map);
                filterByHazardLevel("None");
        }



        SearchView searchView = findViewById(R.id.sv_maps);
        searchView.setQuery(searchQuery, true);
        searchView.setIconified(false);
        searchView.clearFocus();

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

    private void setupCriticalFilter() {
        TextView filterText = findViewById(R.id.filterInput_map);

        filterText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    //Toast.makeText(ListRestaurantActivity.this,"Enter detected",Toast.LENGTH_SHORT).show();

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

    private void setupSearchView() {
        SearchView searchView = findViewById(R.id.sv_maps);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterByName(query);
                searchView.clearFocus();

                //geoLocate();
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

    public void filterByName(String text) {

        restaurantsTextFilter = new boolean[restaurantsFullList.size()];

        if(text.isEmpty()){
            setAllValuesToTrue(restaurantsTextFilter);
        } else{
            text = text.toLowerCase();
            for (int i = 0; i < restaurantsFullList.size(); i++){
                Restaurant r = restaurantsFullList.get(i);

                if(r.getName().toLowerCase().contains(text)){
                    restaurantsTextFilter[i] = true;
                }
            }
        }

        filterAll();
    }

    public void filterByFavourites(boolean doFilter){
        restaurantsFavourites = new boolean[restaurantsFullList.size()];

        if (!doFilter){
            setAllValuesToTrue(restaurantsFavourites);
            filterAll();
            return;
        }

        for (int i = 0; i < restaurantsFullList.size(); i++){
            if (restaurantsFullList.get(i).isFavourite()){
                restaurantsFavourites[i] = true;
            }
        }

        filterAll();
    }

    public void filterByCriticalViolations(String criticalViolations, boolean isGreaterThan){

        restaurantsCriticalFilter = new boolean[restaurantsFullList.size()];

        if (criticalViolations.isEmpty()) {
            setAllValuesToTrue(restaurantsCriticalFilter);
            filterAll();
            return;
        }

        int criticalViolation = Integer.parseInt(criticalViolations);

        for (int j = 0; j < restaurantsFullList.size(); j++){
            Restaurant r = restaurantsFullList.get(j);
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
                Log.d("ListActivity",r.getName() + " : " + numOfCriticalViolationsFound);
                restaurantsCriticalFilter[j] = true;
            } else if (!isGreaterThan && numOfCriticalViolationsFound <= criticalViolation){
                restaurantsCriticalFilter[j] = true;
            }
        }

        filterAll();
    }

    private void setupShowFiltersButton() {
        ImageButton showFilters = findViewById(R.id.ib_show_filters);
        filtersLayout = findViewById(R.id.cl_map_filters);
        showFilters.setImageResource(R.drawable.ic_expand_more_black_24dp);

        showFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filtersLayout.getVisibility() == View.INVISIBLE){
                    filtersLayout.setVisibility(View.VISIBLE);
                    showFilters.setImageResource(R.drawable.ic_expand_less_black_24dp);
                } else {
                    filtersLayout.setVisibility(View.INVISIBLE);
                    showFilters.setImageResource(R.drawable.ic_expand_more_black_24dp);
                }

            }
        });
    }




    private void filterEditText(){
        TextView filterText = findViewById(R.id.filterInput_map);
        String content = filterText.getText().toString();

        boolean isGreaterThan = true;
        ToggleButton toggleComparison = findViewById(R.id.tb_greater_or_lesser_map);
        //Toast.makeText(this, toggleComparison.getText(), Toast.LENGTH_SHORT).show();

        if (toggleComparison.isChecked()){
            isGreaterThan = false;
        }

        filterByCriticalViolations(content, isGreaterThan);
    }

    private void setAllValuesToTrue(boolean[] list){
        for (int i = 0; i < list.length; i++){
            list[i] = true;
        }
    }

    public void onRadioClick(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.filterLow_map:
                if (checked)
                    filterByHazardLevel("Low");
                break;
            case R.id.filterModerate_map:
                if (checked)
                    filterByHazardLevel("Moderate");
                break;
            case R.id.filterHigh_map:
                if(checked)
                    filterByHazardLevel("High");
                break;
            case R.id.filterNone_map:
                if(checked)
                    filterByHazardLevel("None");
        }

    }

    public void filterByHazardLevel(String hazardLevel){

        restaurantsHazardFilter = new boolean[restaurantsFullList.size()];

        if (hazardLevel.equals("None")){
            setAllValuesToTrue(restaurantsHazardFilter);
            filterAll();
            return;
        }

        for (int i = 0; i < restaurantsFullList.size(); i++){
            Restaurant r = restaurantsFullList.get(i);

            if(r.getInspections().isEmpty()){
                Log.d("ListActivity",r.getName() + " has no inspections");
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

    private void filterAll(){
        restaurantDataset.clear();
        //map.clear();
        clusterManager.clearItems();
        for (int i = 0; i < manager.getRestaurantList().size(); i++){
            if (restaurantsHazardFilter[i] && restaurantsTextFilter[i]
                    && restaurantsCriticalFilter[i] && restaurantsFavourites[i]){
                restaurantDataset.add(manager.getRestaurantList().get(i));
            }
        }

        //onMapReady(map);
        readItems();

        clusterManager.cluster();

    }


    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapsActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapsActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        return false;
    }

    private void setupToolbar() {
        ImageButton helpButton = findViewById(R.id.ib_restaurant_help_icon);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = InfoScreenActivity.makeLaunchIntent(MapsActivity.this);
                startActivity(intent);
            }
        });

        ImageView listButton = findViewById(R.id.ib_go_to_list);
        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchView searchView = findViewById(R.id.sv_maps);
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
                    case R.id.filterHigh_map:
                        hazardFilter = "High";
                        break;
                    case R.id.filterNone_map:
                        hazardFilter = "none";
                        break;
                }


                Intent listActivity = ListRestaurantActivity.makeIntent(MapsActivity.this,
                        searchView.getQuery().toString(), favSwitch.isChecked(), hazardFilter,
                        tbGreater.isChecked(), criticalFilter.getText().toString());


                startActivity(listActivity);
                finish();
            }
        });
    }

    private void setupRestaurantManager(){
        if (isDataDefaultVersion()){
            // Call old method for csv parsing
            manager.readRestaurantFromOldCSV(this);
            manager.readInspectionsFromOldCSV(this);
        } else {
            // Call new method for csv parsing
            manager.readRestaurantFromNewCSV(this);
            manager.readInspectionsFromNewCSV(this);
        }

        manager.sortByRestaurantName();
        manager.sortInspections();

        restaurantsFullList.addAll(manager.getRestaurantList());
    }

    //region Server Implementation

    private void checkIfTimeToUpdate() {
        if (isTimeForUpdate(getLastUpdateDate())){
            checkForNewServerData();
        }
    }

    private String getLastUpdateDate(){
        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        return preferences.getString(TAG_UPDATE_DATE, "");
    }

    private void checkForNewServerData() {
        Thread gettingDataThread = new Thread(new Runnable(){
            public void run(){
                try{
                    HttpHandler httpHandler = new HttpHandler(RESTAURANT_URL);
                    String latestDate = httpHandler.getCurrentDateFromServer();
                    String lastServerDate = getLastServerDate();

                    if (isNewDataAvailable(lastServerDate, latestDate)){
                        // ask user in dialog
                        FragmentManager manager = getSupportFragmentManager();
                        NewDataFragment dialog = new NewDataFragment(latestDate);
                        dialog.show(manager, TAG_DIALOG);
                    }

                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        gettingDataThread.start();
    }

    public boolean isNewDataAvailable(String lastUpdateDate, String serverDate){
        return !lastUpdateDate.equals(serverDate);
    }

    private String getLastServerDate(){
        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        return preferences.getString(TAG_SERVER_METADATA_DATE, "empty");
    }

    public boolean isTimeForUpdate(String latestDate){
        if (latestDate.isEmpty()){
            DateTime today = new DateTime();
            updateLastUpdateDate(today.toString());
            return true;
        }

        if(isDataDefaultVersion()){
            return true;
        }

        DateCalculations dateCalculations = new DateCalculations();

        return dateCalculations.hoursInBetween(latestDate) >= HOURS_FOR_UPDATE;
    }

    private void updateLastUpdateDate(String newUpdateDate){
        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(TAG_UPDATE_DATE, newUpdateDate);
        editor.apply();
    }

    private boolean isDataDefaultVersion(){
        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        return preferences.getBoolean(TAG_DEFAULT_DATA, true);
    }

    private void setDataDefaultVersionToFalse(){
        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(TAG_DEFAULT_DATA, false);
        editor.apply();
    }

    private void loadCSVsFromServer() {
        loadRestaurantsFromServer();
        loadInspectionsFromServer();
    }

    public String loadInspectionsFromServer() {

        String line = "";
        try {

            HttpHandler httpHandler = new HttpHandler(INSPECTION_URL);
            httpHandler.getData();
            String body = httpHandler.getBody();

            return body;


        } catch (Exception e) {
            Log.wtf("My Activity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }

        return null;

    }

    public String loadRestaurantsFromServer() {

        String line = "";
        try {
            HttpHandler httpHandler = new HttpHandler(RESTAURANT_URL);
            httpHandler.getData();
            String body = httpHandler.getBody();
            return body;

        } catch (Exception e) {
            Log.wtf("My Activity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }

        return "";
    }

    public void updateDataAndRefresh(String latestDate) {
        WaitTask waitTask = new WaitTask(this, latestDate);
        waitTask.execute();
    }

    public void updateFiles(String restaurantBody, String inspectionsBody, String latestDate){
        setDataDefaultVersionToFalse();
        DateTime dateTime = new DateTime();
        updateLastUpdateDate(dateTime.toString());
        updateLastServerDate(latestDate);
        writeDataOnCsvFiles(restaurantBody, inspectionsBody);

        finish();
        startActivity(getIntent());
    }

    public String[] getBodiesFromServer(){
        storeFavouriteRestaurants();

        loadCSVsFromServer();
        String restaurantBody = loadRestaurantsFromServer();
        String inspectionsBody = loadInspectionsFromServer();

        String[] bodies = {restaurantBody, inspectionsBody};

        return bodies;
    }

    private void storeFavouriteRestaurants() {
        for (Restaurant r : manager.getRestaurantList()){
            if (r.isFavourite()){
                if (r.getInspections().size() > 0){
                    manager.insertIntoFavouritesMap(r.getTrackingNumber(), r.getInspections().get(0));
                }

            }
        }
    }

    private void writeDataOnCsvFiles(String restaurantBody, String inspectionsBody) {

        try{
            File path = getFilesDir();
            File file = new File(path, "restaurants.csv");
            PrintWriter writer = new PrintWriter(file);
            writer.write(restaurantBody);
            writer.close();

            path = getFilesDir();
            file = new File(path, "inspectionreports.csv");
            writer = new PrintWriter(file);
            writer.write(inspectionsBody);
            writer.close();

        } catch (IOException e){
            e.printStackTrace();
        }

    }

    private void updateLastServerDate(String newServerDate){
        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(TAG_SERVER_METADATA_DATE, newServerDate);
        editor.apply();
    }

    //endregion Server Implementation

    //region Map Implementation

    public GoogleMap getMap() {
        return map;
    }

    private String violationType = "";
    private int maxCritical = 564983;


    private void init(){
        Log.d(TAG, "init: initializing");

//        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH
//                        ||actionId == EditorInfo.IME_ACTION_DONE
//                        ||keyEvent.getAction() == KeyEvent.ACTION_DOWN
//                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
//                    //action goes here:
//                    geoLocate();
//                    hideSoftKeyboard();
//                }
//
//                return false;
//            }
//        });
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });

        hideSoftKeyboard();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("MapActivity","onMapReadyCalled");
        map = googleMap;

        clusterManager = new ClusterManager<ClusterMarker>(this, getMap());
        clusterManager.setRenderer(new MyClusterManagerRenderer(this, map, clusterManager));
        getMap().setOnCameraIdleListener(clusterManager);
        getMap().setOnMarkerClickListener(clusterManager);
        getMap().setOnInfoWindowClickListener(clusterManager);

        clusterManager.setOnClusterClickListener(this);
        clusterManager.setOnClusterItemInfoWindowClickListener(this);

        readItems();

        clusterManager.cluster();

        setupSavedFilters();

        if(locationPermissionsGranted){
            Log.d(TAG, "Executing: getDeviceLocation() function");
            getDeviceLocation();
            map.setMyLocationEnabled(true);

            //UI settings
            map.getUiSettings().setMyLocationButtonEnabled(false);
            map.getUiSettings().setAllGesturesEnabled(true);
            map.getUiSettings().setZoomControlsEnabled(true);

            init();
        }
    }

    private void readItems() {
        Log.d("ReadItem", "START");
        Log.d("ReadItem", "List = " + restaurantDataset.size());

        for (Restaurant restaurant : restaurantDataset) {
            Log.d("ReadItem", restaurant.getName());
            int markerID;
            String hazardRating;
            if (restaurant.getInspections().isEmpty()) {
                hazardRating = getString(R.string.none);
            } else {
                hazardRating = restaurant.getInspections().get(0).getHazardRating();
            }

            if (hazardRating.equals("High")) {
                hazardRating = getString(R.string.high);
                markerID = R.drawable.criticality_high_icon;
            } else if (hazardRating.equals("Moderate")) {
                hazardRating = getString(R.string.moderate);
                markerID = R.drawable.criticality_medium_icon;
            } else {
                hazardRating = getString(R.string.low);
                markerID = R.drawable.criticality_low_icon;
            }
            ClusterMarker newClusterMarker = new ClusterMarker(

                    new LatLng(restaurant.getLatitude(), restaurant.getLongitude()),
                    restaurant.getName() + "\n" + restaurant.getPhysicalAddress() + "\n" + getString(R.string.hazard_rating)  + " " + hazardRating,
                    "blank",
                    markerID,
                    restaurant.getTrackingNumber()
            );

            clusterManager.addItem(newClusterMarker);
            clusterMarkers.add(newClusterMarker);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        locationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            locationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    locationPermissionsGranted = true;
                    initMap();
                }
            }
        }
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void initMap(){
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void getDeviceLocation () {
        Log.d(TAG, "getDeviceLocation: getting the device current location");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            if(locationPermissionsGranted){
                Task location = fusedLocationClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            Intent intent = getIntent();
                            String trackingNumberFromIntent = intent.getStringExtra(TAG_EXTRA_TRACKING_NUMBER);

                            if (comeFromInspectionList) {

                                isComingFromGPS = true;
                                ClusterMarker foundMarker = new ClusterMarker();
                                int index = 0;
                                for (ClusterMarker marker : clusterMarkers) {

                                    if (marker.getTrackingNumber().equals(trackingNumberFromIntent)) {
                                        foundMarker = marker;
                                        break;
                                    }
                                    index++;
                                }

                                LatLng currGPS = foundMarker.getPosition();

                                final int fIndex = index;
                                customMarker = map.addMarker(new MarkerOptions().position(currGPS).title(foundMarker.getTitle()).snippet(foundMarker.getSnippet()));
                                moveCamera(currGPS,
                                        DEFAULT_ZOOM, foundMarker.getTitle());
                                customMarker.showInfoWindow();

                                customMarkerTrackingNumber = foundMarker.getTrackingNumber();

                                comeFromInspectionList = false;

                            } else {
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                        DEFAULT_ZOOM, "My location");
                            }


                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                        }
                    }
                });
            }

        }catch (SecurityException e){
            Log.e(TAG,"getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat:" + latLng.latitude + ", lng: " + latLng.longitude);
        map.moveCamera( CameraUpdateFactory.newLatLngZoom(latLng,zoom));

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.custom_info_window, null);
                TextView tLocation = v.findViewById(R.id.infoAddress);
                if (marker.getTitle().contains(getString(R.string.hazard_rating) + " " + getString(R.string.high))){
                    v.setBackgroundColor(ContextCompat.getColor(MapsActivity.this, R.color.colorHighHazard));
                } else if (marker.getTitle().contains(getString(R.string.hazard_rating) + " " + getString(R.string.moderate))){
                    v.setBackgroundColor(ContextCompat.getColor(MapsActivity.this, R.color.colorMedHazard));
                } else if (marker.getTitle().contains(getString(R.string.hazard_rating) + " " + getString(R.string.low))){
                    v.setBackgroundColor(ContextCompat.getColor(MapsActivity.this, R.color.colorLowHazard));
                }
                tLocation.setText(marker.getTitle());
                return v;
            }
        });


        if (!title.equals("My location") ){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            map.addMarker(options);
        }
        hideSoftKeyboard();

    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    //endregion Map Implementation

    //region Interface Override methods

    @Override
    public void onClusterItemInfoWindowClick(ClusterMarker item) {
        String trackingNumber = item.getTrackingNumber();

        Intent intent = RestaurantReportActivity.makeIntent(this, trackingNumber);
        startActivity(intent);
    }


    @Override
    public boolean onClusterClick(Cluster<ClusterMarker> cluster) {
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }

        final LatLngBounds bounds = builder.build();

        try {
            getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }



    //endregion Interface Override methods



}

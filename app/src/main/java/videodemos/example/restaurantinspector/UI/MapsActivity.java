package videodemos.example.restaurantinspector.UI;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

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

import videodemos.example.restaurantinspector.Model.ClusterMarker;
import videodemos.example.restaurantinspector.Model.DataHandling.DateCalculations;
import videodemos.example.restaurantinspector.Model.DataHandling.Restaurant;
import videodemos.example.restaurantinspector.Model.Network.HttpHandler;
import videodemos.example.restaurantinspector.Model.RestaurantManager;
import videodemos.example.restaurantinspector.R;
import videodemos.example.restaurantinspector.UI.Dialogs.NewDataFragment;
import videodemos.example.restaurantinspector.Utilities.MyClusterManagerRenderer;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        ClusterManager.OnClusterClickListener<ClusterMarker>, ClusterManager.OnClusterInfoWindowClickListener<ClusterMarker>,
        ClusterManager.OnClusterItemClickListener<ClusterMarker>, ClusterManager.OnClusterItemInfoWindowClickListener<ClusterMarker>{

    public static boolean comeFromInspectionList = false;

    private final String RESTAURANT_URL = "https://data.surrey.ca/api/3/action/package_show?id=restaurants";
    private final String INSPECTION_URL = "https://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";
    private final int HOURS_FOR_UPDATE = 20;
    private final String PREFERENCES = "data";
    private final String TAG_UPDATE_DATE = "last_update_date";
    private final String TAG_SERVER_METADATA_DATE = "last_server_date";
    private final String TAG_DEFAULT_DATA = "default_data";
    private final String TAG_DIALOG = "new data dialog tag";

    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    public static final float DEFAULT_ZOOM =15f;

    private GoogleMap mMap;
    private ClusterManager<ClusterMarker> mClusterManager;
    private List<ClusterMarker> mClusterMarkers = new ArrayList<>();

    //widgets
    private EditText mSearchText;
    private ImageView mGps;
    //vars
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationClient;     // dependency:     implementation 'com.google.android.gms:play-services-location:15.0.1'

    private boolean isComingFromGPS = false;

    private SharedPreferences preferences;
    private RestaurantManager manager = RestaurantManager.getInstance();;

    private boolean haveCoordinatesBeenSet = false;

    public static Intent makeIntent(Context c){
        Intent intent = new Intent(c,MapsActivity.class);
        return intent;
    }

    public static Intent makeGPSIntent(Context c, double latitude, double longitude){
        Intent intent = new Intent(c,MapsActivity.class);
        intent.putExtra("lat", latitude);
        intent.putExtra("long", longitude);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        checkIfTimeToUpdate();
        setupRestaurantManager();

        setupToolbar();

        getLocationPermission();
        mSearchText = findViewById(R.id.input_search);
        mGps = findViewById(R.id.ic_gps);

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
                Intent mainActivity = MainActivity.makeIntent(MapsActivity.this);
                startActivity(mainActivity);
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
    }

    //region Server Implementation

    private void checkIfTimeToUpdate() {
        if (isTimeForUpdate(getLastUpdateDate())){
            checkForNewServerData();
        }
    }

    private String getLastUpdateDate(){
        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        String date = preferences.getString(TAG_UPDATE_DATE, "");
        return date;
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
        String date = preferences.getString(TAG_SERVER_METADATA_DATE, "empty");
        return date;
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
        Log.d("Updated:", "DEFAULT DATA UPDATED");
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
        loadCSVsFromServer();
        String restaurantBody = loadRestaurantsFromServer();
        String inspectionsBody = loadInspectionsFromServer();

        String[] bodies = {restaurantBody, inspectionsBody};

        return bodies;
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
        return mMap;
    }

    private void init(){
        Log.d(TAG, "init: initializing");

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        ||actionId == EditorInfo.IME_ACTION_DONE
                        ||keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
                    //action goes here:
                    geoLocate();
                }

                return false;
            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });

        hideSoftKeyboard();
    }

    private void geoLocate() {
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString,1);
        }catch (IOException e){
            Log.e(TAG,"geoLocate: IOException: " + e.getMessage());
        }

        if(list.size() > 0){
            Address address = list.get(0);

            Log.d(TAG, "geoLocate:found a location:" + address.toString());
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()),
                    DEFAULT_ZOOM,
                    address.getAddressLine(0) );
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("MapActivity","onMapReadyCalled");
        mMap = googleMap;

        mClusterManager = new ClusterManager<ClusterMarker>(this, getMap());
        mClusterManager.setRenderer(new MyClusterManagerRenderer(this,mMap,mClusterManager));
        getMap().setOnCameraIdleListener(mClusterManager);
        getMap().setOnMarkerClickListener(mClusterManager);
        getMap().setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);

        readItems();

        mClusterManager.cluster();

        if(mLocationPermissionsGranted){
            Log.d(TAG, "Executing: getDeviceLocation() function");
            getDeviceLocation();
            mMap.setMyLocationEnabled(true);

            //UI settings
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setAllGesturesEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);

            init();
        }
    }

    private void readItems() {
        for (Restaurant restaurant : manager.getRestaurantList()) {
            int markerID;
            String hazardRating;
            if (restaurant.getInspections().isEmpty()) {
                hazardRating = "No hazards";
            } else {
                hazardRating = restaurant.getInspections().get(0).getHazardRating();
            }

            if (hazardRating.equals("High")) {
                markerID = R.drawable.criticality_high_icon;
            } else if (hazardRating.equals("Moderate")) {
                markerID = R.drawable.criticality_medium_icon;
            } else {
                markerID = R.drawable.criticality_low_icon;
            }
            ClusterMarker newClusterMarker = new ClusterMarker(

                    new LatLng(restaurant.getLatitude(), restaurant.getLongitude()),
                    restaurant.getName() + "\n" + restaurant.getPhysicalAddress() + "\n" + "Hazard Rating: " + hazardRating,
                    "blank",
                    markerID,
                    restaurant.getTrackingNumber()
            );

            mClusterManager.addItem(newClusterMarker);
            mClusterMarkers.add(newClusterMarker);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
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
                mLocationPermissionsGranted = true;
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
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            if(mLocationPermissionsGranted){
                Task location = mFusedLocationClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            Intent intent = getIntent();
                            double latitude = intent.getDoubleExtra("lat", -999.0);
                            double longitude = intent.getDoubleExtra("long", -999.0);

                            if (latitude != -999.0 && longitude != -999.0 && comeFromInspectionList) {

                                isComingFromGPS = true;
                                LatLng currGPS = new LatLng(latitude, longitude);
                                ClusterMarker foundMarker = new ClusterMarker();
                                int index = 0;
                                for (ClusterMarker marker : mClusterMarkers) {

                                    if (marker.getPosition().equals(currGPS)) {
                                        foundMarker = marker;
                                        break;
                                    }
                                    index++;
                                }

                                final int fIndex = index;
                                Marker mark = mMap.addMarker(new MarkerOptions().position(currGPS).title(foundMarker.getTitle()).snippet(foundMarker.getSnippet()));
                                moveCamera(new LatLng(latitude, longitude),
                                        DEFAULT_ZOOM, foundMarker.getTitle());
                                mark.showInfoWindow();


                                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                    @Override
                                    public void onInfoWindowClick(Marker marker) {
                                        Intent intent = RestaurantReportActivity.makeIntent(MapsActivity.this, fIndex);
                                        startActivity(intent);
                                    }
                                });

                                comeFromInspectionList = false;

                            } else {
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                        DEFAULT_ZOOM, "My location");
                            }


                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
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
        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(latLng,zoom));

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.custom_info_window, null);
                TextView tLocation = v.findViewById(R.id.infoAddress);
                if (marker.getTitle().contains("Hazard Rating: High")){
                    v.setBackgroundColor(ContextCompat.getColor(MapsActivity.this, R.color.colorHighHazard));
                } else if (marker.getTitle().contains("Hazard Rating: Moderate")){
                    v.setBackgroundColor(ContextCompat.getColor(MapsActivity.this, R.color.colorMedHazard));
                } else if (marker.getTitle().contains("Hazard Rating: Low")){
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
            mMap.addMarker(options);
        }
        hideSoftKeyboard();

    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    //endregion Map Implementation

    //region Interface Override methods

    @Override
    public void onClusterInfoWindowClick(Cluster<ClusterMarker> cluster) {

    }

    @Override
    public boolean onClusterItemClick(ClusterMarker item) {

        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(ClusterMarker item) {
        String trackingNumber = item.getTrackingNumber();
        int index = 0;
        for(int i = 0; i < manager.getRestaurantList().size();i++){
            Restaurant rInQuestion = manager.getRestaurant(i);
            if(rInQuestion.getTrackingNumber().equals(trackingNumber)){
                index = i;
                break;
            }
        }

        if(isComingFromGPS){
            finish();
        }else{
            Intent intent = RestaurantReportActivity.makeIntent(this, index);
            startActivity(intent);
        }

    }


    @Override
    public boolean onClusterClick(Cluster<ClusterMarker> cluster) {
        // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
        // inside of bounds, then animate to center of the bounds.

        // Create the builder to collect all essential cluster items for the bounds.
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        // Get the LatLngBounds
        final LatLngBounds bounds = builder.build();

        // Animate camera to the bounds
        try {
            getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }



    //endregion Interface Override methods
}

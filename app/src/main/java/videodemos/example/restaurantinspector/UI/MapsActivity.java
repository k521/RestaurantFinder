package videodemos.example.restaurantinspector.UI;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

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
import com.google.maps.android.clustering.view.ClusterRenderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import videodemos.example.restaurantinspector.Model.ClusterMarker;
import videodemos.example.restaurantinspector.Model.DataHandling.Restaurant;
import videodemos.example.restaurantinspector.Model.RestaurantManager;
import videodemos.example.restaurantinspector.R;
import videodemos.example.restaurantinspector.Utilities.MyClusterManagerRenderer;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        ClusterManager.OnClusterClickListener<ClusterMarker>, ClusterManager.OnClusterInfoWindowClickListener<ClusterMarker>,
        ClusterManager.OnClusterItemClickListener<ClusterMarker>, ClusterManager.OnClusterItemInfoWindowClickListener<ClusterMarker>{

    private GoogleMap mMap;

    private boolean isComingFromGPS = false;

    public GoogleMap getMap() {
        return mMap;
    }

    public static Intent makeIntent(Context c){
        Intent intent = new Intent(c,MapsActivity.class);
        return intent;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getLocationPermission();
        mSearchText = findViewById(R.id.input_search);
        mGps = findViewById(R.id.ic_gps);



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

        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent mainActivity = MainActivity.makeIntent(MapsActivity.this);
                    startActivity(mainActivity);
                    finish();
                    // The toggle is enabled
                } else {
                    // The toggle is disabled
                }
            }
        });
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
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    //private ClusterManager mClusterManager;
    //  private MyClusterManagerRenderer mClusterManagerRenderer;
//    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();

    private ClusterManager<ClusterMarker> mClusterManager;

    private List<ClusterMarker> mClusterMarkers = new ArrayList<>();



    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("MapActivity","onMapReadyCalled");
        RestaurantManager manager = RestaurantManager.getInstance(this);
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
        //mClusterManager.setRenderer(mClusterManagerRenderer);
        RestaurantManager manager = RestaurantManager.getInstance(this);
        for (Restaurant restaurant : manager.getRestaurantList()) {
            int markerID;
            String hazardRating;
            if (restaurant.getInspections().isEmpty()) {
                hazardRating = "No hazards";
            } else {
                hazardRating = restaurant.getInspections().get(0).getHazardRating();
            }

            if (hazardRating.equals("Low")) {
                markerID = R.drawable.criticality_low_icon;
            } else if (hazardRating.equals("Moderate")) {
                markerID = R.drawable.criticality_medium_icon;
            } else {
                markerID = R.drawable.criticality_high_icon;
            }
            ClusterMarker newClusterMarker = new ClusterMarker(

                    new LatLng(restaurant.getLatitude(), restaurant.getLongitude()),
                    restaurant.getName() + "\n" + restaurant.getPhysicalAddress() + "\n" + "Hazard Rating: " + hazardRating,
                    "blank",
                    markerID,
                    restaurant
            );

            mClusterManager.addItem(newClusterMarker);
            mClusterMarkers.add(newClusterMarker);
        }

    }


    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    //widgets
    private EditText mSearchText;
    private ImageView mGps;

    //vars
    private Boolean mLocationPermissionsGranted = false;

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

    private FusedLocationProviderClient mFusedLocationClient;     // dependency:     implementation 'com.google.android.gms:play-services-location:15.0.1'
    public static final float DEFAULT_ZOOM =15f;

    public void getDeviceLocation ()
    {
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

                            if(latitude != -999.0 && longitude != -999.0){

                                isComingFromGPS = true;
                                LatLng currGPS = new LatLng(latitude, longitude);
                                ClusterMarker foundMarker = new ClusterMarker();
                                int index = 0;
                                for(ClusterMarker marker: mClusterMarkers){

                                    if(marker.getPosition().equals(currGPS)){
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


                            }else{
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
    private void hideSoftKeyboard()
    {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    @Override
    public void onClusterInfoWindowClick(Cluster<ClusterMarker> cluster) {

    }

    @Override
    public boolean onClusterItemClick(ClusterMarker item) {

        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(ClusterMarker item) {
        RestaurantManager manager = RestaurantManager.getInstance(this);
        Restaurant r = item.getRestaurant();
        int index = 0;
        for(int i = 0; i < manager.getRestaurantList().size();i++){
            Restaurant rInQuestion = manager.getRestaurant(i);
            if(rInQuestion.getTrackingNumber().equals(r.getTrackingNumber())){
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
        // Show a toast with some info when the cluster is clicked.
        String firstName = cluster.getItems().iterator().next().getRestaurant().getName();
        // Toast.makeText(this, cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();

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
    public static Intent makeGPSIntent(Context c, double latitude, double longitude){
        Intent intent = new Intent(c,MapsActivity.class);
        intent.putExtra("lat", latitude);
        intent.putExtra("long", longitude);
        return intent;
    }



//     @Override
//    public void onBackPressed() {
//        RestaurantManager manager = RestaurantManager.getInstance(this);
//        manager.getRestaurantList().clear();
//
//        finish();
//
//
//    }

}

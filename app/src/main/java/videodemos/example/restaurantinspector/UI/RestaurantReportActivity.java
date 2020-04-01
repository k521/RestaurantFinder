package videodemos.example.restaurantinspector.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import videodemos.example.restaurantinspector.Model.DataHandling.Restaurant;
import videodemos.example.restaurantinspector.Model.RestaurantManager;
import videodemos.example.restaurantinspector.R;
import videodemos.example.restaurantinspector.UI.Adapters.InspectionsAdapter;

/**
 * A class that shows inspection reports for a chosen restaurant.
 */
public class RestaurantReportActivity extends AppCompatActivity
        implements InspectionsAdapter.OnInspectionListener {

    private static final String RESTAURANT_INDEX = "RESTAURANT_INDEX";

    public static Intent makeIntent(Context context, int indexOfRestaurant){
        Intent intent = new Intent(context, RestaurantReportActivity.class);
        intent.putExtra(RESTAURANT_INDEX, indexOfRestaurant);

        return intent;
    }


    private final String PREFERENCES = "data";
    private final String TAG_TRACKING_NUMBER_LIST = "list of tracking numbers";
    private SharedPreferences preferences;
    private int indexOfRestaurant;
    private Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        indexOfRestaurant = getIntent().getIntExtra(RESTAURANT_INDEX, 0);
        RestaurantManager manager = RestaurantManager.getInstance();
        restaurant = manager.getRestaurant(indexOfRestaurant);

        if (restaurant.getInspections().isEmpty()){
            setContentView(R.layout.activity_restaurant_report_empty);
        } else {
            setContentView(R.layout.activity_restaurant_report);
            setupRecyclerView();
        }

        setupToolbar();
        setupRestaurantInfoTextViews();
    }

    public void onGpsClick(View v){
        double latitude = restaurant.getLatitude();
        double longitude = restaurant.getLongitude();
        MapsActivity.comeFromInspectionList = true;
        Intent intent = MapsActivity.makeGPSIntent(this, latitude, longitude);
        startActivity(intent);
    }

    private void setupToolbar() {
        ImageButton backButton = findViewById(R.id.ib_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        ToggleButton favouriteButton = findViewById(R.id.tb_favourite);

        favouriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    favouriteButton.setBackgroundResource(R.drawable.star_filled);
                    setRestaurantToFavourite();
                } else {
                    favouriteButton.setBackgroundResource(R.drawable.star_empty);
                    restaurant.setFavourite(false);
                }
            }
        });

        if (restaurant.isFavourite()){
            favouriteButton.setChecked(true);
        }
    }

    private void setRestaurantToFavourite(){
        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        restaurant.setFavourite(true);

        String listOfTrackingNums = getFavouriteRestaurantsTrackingNumbers();
        if (listOfTrackingNums.equals("")){
            listOfTrackingNums = restaurant.getTrackingNumber();
        } else {
            listOfTrackingNums += "," + restaurant.getTrackingNumber();
        }

        editor.putString(TAG_TRACKING_NUMBER_LIST, listOfTrackingNums);
        editor.apply();
    }

    private String getFavouriteRestaurantsTrackingNumbers(){
        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        return preferences.getString(TAG_TRACKING_NUMBER_LIST, "");
    }

    private void setupRestaurantInfoTextViews() {
        TextView restaurantName = findViewById(R.id.tv_report_restaurant_name);
        restaurantName.setText(restaurant.getName());

        TextView address = findViewById(R.id.tv_report_address);
        address.setText(restaurant.getPhysicalAddress());

        TextView gpsCoordinates = findViewById(R.id.tv_report_coordinates);
        gpsCoordinates.setText(getString(R.string.coordinates, restaurant.getLatitude(), restaurant.getLongitude()));
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.rv_reports_list);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        InspectionsAdapter adapter = new InspectionsAdapter(restaurant.getInspections(),
                this, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onInspectionClick(int position) {
        Log.d("We are passing the following index", "Rest Index " + indexOfRestaurant +
                " Inspect Index " + position);
        Intent intent = InspectionReportActivity.makeIntent(this,indexOfRestaurant,position);
        startActivity(intent);

    }
}

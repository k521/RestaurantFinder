package videodemos.example.restaurantinspector.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import videodemos.example.restaurantinspector.Model.DataHandling.Restaurant;
import videodemos.example.restaurantinspector.Model.RestaurantManager;
import videodemos.example.restaurantinspector.R;

public class RestaurantReportActivity extends AppCompatActivity implements InspectionsAdapter.OnInspectionListener {

    private static final String RESTAURANT_INDEX = "RESTAURANT_INDEX";

    public static Intent makeIntent(Context context, int indexOfRestaurant){
        Intent intent = new Intent(context, RestaurantReportActivity.class);
        intent.putExtra(RESTAURANT_INDEX, indexOfRestaurant);

        return intent;
    }


    private int indexOfRestaurant;
    private Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        indexOfRestaurant = getIntent().getIntExtra(RESTAURANT_INDEX, 0);
        RestaurantManager manager = RestaurantManager.getInstance(this);
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

    public void onClick(View v){
        double latitude = restaurant.getLatitude();
        double longitude = restaurant.getLongitude();
        Intent intent = MapsActivity.makeGPSIntent(this, latitude, longitude);
        startActivity(intent);
        //finish();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.restaurant_report_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
        InspectionsAdapter adapter = new InspectionsAdapter(restaurant.getInspections(), this, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onInspectionClick(int position) {
        Log.d("We are passing the following index", "Rest Index " + indexOfRestaurant +" Inspect Index " + position);
        Intent intent = InspectionReportActivity.makeIntent(this,indexOfRestaurant,position);

        //startActivity(intent);

    }
}

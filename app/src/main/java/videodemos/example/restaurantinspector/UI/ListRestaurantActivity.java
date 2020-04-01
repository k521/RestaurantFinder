package videodemos.example.restaurantinspector.UI;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;
import java.util.List;

import videodemos.example.restaurantinspector.Model.DataHandling.Inspection;
import videodemos.example.restaurantinspector.Model.DataHandling.Restaurant;
import videodemos.example.restaurantinspector.Model.RestaurantManager;
import videodemos.example.restaurantinspector.R;
import videodemos.example.restaurantinspector.UI.Adapters.RestaurantsAdapter;

/**
 * Main Activity displays all the restaurants.
 */

public class ListRestaurantActivity extends AppCompatActivity implements RestaurantsAdapter.OnRestaurantListener {

    private static final String TAG = "ListRestaurantActivity";

    private List<Restaurant> filteredList = new ArrayList<>();

    private RestaurantManager manager;
    RestaurantsAdapter restaurantsAdapter;
    RecyclerView.LayoutManager layoutManager;

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

        setUpRestaurantsRecylerView(true);

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

    private void setUpRestaurantsRecylerView(boolean useManager) {
        RecyclerView restaurantsRecyclerView = findViewById(R.id.rv_restaurant_list);

        restaurantsRecyclerView.setHasFixedSize(true);
        restaurantsRecyclerView.setItemViewCacheSize(20);
        layoutManager = new LinearLayoutManager(this);
        restaurantsRecyclerView.setLayoutManager(layoutManager);

        if(useManager){
            restaurantsAdapter = new RestaurantsAdapter(manager.getRestaurantList(), this, this);
            restaurantsRecyclerView.setAdapter(restaurantsAdapter);
        }
        else{
            restaurantsAdapter = new RestaurantsAdapter(filteredList, this, this);
            restaurantsRecyclerView.setAdapter(restaurantsAdapter);
        }


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
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.filterLow:
                if (checked)
                    filterRestaurantList("Low");
                    setUpRestaurantsRecylerView(false);
                    break;
            case R.id.filterModerate:
                if (checked)
                    filterRestaurantList("Moderate");
                    setUpRestaurantsRecylerView(false);
                    break;
            case R.id.filterHigh:
                if(checked)
                    filterRestaurantList("High");
                    setUpRestaurantsRecylerView(false);
                    break;
        }

    }

    public void filterRestaurantList(String hazardLevel){
        filteredList.clear();
        for(Restaurant r : manager.getRestaurantList()){
            if(r.getInspections().isEmpty()){
                Log.d("ListActivity",r.getName() + " has no inspections");
                continue;
            }
            Inspection mostRecentInspection = r.getInspections().get(0);
            if(mostRecentInspection.getHazardRating().equals(hazardLevel)){
                //r.setVisible(false);
                filteredList.add(r);
            }
        }
    }
}

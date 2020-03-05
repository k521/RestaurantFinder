package videodemos.example.restaurantinspector.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import videodemos.example.restaurantinspector.Model.Restaurant;
import videodemos.example.restaurantinspector.Model.RestaurantManager;
import videodemos.example.restaurantinspector.Model.ViolationMaps;
import videodemos.example.restaurantinspector.R;

public class MainActivity extends AppCompatActivity {

    private RestaurantManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = RestaurantManager.getInstance(this);
        ViolationMaps violationInstance = ViolationMaps.getInstance();

        manager.InspectionReader(this);

        Restaurant r = manager.getRestaurant(0);
        Log.d("MainActivity is here. We have", "" + r.getInspections().size());
        manager.sortInspections();

//        RecyclerView restaurantsRecyclerView = findViewById(R.id.rv_restaurant_list);
//
//        restaurantsRecyclerView.setHasFixedSize(true);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
//        restaurantsRecyclerView.setLayoutManager(layoutManager);
//
//        RestaurantsAdapter restaurantsAdapter = new RestaurantsAdapter(manager.getRestaurantList());
//        restaurantsRecyclerView.setAdapter(restaurantsAdapter);

        Intent intent = RestaurantReportActivity.makeIntent(this, 0);
        startActivity(intent);



    }
}

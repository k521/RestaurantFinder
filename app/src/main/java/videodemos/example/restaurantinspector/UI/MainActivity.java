package videodemos.example.restaurantinspector.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import okhttp3.Request;
import okhttp3.Response;
import videodemos.example.restaurantinspector.Model.DataHandling.RestaurantManager;
import videodemos.example.restaurantinspector.Model.Network.HttpHandler;
import videodemos.example.restaurantinspector.R;

/**
 * Main Activity displays all the restaurants.
 */

public class MainActivity extends AppCompatActivity implements RestaurantsAdapter.OnRestaurantListener {

    private RestaurantManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




//        System.out.println("We are done here");
//        System.out.println(httpHandler.getBody());



        setupToolbar();
        setupRestaurantManager();
        setUpRestaurantsRecylerView();

    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.restaurant_list_toolbar);
        setSupportActionBar(toolbar);

        ImageButton helpButton = findViewById(R.id.ib_restaurant_help_icon);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = InfoScreenActivity.makeLaunchIntent(MainActivity.this);
                startActivity(intent);
            }
        });
    }

    private void setupRestaurantManager(){
        manager = RestaurantManager.getInstance(this);
        manager.InspectionReader(this);
        manager.sortInspections();
    }

    private void setUpRestaurantsRecylerView() {
        RecyclerView restaurantsRecyclerView = findViewById(R.id.rv_restaurant_list);

        restaurantsRecyclerView.setHasFixedSize(true);
        restaurantsRecyclerView.setItemViewCacheSize(20);
        restaurantsRecyclerView.setDrawingCacheEnabled(true);
        restaurantsRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        restaurantsRecyclerView.setLayoutManager(layoutManager);

        RestaurantsAdapter restaurantsAdapter = new RestaurantsAdapter(manager.getRestaurantList(), this, this);
        restaurantsRecyclerView.setAdapter(restaurantsAdapter);
    }

    @Override
    public void onRestaurantClick(int position) {
            Intent intent = RestaurantReportActivity.makeIntent(this, position);
            startActivity(intent);
    }
}

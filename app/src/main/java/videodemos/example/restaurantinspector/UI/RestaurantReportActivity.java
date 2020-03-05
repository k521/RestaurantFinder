package videodemos.example.restaurantinspector.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import videodemos.example.restaurantinspector.Model.Restaurant;
import videodemos.example.restaurantinspector.Model.RestaurantManager;
import videodemos.example.restaurantinspector.R;

public class RestaurantReportActivity extends AppCompatActivity implements InspectionsAdapter.OnInspectionListener {

    private static final String RESTAURANT_INDEX = "RESTAURANT_INDEX";

    public static Intent makeIntent(Context context, int indexOfRestaurant){
        Intent intent = new Intent(context, RestaurantReportActivity.class);
        intent.putExtra(RESTAURANT_INDEX, indexOfRestaurant);

        return intent;
    }

    private Restaurant restaurant;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int restaurantIndex = getIntent().getIntExtra(RESTAURANT_INDEX, 0);
        RestaurantManager manager = RestaurantManager.getInstance(this);

        restaurant = manager.getRestaurantList().get(restaurantIndex);

        if (restaurant.getInspections().size() > 0){
            setContentView(R.layout.activity_restaurant_report);
            setupRecyclerView();
        } else {
            setContentView(R.layout.activity_restaurant_report_empty);
        }

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
        Toast.makeText(this, "Inspection clicked: " + restaurant.getInspections().get(position).toString(), Toast.LENGTH_SHORT).show();
    }
}

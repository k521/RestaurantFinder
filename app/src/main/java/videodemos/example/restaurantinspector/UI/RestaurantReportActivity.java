package videodemos.example.restaurantinspector.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import videodemos.example.restaurantinspector.Model.Restaurant;
import videodemos.example.restaurantinspector.Model.RestaurantManager;
import videodemos.example.restaurantinspector.R;

public class RestaurantReportActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_restaurant_report);

        int restaurantIndex = getIntent().getIntExtra(RESTAURANT_INDEX, 0);
        RestaurantManager manager = RestaurantManager.getInstance(this);

        restaurant = manager.getRestaurantList().get(restaurantIndex);

    }
}

package videodemos.example.restaurantinspector.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

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

    }
}

package videodemos.example.restaurantinspector.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.HashMap;

import videodemos.example.restaurantinspector.Model.RestaurantManager;
import videodemos.example.restaurantinspector.Model.Violations;
import videodemos.example.restaurantinspector.R;

public class MainActivity extends AppCompatActivity {

    private RestaurantManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = RestaurantManager.getInstance(this);
        Violations violationInstance = Violations.getInstance();

    }
}

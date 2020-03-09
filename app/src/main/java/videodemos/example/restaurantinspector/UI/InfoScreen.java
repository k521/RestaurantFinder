package videodemos.example.restaurantinspector.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import videodemos.example.restaurantinspector.R;

public class InfoScreen extends AppCompatActivity {

    public static Intent makeLaunchIntent (Context context)
    {
        Intent intent = new Intent( context, InfoScreen.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_screen);


    }
}

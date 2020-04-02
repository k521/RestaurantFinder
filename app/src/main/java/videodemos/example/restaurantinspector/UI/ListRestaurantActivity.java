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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import videodemos.example.restaurantinspector.Model.DataHandling.DateCalculations;
import videodemos.example.restaurantinspector.Model.DataHandling.Inspection;
import videodemos.example.restaurantinspector.Model.DataHandling.Restaurant;
import videodemos.example.restaurantinspector.Model.RestaurantManager;
import videodemos.example.restaurantinspector.R;
import videodemos.example.restaurantinspector.UI.Adapters.RestaurantsAdapter;
import android.view.View.OnKeyListener;
import android.view.View;
import android.view.KeyEvent;



/**
 * Main Activity displays all the restaurants.
 */

public class ListRestaurantActivity extends AppCompatActivity implements RestaurantsAdapter.OnRestaurantListener {

    private static final String TAG = "ListRestaurantActivity";

    boolean isTextFilterOn = false;

    boolean isHazardLevelFilterOn = false;

    boolean isOneFilterOn = false;

    private List<Restaurant> filteredList = new ArrayList<>();

    private List <Restaurant> filteredList2 = new ArrayList<>();

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


        TextView filterText = findViewById(R.id.filterInput);

        filterText.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                //If the keyevent is a key-down event on the "enter" button
                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //...
                    // Perform your action on key press here
                    // ...
                    Toast.makeText(ListRestaurantActivity.this,"Enter detected",Toast.LENGTH_SHORT).show();
                    filterEditText();

                    return true;
                }
                return false;
            }
        });

        setUpRestaurantsRecylerView(true);
    }

    private void filterEditText(){
        TextView filterText = findViewById(R.id.filterInput);
        String content = filterText.getText().toString();
        if(content.isEmpty()){
            return;
        }
        int numOfViolationsInput = Integer.parseInt(content);
        Toast.makeText(this,"Input number is " + numOfViolationsInput,Toast.LENGTH_SHORT).show();
        filterByCriticalViolations(numOfViolationsInput);
    }

    private void filterByCriticalViolations(int criticalViolations){
        isTextFilterOn = true;
        if(!isHazardLevelFilterOn){
            Toast.makeText(this,"Hazard filter is off",Toast.LENGTH_SHORT).show();
            filteredList.clear();
            for(Restaurant r : manager.getRestaurantList()){
                int numOfCriticalViolationsFound = 0;
                for(Inspection i : r.getInspections()){
                    String dateOfInspection = i.getInspectionDate();
                    DateCalculations dc = new DateCalculations();
                    int numOfDays = dc.daysInBetween(dateOfInspection);
                    if(numOfDays <= 365){
                        numOfCriticalViolationsFound += i.getNumCritical();
                    }
                    else{
                        break;
                    }
                }
                if(numOfCriticalViolationsFound >= criticalViolations){
                    Log.d("ListActivity",r.getName() + " : " + numOfCriticalViolationsFound);
                    filteredList.add(r);
                }
            }

        }
        else{
            Toast.makeText(this,"Hazard filter is on",Toast.LENGTH_SHORT).show();
            for(int i = 0; i < filteredList.size();i++){
                Restaurant r = filteredList.get(i);
                int numOfCriticFound = 0;
                for(int j = 0;j < r.getInspections().size();j++){
                    Inspection ins = r.getInspections().get(j);
                    String dateOfInsp = ins.getInspectionDate();
                    DateCalculations dc = new DateCalculations();
                    int numOfDays = dc.daysInBetween(dateOfInsp);
                    if(numOfDays <= 365){
                        numOfCriticFound += ins.getNumCritical();
                    }
                    else{
                        break;
                    }
                }
                if(numOfCriticFound > criticalViolations){
                    //filteredList.remove(i);
                    filteredList2.add(r);
                }
            }

        }

        setUpRestaurantsRecylerView(false);

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
            if(isOneFilterOn){
                restaurantsAdapter = new RestaurantsAdapter(filteredList, this, this);
                restaurantsRecyclerView.setAdapter(restaurantsAdapter);
                isOneFilterOn = false;
            }

            restaurantsAdapter = new RestaurantsAdapter(filteredList2, this, this);
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
        // Check to see if the other filter is on
        isOneFilterOn = true;
        TextView filterText = findViewById(R.id.filterInput);
        String content = filterText.getText().toString();
        isHazardLevelFilterOn = true;
        if(content.isEmpty()){
            isTextFilterOn = false;
        }
        else{
            isTextFilterOn = true;
        }
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
        if(!isTextFilterOn){
            Toast.makeText(this,"No Text filter found", Toast.LENGTH_SHORT).show();
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
        else{
            Toast.makeText(this,"Text filter found", Toast.LENGTH_SHORT).show();
            for(int i = 0; i < filteredList.size(); i++){
                Restaurant restaurantInQuestion = filteredList.get(i);
                if(restaurantInQuestion.getInspections().isEmpty()){
                    filteredList.remove(i);
                }
                else{
                    Inspection mostRecentInspection = restaurantInQuestion.getInspections().get(0);
                    if(mostRecentInspection.getHazardRating().equals(hazardLevel)){
                        filteredList.remove(i);
                    }
                    else{
                        Log.d("ListActivity"," We are keeping" + mostRecentInspection.getHazardRating());
                    }
                }
            }
        }

    }


}

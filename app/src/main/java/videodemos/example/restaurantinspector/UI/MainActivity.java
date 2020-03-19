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
import android.widget.ImageButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;

import videodemos.example.restaurantinspector.Model.DBAdapter;
import videodemos.example.restaurantinspector.Model.DataHandling.DateCalculations;
import videodemos.example.restaurantinspector.Model.DataHandling.Inspection;
import videodemos.example.restaurantinspector.Model.DataHandling.Restaurant;
import videodemos.example.restaurantinspector.Model.RestaurantManager;
import videodemos.example.restaurantinspector.Model.Network.HttpHandler;
import videodemos.example.restaurantinspector.R;

/**
 * Main Activity displays all the restaurants.
 */

public class MainActivity extends AppCompatActivity implements RestaurantsAdapter.OnRestaurantListener {

    private final String RESTAURANT_URL = "https://data.surrey.ca/api/3/action/package_show?id=restaurants";
    private final String INSPECTION_URL = "https://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";
    private final int HOURS_FOR_UPDATE = 300000000;

    private RestaurantManager manager;
    private DBAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadDB();

        setupToolbar();
        setupRestaurantManager();
        //loadInspections();
        setUpRestaurantsRecylerView();

        checkForNewServerData();

    }

    private void loadDB() {
        dbAdapter = new DBAdapter(this);
        dbAdapter.open();
        readFromCSV();
        dbAdapter.close();
    }

    private void readFromCSV() {
        InputStream is = this.getResources().openRawResource(R.raw.restaurants);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        String line = "";
        try {

            final int TRACKING_NUM_INDEX = 0;
            final int SET_NAME_INDEX = 1;
            final int SET_PHYSICAL_ADDRESS = 2;
            final int SET_PHYSICALCITY = 3;
            final int SET_FACT_TYPE = 4;
            final int SET_LATITUDE_TYPE = 5;
            final int SET_LONGITUDE= 6;

            // Step over headers
            reader.readLine();
            while ((line = reader.readLine()) != null) {

                // Split by ','

                String[] tokens = line.split(",");

                // Read the data
                String trackingNum = tokens[TRACKING_NUM_INDEX].
                        replace("\"", "");

                String name = tokens[SET_NAME_INDEX].
                        replace("\"", "");

                String address = tokens[SET_PHYSICAL_ADDRESS].
                        replace("\"", "");

                String city = tokens[SET_PHYSICALCITY].
                        replace("\"", "");

                String type = tokens[SET_FACT_TYPE].
                        replace("\"", "");

                double latitude = 0;
                if (tokens[SET_LATITUDE_TYPE].length() > 0) {
                    latitude = Double.parseDouble(tokens[SET_LATITUDE_TYPE]);
                }

                double longitude = 0;
                if (tokens[SET_LONGITUDE].length() > 0) {
                    longitude = Double.parseDouble(tokens[SET_LONGITUDE]);
                }

                dbAdapter.insertRow(trackingNum, name, address, city, type, latitude, longitude);

            }
        } catch (IOException e) {
            Log.wtf("My Activity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }
    }

    private void loadInspections() {
        dbAdapter.open();
        loadInspectionsCSVToDB();
        dbAdapter.close();
    }

    public void loadInspectionsCSVToDB() {

        InputStream is = getResources().openRawResource(R.raw.inspectionreports);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        String line = "";
        try {

            final int TRACKING_NUM_INDEX = 1;
            final int DATE_INDEX = 2;
            final int INSPECTION_TYPE_INDEX = 3;
            final int CRITICAL_INDEX = 4;
            final int HAZARD_INDEX = 5;
            final int VIOLATIONS_LUMP_INDEX = 7;


            // Step over headers
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                Inspection inspection = new Inspection();

                // Even newer splitting method
                //NDAA-9DNQLJ,20180924,Follow-Up,0,0,,Low
                //HCAR-BKDQCL,20200127,Routine,1,0,"302,Critical,Equipment/utensils/food contact surfaces not properly washed and sanitized [s. 17(2)],Not Repeat",Low
                String [] tokens = line.split("\"");

                String [] tokensFirstHalf = tokens[0].split(",");

                if (tokensFirstHalf.length == 0){
                    continue;
                }
                String trackingNum = tokensFirstHalf[0];

                if(tokens.length > 1){
                    for(Restaurant r : manager.getRestaurantList()){
                        if(r.getTrackingNumber().equals(trackingNum)){
                            String datetoAdd = tokensFirstHalf[1];

                            String inspectionType = tokensFirstHalf[2];

                            int numOfCritical = Integer.parseInt(tokensFirstHalf[3]);

                            int numOfNonCritical = Integer.parseInt(tokensFirstHalf[4]);

                            //not sure how to store the violation array in the database
                            if(numOfCritical + numOfNonCritical != 0){
                                String[] violations = tokens[1].split("\\|");
                                for(String violationPossibility: violations){
                                    String violation = violationPossibility.substring(0, 3);
                                    inspection.addViolation(Integer.parseInt(violation));
                                }
                            }

                            String hazardLevel = tokens[2];
                            if(hazardLevel.length() > 1){
                                inspection.setHazardRating(hazardLevel);
                            }
                            else{
                                inspection.setHazardRating("Low");
                            }


                            r.addInspection(inspection);
                            break;
                        }

                    }
                }
                else{

                    // NDAA-8RNNVR,20181017,Routine,0,0,,Low
                    String [] tokensSplit = tokens[0].split(",");
                    trackingNum = tokensSplit[0];

                    for(Restaurant r : manager.getRestaurantList()){
                        if(r.getTrackingNumber().equals(trackingNum)){
                            String dateToAdd = tokensSplit[1];
                            inspection.setInspectionDate(dateToAdd);

                            String inspectionType = tokensSplit[2];
                            inspection.setInspType(inspectionType);

                            inspection.setNumNonCritical(Integer.parseInt(tokensSplit[3]));
                            inspection.setNumCritical(Integer.parseInt(tokensSplit[4]));
                            if (tokensSplit.length == 7){
                                inspection.setHazardRating(tokensSplit[6]);
                            } else {
                                //csv has no hazard rating
                                inspection.setHazardRating("Low");
                            }


                            r.addInspection(inspection);
                            break;

                        }
                    }

                }

            }

        } catch (Exception e) {
            Log.wtf("My Activity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }

    }


    private void checkForNewServerData() {
        Thread gettingDataThread = new Thread(new Runnable(){
            public void run(){
                try{
                    HttpHandler httpHandler = new HttpHandler(RESTAURANT_URL);
                    String latestDate = httpHandler.getCurrentDateFromServer();
                    if (isNewDataAvailable(latestDate)){
                        // ask user in dialog

                        // update database

                        Log.d("DATEWORKS", "Nice");
                    }else {
                        Log.d("DATEWORKS", "Nah");
                    }

                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        gettingDataThread.start();
    }

    public boolean isNewDataAvailable(String latestDate){
        DateCalculations dateCalculations = new DateCalculations();

        return dateCalculations.hoursInBetween(latestDate) >= HOURS_FOR_UPDATE;
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

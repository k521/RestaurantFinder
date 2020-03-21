package videodemos.example.restaurantinspector.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import videodemos.example.restaurantinspector.Model.DBAdapter;
import videodemos.example.restaurantinspector.Model.DataHandling.DateCalculations;
import videodemos.example.restaurantinspector.Model.RestaurantManager;
import videodemos.example.restaurantinspector.Model.Network.HttpHandler;
import videodemos.example.restaurantinspector.R;

/**
 * Main Activity displays all the restaurants.
 */

public class MainActivity extends AppCompatActivity implements RestaurantsAdapter.OnRestaurantListener {

    private final String RESTAURANT_URL = "https://data.surrey.ca/api/3/action/package_show?id=restaurants";
    private final String INSPECTION_URL = "https://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";
    private final int HOURS_FOR_UPDATE = 1;
    private final String PREFERENCES = "data";
    private final String TAG_UPDATE_DATE = "last_update_date";
    private final String TAG_SERVER_METADATA_DATE = "last_server_date";
    private final String TAG_DEFAULT_DATA = "default_data";

    private RestaurantManager manager;
    private DBAdapter dbAdapter = new DBAdapter(this);

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("A" , "STARTED");


        // First check the dates. If there is a new update then proceed as follows


        checkIfTimeToUpdate();
        
        setupToolbar();
        setupRestaurantManager();

        setUpRestaurantsRecylerView();





    }

    private boolean isDataDefaultVersion(){
        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        return preferences.getBoolean(TAG_DEFAULT_DATA, true);
    }

    private void checkIfTimeToUpdate() {

        checkForNewServerData();


//        Log.d("DEBUG:", "BEFORE");
//        if (isTimeForUpdate(getLastUpdateDate())){
//            Log.d("DEBUG:", "Time to update!");
//            checkForNewServerData();
//        }
    }

    private void printViolationDb() {
        dbAdapter.open();

        Cursor cursor = dbAdapter.getAllViolationRows();

        if(cursor.moveToFirst()){
            do{

                Log.d("Violation:" ,cursor.getString(DBAdapter.COL_TRACKING_NUMBER_VIOLATION)
                        + cursor.getString(DBAdapter.COL_VIOLATION_DATE) + cursor.getString(DBAdapter.COL_VIOLATION_CODE));

            }while(cursor.moveToNext());
        }
        cursor.close();
        dbAdapter.close();
    }

    private void loadCSVsFromServer() {
        loadRestaurantsFromServer();
        loadInspectionsFromServer();
    }

    private void loadDBFromCSV() {
        //dbAdapter = new DBAdapter(this);
        dbAdapter.open();
        readFromCSV();
        loadInspectionsCSVToDB();
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
                // Even newer splitting method
                String[] tokens = line.split("\"");
                String trackingNum = tokens[TRACKING_NUM_INDEX];

                String dateToAdd = tokens[DATE_INDEX];
                dateToAdd = dateToAdd.substring(1, dateToAdd.length() - 1);


                String inspectionType = tokens[INSPECTION_TYPE_INDEX];


                String valuesForCritical = tokens[CRITICAL_INDEX];
                valuesForCritical = valuesForCritical.replaceAll(",","");

                char charNumNonCrit = valuesForCritical.charAt(0);
                String numNonCrit = Character.toString(charNumNonCrit);

                char charNumCrit = valuesForCritical.charAt(1);
                String numCrit = Character.toString(charNumCrit);


                String hazardRating = tokens[HAZARD_INDEX];

                if(Integer.parseInt(numNonCrit) + Integer.parseInt(numCrit) > 0){
                    String[] violations = tokens[VIOLATIONS_LUMP_INDEX].split("\\|");

                    for(String violationPossibility: violations){
                        String violation = violationPossibility.substring(0, 3);
                        dbAdapter.insertViolationRow(trackingNum, dateToAdd, Integer.parseInt(violation));
                    }

                }

                dbAdapter.insertRow(trackingNum, hazardRating, dateToAdd, inspectionType, Integer.parseInt(numCrit), Integer.parseInt(numNonCrit));

            }

        } catch (Exception e) {
            Log.wtf("My Activity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }


    }

    public void loadInspectionsFromServer() {

        String line = "";
        try {

            HttpHandler httpHandler = new HttpHandler(INSPECTION_URL);
            httpHandler.getData();
            String body = httpHandler.getBody();

            Log.d("I_BODY:", "" + body.length());

            File outputFile = new File("raw\\inspectionreports.csv");
            PrintWriter writer = new PrintWriter(outputFile);
            writer.write(body);
            writer.close();


        } catch (Exception e) {
            Log.wtf("My Activity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }


        Log.d("ENDWHILE", "ENDED");

    }

    public void loadRestaurantsFromServer() {

        String line = "";
        try {
            HttpHandler httpHandler = new HttpHandler(RESTAURANT_URL);
            httpHandler.getData();
            String body = httpHandler.getBody();

            Log.d("R_BODY:", "" + body.length());

            
            File outputFile = new File("raw\\restaurants.csv");
            PrintWriter writer = new PrintWriter(outputFile);
            writer.write(body);
            writer.close();

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
                    String lastServerDate = getLastServerDate();
                    Log.d("CHECKDATES:", "Server Date: " + lastServerDate + " Latest Date: " + latestDate);
                    if (isNewDataAvailable(lastServerDate, latestDate)){
                        // ask user in dialog


                        // update database
                        Log.d("DEBUG:", "Let's update!");
                        loadCSVsFromServer();

                        DateTime dateTime = new DateTime();
                        updateLastUpdateDate(dateTime.toString());
                        updateLastServerDate(latestDate);

                        finish();
                        startActivity(getIntent());
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

    private void updateLastUpdateDate(String newUpdateDate){
        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(TAG_UPDATE_DATE, newUpdateDate);
        editor.apply();
    }

    private String getLastServerDate(){
        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        String date = preferences.getString(TAG_SERVER_METADATA_DATE, "empty");
        return date;
    }

    private void updateLastServerDate(String newServerDate){
        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(TAG_SERVER_METADATA_DATE, newServerDate);
        editor.apply();
    }

    private String getLastUpdateDate(){
        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);

        String date = preferences.getString(TAG_UPDATE_DATE, "");
        //Toast.makeText(this, "SP Date: " + date, Toast.LENGTH_LONG).show();
        return date;
    }

    public boolean isNewDataAvailable(String lastUpdateDate, String serverDate){
        return !lastUpdateDate.equals(serverDate);
    }


    public boolean isTimeForUpdate(String latestDate){
        DateCalculations dateCalculations = new DateCalculations();


        Boolean hasPassed = dateCalculations.secondsInBetween(latestDate) >= HOURS_FOR_UPDATE;

        Log.d("latestDate:", latestDate);

        return dateCalculations.secondsInBetween(latestDate) >= HOURS_FOR_UPDATE;
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
        if (isDataDefaultVersion()){
            // Call old method for csv parsing
            manager.readRestaurantFromOldCSV(this);
            manager.readInspectionsFromOldCSV(this);
        } else {
            // Call new method for csv parsing
            manager.readRestaurantFromNewCSV(this);
            manager.readInspectionsFromNewCSV(this);
        }

        manager.sortByRestaurantName();
        manager.sortInspections();
    }

    private void setUpRestaurantsRecylerView() {
        RecyclerView restaurantsRecyclerView = findViewById(R.id.rv_restaurant_list);

        restaurantsRecyclerView.setHasFixedSize(true);
        restaurantsRecyclerView.setItemViewCacheSize(20);
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

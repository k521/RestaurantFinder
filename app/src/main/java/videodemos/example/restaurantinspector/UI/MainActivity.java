package videodemos.example.restaurantinspector.UI;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import videodemos.example.restaurantinspector.Model.DBAdapter;
import videodemos.example.restaurantinspector.Model.DataHandling.DateCalculations;
import videodemos.example.restaurantinspector.Model.Network.HttpHandler;
import videodemos.example.restaurantinspector.Model.RestaurantManager;
import videodemos.example.restaurantinspector.R;

/**
 * Main Activity displays all the restaurants.
 */

public class MainActivity extends AppCompatActivity implements RestaurantsAdapter.OnRestaurantListener {

    private final String RESTAURANT_URL = "https://data.surrey.ca/api/3/action/package_show?id=restaurants";
    private final String INSPECTION_URL = "https://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";
    private final int HOURS_FOR_UPDATE = 20;
    private final String PREFERENCES = "data";
    private final String TAG_UPDATE_DATE = "last_update_date";
    private final String TAG_SERVER_METADATA_DATE = "last_server_date";
    private final String TAG_DEFAULT_DATA = "default_data";
    private final String TAG_DIALOG = "new data dialog tag";

    private RestaurantManager manager;
    private DBAdapter dbAdapter = new DBAdapter(this);

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("A" , "STARTED");

        checkIfTimeToUpdate();

        setupToolbar();
        setupRestaurantManager();

        setUpRestaurantsRecylerView();


        if( isServicesOK() ){ //checks for functioning Google Services
            setupMapButton();
        }

    }


    private boolean isDataDefaultVersion(){
        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        return preferences.getBoolean(TAG_DEFAULT_DATA, true);
    }

    private void setDataDefaultVersionToFalse(){
        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(TAG_DEFAULT_DATA, false);
        editor.apply();
        Log.d("Updated:", "DEFAULT DATA UPDATED");
    }

    private void checkIfTimeToUpdate() {
        if (isTimeForUpdate(getLastUpdateDate())){
            checkForNewServerData();
        }
    }

    private void loadCSVsFromServer() {
        loadRestaurantsFromServer();
        loadInspectionsFromServer();
    }

    public String loadInspectionsFromServer() {

        String line = "";
        try {

            HttpHandler httpHandler = new HttpHandler(INSPECTION_URL);
            httpHandler.getData();
            String body = httpHandler.getBody();

            return body;


        } catch (Exception e) {
            Log.wtf("My Activity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }

        return null;

    }

    public String loadRestaurantsFromServer() {

        String line = "";
        try {
            HttpHandler httpHandler = new HttpHandler(RESTAURANT_URL);
            httpHandler.getData();
            String body = httpHandler.getBody();
            return body;

        } catch (Exception e) {
            Log.wtf("My Activity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }

        return "";
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
                        FragmentManager manager = getSupportFragmentManager();
                        NewDataFragment dialog = new NewDataFragment(latestDate);
                        dialog.show(manager, TAG_DIALOG);
                    }

                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        gettingDataThread.start();
    }

    public void updateDataAndRefresh(String latestDate) {
        WaitTask waitTask = new WaitTask(this, latestDate);
        waitTask.execute();
    }

    public void updateFiles(String restaurantBody, String inspectionsBody, String latestDate){
        setDataDefaultVersionToFalse();
        DateTime dateTime = new DateTime();
        updateLastUpdateDate(dateTime.toString());
        updateLastServerDate(latestDate);
        writeDataOnCsvFiles(restaurantBody, inspectionsBody);
        finish();
        startActivity(getIntent());
    }

    public String[] getBodiesFromServer(){
        loadCSVsFromServer();
        String restaurantBody = loadRestaurantsFromServer();
        String inspectionsBody = loadInspectionsFromServer();

        String[] bodies = {restaurantBody, inspectionsBody};

        return bodies;
    }


    private void writeDataOnCsvFiles(String restaurantBody, String inspectionsBody) {

        try{
            File path = getFilesDir();
            File file = new File(path, "restaurants.csv");
            PrintWriter writer = new PrintWriter(file);
            writer.write(restaurantBody);
            writer.close();

            path = getFilesDir();
            file = new File(path, "inspectionreports.csv");
            writer = new PrintWriter(file);
            writer.write(inspectionsBody);
            writer.close();

        } catch (IOException e){
            e.printStackTrace();
        }

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
        Log.d("LastUpdate:", " " + date);
        return date;
    }

    public boolean isNewDataAvailable(String lastUpdateDate, String serverDate){
        return !lastUpdateDate.equals(serverDate);
    }


    public boolean isTimeForUpdate(String latestDate){
        if (latestDate.isEmpty()){
            DateTime today = new DateTime();
            updateLastUpdateDate(today.toString());
            return false;
        }

        DateCalculations dateCalculations = new DateCalculations();


        Boolean hasPassed = dateCalculations.secondsInBetween(latestDate) >= HOURS_FOR_UPDATE;

        Log.d("latestDate:", latestDate);

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

    private static final String TAG = "MainActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void setupMapButton() {

        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent (MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });


    }


}

package videodemos.example.restaurantinspector.Model;

import android.content.SharedPreferences;

public class DataDownloader {
    private final String RESTAURANT_URL = "https://data.surrey.ca/api/3/action/package_show?id=restaurants";
    private final String INSPECTION_URL = "https://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";
    private final int HOURS_FOR_UPDATE = 1;
    private final String PREFERENCES = "data";
    private final String TAG_UPDATE_DATE = "last_update_date";
    private final String TAG_SERVER_METADATA_DATE = "last_server_date";
    private final String TAG_DEFAULT_DATA = "default_data";

    private SharedPreferences preferences;



}

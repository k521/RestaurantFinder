package videodemos.example.restaurantinspector.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import videodemos.example.restaurantinspector.Model.DataHandling.Inspection;
import videodemos.example.restaurantinspector.Model.DataHandling.Restaurant;
import videodemos.example.restaurantinspector.Model.Network.HttpHandler;
import videodemos.example.restaurantinspector.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * A class that holds and loads the list of restaurants.
 */

public class RestaurantManager {

    public static RestaurantManager instance;

    public static RestaurantManager getInstance() {
        if (instance == null) {
            instance = new RestaurantManager();
        }

        return instance;
    }

    private final String PREFERENCES = "data";
    private final String TAG_TRACKING_NUMBER_LIST = "list of tracking numbers";

    private List<Restaurant> restaurantList = new ArrayList<>();
    private HashMap<String, Inspection> favouritesMap = new HashMap<>();
    private SharedPreferences preferences;


    private RestaurantManager() {
    }

    public List<Restaurant> getRestaurantList() {
        return restaurantList;
    }

    public Restaurant getRestaurant(int index) {
        return restaurantList.get(index);
    }

    public void insertIntoFavouritesMap(String trackingNumber, Inspection latestInspection) {
        favouritesMap.put(trackingNumber, latestInspection);

    }

    public HashMap<String, Inspection> getFavouritesMap() {
        return favouritesMap;
    }

    public boolean isFavouritesMapEmpty() {
        return favouritesMap.isEmpty();
    }

    public void clearFavMap() {
        favouritesMap.clear();
    }

    public void readRestaurantFromNewCSV(Context c) {
        restaurantList = new ArrayList<>();

        File path = c.getFilesDir();
        File file = new File(path, "restaurants.csv");

        String line = "";
        try {

            final int TRACKING_NUM_INDEX = 0;
            final int SET_NAME_INDEX = 1;
            final int SET_PHYSICAL_ADDRESS = 2;
            final int SET_PHYSICALCITY = 3;
            final int SET_FACT_TYPE = 4;
            final int SET_LATITUDE_TYPE = 5;
            final int SET_LONGITUDE = 6;

            int length = (int) file.length();
            byte[] bytes = new byte[length];

            FileInputStream in = new FileInputStream(file);
            try {
                in.read(bytes);
            } finally {
                in.close();
            }

            InputStream is = new ByteArrayInputStream(bytes);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );

            // Step over headers
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                // Split by ','

                Restaurant restaurant = new Restaurant();

                String[] tokensEdge = line.split("\"");
                if (tokensEdge.length > 1) {
                    String trackingNum = tokensEdge[0].replaceAll(",", "");
                    restaurant.setTrackingNumber(trackingNum);
                    restaurant.setName(tokensEdge[1]);

                    String[] tokensEdgeRest = tokensEdge[2].split(",");

                    restaurant.setPhysicalAddress(tokensEdgeRest[1]);

                    restaurant.setPhysicalCity(tokensEdgeRest[2]);

                    restaurant.setFactype(tokensEdgeRest[3]);

                    restaurant.setLatitude(Double.parseDouble(tokensEdgeRest[4]));

                    restaurant.setLatitude(Double.parseDouble(tokensEdgeRest[5]));
                } else {
                    String[] tokens = line.split(",");

                    // Read the data

                    // We first try to see if our name has commas in it

                    restaurant.setTrackingNumber(tokens[TRACKING_NUM_INDEX].
                            replace("\"", ""));

                    restaurant.setName(tokens[SET_NAME_INDEX].
                            replace("\"", ""));

                    restaurant.setPhysicalAddress(tokens[SET_PHYSICAL_ADDRESS].
                            replace("\"", ""));

                    restaurant.setPhysicalCity(tokens[SET_PHYSICALCITY].
                            replace("\"", ""));

                    restaurant.setFactype(tokens[SET_FACT_TYPE].
                            replace("\"", ""));

                    if (tokens[SET_LATITUDE_TYPE].length() > 0) {
                        restaurant.setLatitude(Double.parseDouble(tokens[SET_LATITUDE_TYPE]));
                    } else {
                        restaurant.setLatitude(0);
                    }

                    if (tokens[SET_LONGITUDE].length() > 0) {
                        restaurant.setLongitude(Double.parseDouble(tokens[SET_LONGITUDE]));
                    } else {
                        restaurant.setLongitude(0);
                    }
                }

                String favourites = getFavouriteRestaurantsTrackingNumbers(c);
                if (favourites.contains(restaurant.getTrackingNumber())) {
                    restaurant.setFavourite(true);
                }
                restaurantList.add(restaurant);

            }
        } catch (IOException e) {
            Log.wtf("My Activity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }
    }

    public void readInspectionsFromNewCSV(Context c) {
        File path = c.getFilesDir();
        File file = new File(path, "inspectionreports.csv");

        String line = "";
        try {
            int length = (int) file.length();

            byte[] bytes = new byte[length];

            FileInputStream in = new FileInputStream(file);
            try {
                in.read(bytes);
            } finally {
                in.close();
            }

            InputStream is = new ByteArrayInputStream(bytes);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );

            // Step over headers
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                Inspection inspection = new Inspection();

                // Even newer splitting method
                String[] tokens = line.split("\"");

                String[] tokensFirstHalf = tokens[0].split(",");

                if (tokensFirstHalf.length == 0) {
                    continue;
                }
                String trackingNum = tokensFirstHalf[0];

                if (tokens.length > 1) {
                    for (Restaurant r : restaurantList) {
                        if (r.getTrackingNumber().equals(trackingNum)) {
                            String datetoAdd = tokensFirstHalf[1];
                            inspection.setInspectionDate(datetoAdd);

                            String inspectionType = tokensFirstHalf[2];
                            inspection.setInspType(inspectionType);

                            int numOfCritical = Integer.parseInt(tokensFirstHalf[3]);
                            inspection.setNumCritical(numOfCritical);

                            int numOfNonCritical = Integer.parseInt(tokensFirstHalf[4]);
                            inspection.setNumNonCritical(numOfNonCritical);

                            if (numOfCritical + numOfNonCritical != 0) {
                                String[] violations = tokens[1].split("\\|");
                                for (String violationPossibility : violations) {
                                    String violation = violationPossibility.substring(0, 3);
                                    inspection.addViolation(Integer.parseInt(violation));
                                }
                            }

                            String hazardLevel = tokens[2];
                            hazardLevel = hazardLevel.replace(",", "");
                            if (hazardLevel.length() > 1) {
                                inspection.setHazardRating(hazardLevel);
                            } else {
                                inspection.setHazardRating("Low");
                            }

                            r.addInspection(inspection);
                            break;
                        }

                    }
                } else {
                    String[] tokensSplit = tokens[0].split(",");
                    trackingNum = tokensSplit[0];

                    for (Restaurant r : restaurantList) {
                        if (r.getTrackingNumber().equals(trackingNum)) {
                            String dateToAdd = tokensSplit[1];
                            inspection.setInspectionDate(dateToAdd);

                            String inspectionType = tokensSplit[2];
                            inspection.setInspType(inspectionType);

                            inspection.setNumNonCritical(Integer.parseInt(tokensSplit[3]));
                            inspection.setNumCritical(Integer.parseInt(tokensSplit[4]));
                            if (tokensSplit.length == 7) {
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


    public void readRestaurantFromOldCSV(Context c) {
        InputStream is = c.getResources().openRawResource(R.raw.restaurants);
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
            final int SET_LONGITUDE = 6;

            // Step over headers
            reader.readLine();
            while ((line = reader.readLine()) != null) {

                // Split by ','

                String[] tokens = line.split(",");

                // Read the data

                Restaurant restaurant = new Restaurant();
                restaurant.setTrackingNumber(tokens[TRACKING_NUM_INDEX].
                        replace("\"", ""));

                restaurant.setName(tokens[SET_NAME_INDEX].
                        replace("\"", ""));

                restaurant.setPhysicalAddress(tokens[SET_PHYSICAL_ADDRESS].
                        replace("\"", ""));

                restaurant.setPhysicalCity(tokens[SET_PHYSICALCITY].
                        replace("\"", ""));

                restaurant.setFactype(tokens[SET_FACT_TYPE].
                        replace("\"", ""));

                if (tokens[SET_LATITUDE_TYPE].length() > 0) {
                    restaurant.setLatitude(Double.parseDouble(tokens[SET_LATITUDE_TYPE]));
                } else {
                    restaurant.setLatitude(0);
                }

                if (tokens[SET_LONGITUDE].length() > 0) {
                    restaurant.setLongitude(Double.parseDouble(tokens[SET_LONGITUDE]));
                } else {
                    restaurant.setLongitude(0);
                }

                String favourites = getFavouriteRestaurantsTrackingNumbers(c);
                if (favourites.contains(restaurant.getTrackingNumber())) {
                    restaurant.setFavourite(true);
                }
                restaurantList.add(restaurant);

            }
        } catch (IOException e) {
            Log.wtf("My Activity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }
    }

    public void readInspectionsFromOldCSV(Context c) {
        InputStream is = c.getResources().openRawResource(R.raw.inspectionreports);
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
                String[] tokens = line.split("\"");
                String trackingNum = tokens[TRACKING_NUM_INDEX];
                for (Restaurant r : restaurantList) {
                    if (r.getTrackingNumber().equals(trackingNum)) {
                        String dateToAdd = tokens[DATE_INDEX];
                        dateToAdd = dateToAdd.substring(1, dateToAdd.length() - 1);
                        inspection.setInspectionDate(dateToAdd);

                        String inspectionType = tokens[INSPECTION_TYPE_INDEX];
                        inspection.setInspType(inspectionType);

                        String valuesForCritical = tokens[CRITICAL_INDEX];
                        valuesForCritical = valuesForCritical.replaceAll(",", "");

                        char charNumNonCrit = valuesForCritical.charAt(0);
                        String numNonCrit = Character.toString(charNumNonCrit);

                        char charNumCrit = valuesForCritical.charAt(1);
                        String numCrit = Character.toString(charNumCrit);

                        inspection.setNumNonCritical(Integer.parseInt(numNonCrit));
                        inspection.setNumCritical(Integer.parseInt(numCrit));

                        String hazardRating = tokens[HAZARD_INDEX];
                        inspection.setHazardRating(hazardRating);

                        if (Integer.parseInt(numNonCrit) + Integer.parseInt(numCrit) > 0) {
                            String[] violations = tokens[VIOLATIONS_LUMP_INDEX].split("\\|");

                            for (String violationPossibility : violations) {
                                String violation = violationPossibility.substring(0, 3);
                                inspection.addViolation(Integer.parseInt(violation));
                            }

                        }

                        r.addInspection(inspection);

                    }
                }

            }

        } catch (Exception e) {
            Log.wtf("My Activity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }

    }


    public void removeNonNewRestaurantInspections() {
        for (Restaurant r : restaurantList) {
            String key = r.getTrackingNumber();
            if (favouritesMap.containsKey(key)) {
                Inspection latestInspectionFromOldData = favouritesMap.get(key);
                Inspection latestInspectionFromNewData = r.getInspections().get(0);

                try {
                    if (latestInspectionFromOldData.getInspectionDate().equals(latestInspectionFromNewData.getInspectionDate())) {
                        favouritesMap.remove(key);
                    }
                } catch (NullPointerException e) {
                    Log.d("Exception", "Null Inspection");
                }

            }
        }
    }


    public void sortByRestaurantName() {
        Comparator<Restaurant> comparatorName = new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant r1, Restaurant r2) {
                return r1.getName().compareTo(r2.getName());
            }
        };

        Collections.sort(restaurantList, comparatorName);
    }

    public void sortInspections() {
        for (Restaurant r : restaurantList) {
            r.sortByInspectionDate();
        }
    }

    private String getFavouriteRestaurantsTrackingNumbers(Context c) {
        preferences = c.getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        return preferences.getString(TAG_TRACKING_NUMBER_LIST, "");
    }

}

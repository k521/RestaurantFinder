package videodemos.example.restaurantinspector.Model;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import videodemos.example.restaurantinspector.Model.DataHandling.Inspection;
import videodemos.example.restaurantinspector.Model.DataHandling.Restaurant;
import videodemos.example.restaurantinspector.Model.Network.HttpHandler;

/**
 * A class that holds and loads the list of restaurants.
 */

public class RestaurantManager {

    private final String RESTAURANT_URL = "https://data.surrey.ca/api/3/action/package_show?id=restaurants";
    private final String INSPECTION_URL = "https://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";
    private final String RESTAURANT_CSV_URL = "https://data.surrey.ca/dataset/3c8cb648-0e80-4659-9078-ef4917b90ffb/resource/0e5d04a2-be9b-40fe-8de2-e88362ea916b/download/restaurants.csv";
    private final String INSPECTIONS_CSV_URL = "https://data.surrey.ca/dataset/948e994d-74f5-41a2-b3cb-33fa6a98aa96/resource/30b38b66-649f-4507-a632-d5f6f5fe87f1/download/fraserhealthrestaurantinspectionreports.csv";

    public static RestaurantManager instance;
    private List<Restaurant> restaurantList = new ArrayList<>();
    private DBAdapter dbAdapter;


    public static RestaurantManager getInstance(Context c) {
        if (instance == null) {
            instance = new RestaurantManager(c);
        }

        return instance;
    }

    public List<Restaurant> getRestaurantList() {
        return restaurantList;
    }

    public Restaurant getRestaurant(int index) {
        return restaurantList.get(index);
    }

    private RestaurantManager(Context c) {
       readFromCSV(c);
        sortByRestaurantName();
    }

    public void readFromCSV(Context c) {

//        InputStream is = c.getResources().openRawResource(R.raw.restaurants1);
//        BufferedReader reader = new BufferedReader(
//                new InputStreamReader(is, Charset.forName("UTF-8"))
//        );
        dbAdapter = new DBAdapter(c);
        dbAdapter.open();
        Cursor cursor = dbAdapter.getAllRestaurantRows();
        if(cursor.moveToFirst()){
            do{
                Restaurant restaurant = new Restaurant();
                restaurant.setTrackingNumber(cursor.getString(DBAdapter.COL_TRACKING_NUMBER));
                restaurant.setName(cursor.getString(DBAdapter.COL_RESTAURANT_NAME));
                restaurant.setFactype(cursor.getString(DBAdapter.COL_FAC_TYPE));
                restaurant.setLatitude(cursor.getDouble(DBAdapter.COL_LATITUDE));
                restaurant.setLongitude(cursor.getDouble(DBAdapter.COL_LONGITUDE));
                restaurant.setPhysicalAddress(cursor.getString(DBAdapter.COL_ADDRESS));
                restaurant.setPhysicalCity(cursor.getString(DBAdapter.COL_CITY));

                Log.d("ADD_INGRESTAURANT", cursor.getString(DBAdapter.COL_TRACKING_NUMBER));
                restaurantList.add(restaurant);
            }while (cursor.moveToNext());
        }
        Log.d("FINISHED_RESTAURANTS", "FINISHED");
        dbAdapter.close();
//
//        String line = "";
//        try {
//            HttpHandler httpHandler = new HttpHandler(RESTAURANT_URL);
//            httpHandler.getData();
//            String body = httpHandler.getBody();
//
//            BufferedReader reader = new BufferedReader(new StringReader(body));
//            final int TRACKING_NUM_INDEX = 0;
//            final int SET_NAME_INDEX = 1;
//            final int SET_PHYSICAL_ADDRESS = 2;
//            final int SET_PHYSICALCITY = 3;
//            final int SET_FACT_TYPE = 4;
//            final int SET_LATITUDE_TYPE = 5;
//            final int SET_LONGITUDE= 6;
//
//           // SWOD-AG5UGV,"Green Indian Cuisine, Pizza & Sweets",12565 88 Ave,Surrey,Restaurant,49.16401631,-122.8747815
//
//
//
//            // Step over headers
//            reader.readLine();
//            while ((line = reader.readLine()) != null) {
//
//                // Split by ','
//
//                Restaurant restaurant = new Restaurant();
//
//                String []tokensEdge = line.split("\"");
//                if(tokensEdge.length > 1) {
//                    String trackingNum = tokensEdge[0].replaceAll(",", "");
//                    restaurant.setTrackingNumber(trackingNum);
//                    restaurant.setName(tokensEdge[1]);
//
//                    String[] tokensEdgeRest = tokensEdge[2].split(",");
//
//                    restaurant.setPhysicalAddress(tokensEdgeRest[1]);
//
//                    restaurant.setPhysicalCity(tokensEdgeRest[2]);
//
//                    restaurant.setFactype(tokensEdgeRest[3]);
//
//                    restaurant.setLatitude(Double.parseDouble(tokensEdgeRest[4]));
//
//                    restaurant.setLatitude(Double.parseDouble(tokensEdgeRest[5]));
//                }
//                else{
//                    String[] tokens = line.split(",");
//
//                    // Read the data
//
//                    // We first try to see if our name has commas in it
//
//                    restaurant.setTrackingNumber(tokens[TRACKING_NUM_INDEX].
//                            replace("\"", ""));
//
//                    restaurant.setName(tokens[SET_NAME_INDEX].
//                            replace("\"", ""));
//
//                    restaurant.setPhysicalAddress(tokens[SET_PHYSICAL_ADDRESS].
//                            replace("\"", ""));
//
//                    restaurant.setPhysicalCity(tokens[SET_PHYSICALCITY].
//                            replace("\"", ""));
//
//                    restaurant.setFactype(tokens[SET_FACT_TYPE].
//                            replace("\"", ""));
//
//                    if (tokens[SET_LATITUDE_TYPE].length() > 0) {
//                        Log.d("RestaurantManager",tokens[SET_LATITUDE_TYPE]);
//                        Log.d("RestaurantManager","Line is " + line);
//                        restaurant.setLatitude(Double.parseDouble(tokens[SET_LATITUDE_TYPE]));
//                    } else {
//                        restaurant.setLatitude(0);
//                    }
//
//                    if (tokens[SET_LONGITUDE].length() > 0) {
//                        restaurant.setLongitude(Double.parseDouble(tokens[SET_LONGITUDE]));
//                    } else {
//                        restaurant.setLongitude(0);
//                    }
//                }
//
//                restaurantList.add(restaurant);
//
//            }
//        } catch (Exception e) {
//            Log.wtf("My Activity", "Error reading data file on line " + line, e);
//            e.printStackTrace();
//        }
    }

    public void InspectionReader(Context c) {


        dbAdapter = new DBAdapter(c);
        dbAdapter.open();
        Cursor cursorInspection = dbAdapter.getAllInspectionRows();
        Cursor cursorViolation = dbAdapter.getAllViolationRows();

        if(cursorInspection.moveToFirst()){
            do{

                Inspection inspection = new Inspection();
                inspection.setHazardRating(cursorInspection.getString(DBAdapter.COL_HAZARD_RATING));
                inspection.setInspectionDate(cursorInspection.getString(DBAdapter.COL_INSPECTION_DATE));
                inspection.setInspType(cursorInspection.getString(DBAdapter.COL_INSP_TYPE));
                inspection.setNumCritical(cursorInspection.getInt(DBAdapter.COL_NUM_CRITICAL));
                inspection.setNumNonCritical(cursorInspection.getInt(DBAdapter.COL_NUM_NON_CRITICAL));

                String trackingNumInspection = cursorInspection.getString(DBAdapter.COL_TRACKING_NUMBER_INSPECTION);
                String inpectionDate = cursorInspection.getString(DBAdapter.COL_INSPECTION_DATE);

                if (cursorViolation.moveToFirst()){
                    do {
                        String trackingNumViolation = cursorInspection.getString(DBAdapter.COL_TRACKING_NUMBER_VIOLATION);
                        String violationDate = cursorInspection.getString(DBAdapter.COL_VIOLATION_DATE);

                        if (trackingNumViolation.equals(trackingNumInspection) && violationDate.equals(inpectionDate)) {
                            inspection.addViolation(cursorViolation.getInt(DBAdapter.COL_VIOLATION_CODE));
                        }
                        Log.d("ADDING_VIOLATIONS", cursorInspection.getString(DBAdapter.COL_TRACKING_NUMBER_VIOLATION));
                    } while (cursorViolation.moveToNext());
                }

                for (Restaurant restaurant : restaurantList){
                    if (restaurant.getTrackingNumber().equals(trackingNumInspection)){
                        restaurant.addInspection(inspection);
                    }
                }

                Log.d("ADDING_INSPECTION", cursorInspection.getString(DBAdapter.COL_INSPECTION_DATE));
            }while (cursorInspection.moveToNext());
        }

        Log.d("FINISHED_INSPECTIONS", ":)");
        dbAdapter.close();



//        InputStream is = c.getResources().openRawResource(R.raw.restaurantinspectionreports1);
//        BufferedReader reader = new BufferedReader(
//                new InputStreamReader(is, Charset.forName("UTF-8"))
//        );
//
//        String line = "";
//        try {
//
//
//
//            //   NDAA-8RNNVR,20190412,Routine,0,2,
//            //   "306,Not Critical,Food premises not maintained in a sanitary condition
//            //   [s. 17(1)],Not Repeat|308,Not Critical,Equipment/utensils/food contact surfaces are
//            //   not in good working order [s. 16(b)],Not Repeat",Moderate
//
//            final int TRACKING_NUM_INDEX = 1;
//            final int DATE_INDEX = 2;
//            final int INSPECTION_TYPE_INDEX = 3;
//            final int CRITICAL_INDEX = 4;
//            final int HAZARD_INDEX = 5;
//            final int VIOLATIONS_LUMP_INDEX = 7;
//
//            HttpHandler httpHandler = new HttpHandler(INSPECTION_URL);
//            httpHandler.getData();
//            String body = httpHandler.getBody();
//
//            BufferedReader reader = new BufferedReader(new StringReader(body));
//
//            // Step over headers
//            reader.readLine();
//            while ((line = reader.readLine()) != null) {
//                Inspection inspection = new Inspection();
//
//                // Even newer splitting method
//                //NDAA-9DNQLJ,20180924,Follow-Up,0,0,,Low
//                //HCAR-BKDQCL,20200127,Routine,1,0,"302,Critical,Equipment/utensils/food contact surfaces not properly washed and sanitized [s. 17(2)],Not Repeat",Low
//                String [] tokens = line.split("\"");
//
//                String [] tokensFirstHalf = tokens[0].split(",");
//
//                if (tokensFirstHalf.length == 0){
//                    continue;
//                }
//                String trackingNum = tokensFirstHalf[0];
//
//                if(tokens.length > 1){
//                    for(Restaurant r : restaurantList){
//                        if(r.getTrackingNumber().equals(trackingNum)){
//                            String datetoAdd = tokensFirstHalf[1];
//                            inspection.setInspectionDate(datetoAdd);
//
//                            String inspectionType = tokensFirstHalf[2];
//                            inspection.setInspType(inspectionType);
//
//                            int numOfCritical = Integer.parseInt(tokensFirstHalf[3]);
//                            inspection.setNumCritical(numOfCritical);
//
//                            int numOfNonCritical = Integer.parseInt(tokensFirstHalf[4]);
//                            inspection.setNumNonCritical(numOfNonCritical);
//
//                            if(numOfCritical + numOfNonCritical != 0){
//                                String[] violations = tokens[1].split("\\|");
//                                for(String violationPossibility: violations){
//                                    String violation = violationPossibility.substring(0, 3);
//                                    inspection.addViolation(Integer.parseInt(violation));
//                                }
//                            }
//
//                            String hazardLevel = tokens[2];
//                            if(hazardLevel.length() > 1){
//                                inspection.setHazardRating(hazardLevel);
//                            }
//                            else{
//                                inspection.setHazardRating("Low");
//                            }
//
//                            r.addInspection(inspection);
//                            break;
//                        }
//
//                    }
//                }
//                else{
//
//                    // NDAA-8RNNVR,20181017,Routine,0,0,,Low
//                    String [] tokensSplit = tokens[0].split(",");
//                     trackingNum = tokensSplit[0];
//
//                     for(Restaurant r : restaurantList){
//                         if(r.getTrackingNumber().equals(trackingNum)){
//                             String dateToAdd = tokensSplit[1];
//                             inspection.setInspectionDate(dateToAdd);
//
//                             String inspectionType = tokensSplit[2];
//                             inspection.setInspType(inspectionType);
//
//                             inspection.setNumNonCritical(Integer.parseInt(tokensSplit[3]));
//                             inspection.setNumCritical(Integer.parseInt(tokensSplit[4]));
//                             if (tokensSplit.length == 7){
//                                 inspection.setHazardRating(tokensSplit[6]);
//                             } else {
//                                 //csv has no hazard rating
//                                 inspection.setHazardRating("Low");
//                             }
//
//
//                             r.addInspection(inspection);
//                             break;
//
//                         }
//                     }
//
//                }
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
////                String[] tokens = line.split("\"");
////                String trackingNum = tokens[TRACKING_NUM_INDEX];
////                for(Restaurant r: restaurantList){
////                    Log.d("Main Activity", " Found right restaurant");
////                    if(r.getTrackingNumber().equals(trackingNum)){
////                        String dateToAdd = tokens[DATE_INDEX];
////                        dateToAdd = dateToAdd.substring(1, dateToAdd.length() - 1);
////                        inspection.setInspectionDate(dateToAdd);
////
////                        String inspectionType = tokens[INSPECTION_TYPE_INDEX];
////                        inspection.setInspType(inspectionType);
////
////                        String valuesForCritical = tokens[CRITICAL_INDEX];
////                        valuesForCritical = valuesForCritical.replaceAll(",","");
////
////                        char charNumNonCrit = valuesForCritical.charAt(0);
////                        String numNonCrit = Character.toString(charNumNonCrit);
////
////                        char charNumCrit = valuesForCritical.charAt(1);
////                        String numCrit = Character.toString(charNumCrit);
////
////                        inspection.setNumNonCritical(Integer.parseInt(numNonCrit));
////                        inspection.setNumCritical(Integer.parseInt(numCrit));
////
////                        String hazardRating = tokens[HAZARD_INDEX];
////                        inspection.setHazardRating(hazardRating);
////
////                        if(Integer.parseInt(numNonCrit) + Integer.parseInt(numCrit) > 0){
////                            String[] violations = tokens[VIOLATIONS_LUMP_INDEX].split("\\|");
////
////                            for(String violationPossibility: violations){
////                                String violation = violationPossibility.substring(0, 3);
////                               inspection.addViolation(Integer.parseInt(violation));
////                            }
////
////                        }
////
////                        r.addInspection(inspection);
////
////                    }
////                }
//
//            }
//
//        } catch (Exception e) {
//            Log.wtf("My Activity", "Error reading data file on line " + line, e);
//            e.printStackTrace();
//        }

    }



    private void sortByRestaurantName() {
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
    
}

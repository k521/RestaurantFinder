package videodemos.example.restaurantinspector.Model;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import videodemos.example.restaurantinspector.R;


public class RestaurantManager {
    private final int NUM_OF_FIRST_NONVIOLATION_COLS = 5;
    public static RestaurantManager instance;
    private List<Restaurant> restaurantList = new ArrayList<>();

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
        InputStream is = c.getResources().openRawResource(R.raw.restaurants);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        String line = "";
        try {

            // Step over headers
            reader.readLine();
            while ((line = reader.readLine()) != null) {

                // Split by ','

                String[] tokens = line.split(",");


                // Read the data

                Restaurant restaurant = new Restaurant();
                restaurant.setTrackingNumber(tokens[0]);
                restaurant.setName(tokens[1]);
                restaurant.setPhysicalAddress(tokens[2]);
                restaurant.setPhysicalCity(tokens[3]);
                restaurant.setFactype(tokens[4]);

                if (tokens[5].length() > 0) {
                    restaurant.setLatitude(Double.parseDouble(tokens[5]));
                } else {
                    restaurant.setLatitude(0);
                }

                if (tokens[6].length() > 0) {
                    restaurant.setLongitude(Double.parseDouble(tokens[6]));
                } else {
                    restaurant.setLongitude(0);
                }

                restaurantList.add(restaurant);

                Log.d("MyActivity", "Just  created this right now" + restaurant);

            }
        } catch (IOException e) {
            Log.wtf("My Activity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }
    }

    public void InspectionReader(Context c) {
        InputStream is = c.getResources().openRawResource(R.raw.inspectionreports);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        String line = "";
        try {
            // Step over headers
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                Log.d("My Activity String initial string", line);
                Inspection inspection = new Inspection();
                String[] tokens = line.split("\"");
                String firstHalf = tokens[0];

                String[] sections = firstHalf.split(",");


                String trackingNum = sections[0];

                for (Restaurant r : restaurantList) {
                    if (r.getTrackingNumber().equals(trackingNum)) {
                        inspection.setInspectionDate(sections[1]);
                        inspection.setInspType(sections[2]);
                        inspection.setNumCritical(Integer.parseInt(sections[3]));
                        inspection.setNumNonCritical(Integer.parseInt(sections[4]));
                        inspection.setHazardRating(sections[5]);

                        if (inspection.getNumCritical() != 0) {
                            String secondHalf = tokens[1];
                            sections = secondHalf.split("\\|");

                            for (String violationPossibility : sections) {
                                    if(violationPossibility.length() > 3){
                                        String violation = violationPossibility.substring(0, 3);
                                        inspection.addViolation(Integer.parseInt(violation));
                                    }

                            }
                        }

                        r.addInspection(inspection);
                    }
                }

            }

        } catch (IOException e) {
            Log.wtf("My Activity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }

    }
}

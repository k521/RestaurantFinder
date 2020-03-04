package videodemos.example.restaurantinspector.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import videodemos.example.restaurantinspector.Model.Restaurant;
import videodemos.example.restaurantinspector.Model.RestaurantManager;
import videodemos.example.restaurantinspector.Model.Violation;
import videodemos.example.restaurantinspector.R;

public class InspectionReportActivity extends AppCompatActivity {
    private List<Violation> violationList = new ArrayList<Violation>();

    String restaurantName = getIntent().getStringExtra("restaurantName");
    String inspectionDate = getIntent().getStringExtra("inspectionDate");

    RestaurantManager restaurantManager = getInstance(this);
    Restaurant currentRestaurant;

    ArrayList<Inspection> inspectionsList;
    Inspection currentInspection;

    ArrayList<Integer> violationCodes;
    ViolationMaps maps = getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_report);

        getCurrentRestaurant();
        getCurrentInspectionReport();
        getViolationCodes();

        populateViolationList();
        populateListView();
        //registerClickCallback();
    }

    private void getViolationCodes() {
        violationCodes = currentInspection.getViolationCodes();
    }

    private void getCurrentInspectionReport() {
        inspectionsList = currentRestaurant.getInspections();
        for(Inspection foundInspection: inspectionsList){
            if(foundInspection.getInspectionDate() == inspectionDate){
                currentInspection = foundInspection;
            }
        }
    }

    private void getCurrentRestaurant(){
        for(Restaurant foundRestaurant: restaurantManager){
            if(foundRestaurant.getName() == restaurantName){
                currentRestaurant = foundRestaurant;
            }
        }

    }
    private void populateViolationList() {
        for(Integer violationHashCode: violationCodes){
            String violation = maps.shortViolation.get(violationHashCode);
            //String idToImage = maps.natureViolation.get(violationHashCode);
            boolean severityToImage = maps.severity.get(violationHashCode);
            Violation newViolate = Violation(violation, idToImage, severityToImage);
            violationList.add(newViolate);
        }
    }


    private void populateListView() {
        ArrayAdapter<Violation> adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.violationsListView);
        list.setAdapter(adapter);

    }

    private class MyListAdapter extends ArrayAdapter<Violation> {

        public MyListAdapter() {
            super(InspectionReportActivity.this, R.layout.item_view, violationList);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.item_view, parent, false);
            }


            Violation currentViolation = violationList.get(position);

            ImageView severity = (ImageView) itemView.findViewById(R.id.severity);
            if(currentViolation.getSeverityToImage() == true){
                severity.setImageResource(criticalID);
            }
            else{
                severity.setImageResource(nonCriticalID);
            }

            ImageView violationType = (ImageView) itemView.findViewById(R.id.violationType);
            if(currentViolation.getIdToImage() == food){
                violationType.setImageResource(foodimageID);
            }
            else if(currentViolation.getIdToImage() == equipment){
                violationType.setImageResource(equipmentimageID);
            }
            else if(currentViolation.getIdToImage() == misc){
                violationType.setImageResource(miscImageID);
            }

            TextView description = (TextView) itemView.findViewById(R.id.violationDescription);
            description.setText(currentViolation.getViolation());

            return itemView;
            //return super.getView(position, convertView, parent);
        }

    }
//    private void registerClickCallback() {
//        ListView list = (ListView) findViewById(R.id.violations);
//        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
//            @Override
//            public void onItemClick(AdapterView<?> paret, View viewClicked, int position, long id){
//                TextView textView = (TextView) viewClicked;
//                String message = "a";//the detailed description of the violation
//                Toast.makeText(InspectionReportActivity.this, message, Toast.LENGTH_LONG);
//            }

//        });
//    }

//    private static void populateSeverity(){
//        //TODO: Add boolean as second field based on criticallity
//        severity.put(101, false);
//        severity.put(102,false);
//        severity.put(103,false);
//        severity.put(104,false);
//        severity.put(201, true);
//        severity.put(202,true);
//        severity.put(203,true);
//        severity.put(204,true);
//        severity.put(205,true);
//        severity.put(206,true);
//        severity.put(208,false);
//        severity.put(209,false);
//        severity.put(210,false);
//        severity.put(211,false);
//        severity.put(212,false);
//        severity.put(301,true);
//        severity.put(302,true);
//        severity.put(303,true);
//        severity.put(304,false);
//        severity.put(305,false);
//        severity.put(306,false);
//        severity.put(307,false);
//        severity.put(308,false);
//        severity.put(309,false);
//        severity.put(310,false);
//        severity.put(311,false);
//        severity.put(312,false);
//        severity.put(313,false);
//        severity.put(314,false);
//        severity.put(315,false);
//        severity.put(401,true);
//        severity.put(402,true);
//        severity.put(403,false);
//        severity.put(404,false);
//        severity.put(501,false);
//        severity.put(502,false);
    //  }

    //public static HashMap<Integer, String> shortViolation = new HashMap<Integer, String>();

    //    private static void populateShortViolation(){
//        //TODO: Add boolean as second field based on criticallity
//        shortViolation.put(101, "Plans/construction/alterations not up to standard.");
//        shortViolation.put(102,"Operation of unapproved food premises. ");
//        shortViolation.put(103,"No valid permit for this restaurant.");
//        shortViolation.put(104,"Permit not seen easily.");
//        shortViolation.put(201, "Food contaminated or unfit for consumption.");
//        shortViolation.put(202,"Food not processed in manner for safe eating.");
//        shortViolation.put(203,"Food not cooled properly.");
//        shortViolation.put(204,"Food not cooked or heated for safe eating.");
//        shortViolation.put(205,"Cold food not stored properly.");
//        shortViolation.put(206,"Hot food not stored properly.);
//        shortViolation.put(208,"Food obtained from unapproved sources.");
//        shortViolation.put(209,"Food not protected from contamination.");
//        shortViolation.put(210,"Food not thawed properly.");
//        shortViolation.put(211,"Frozen food not stored properly.");
//        shortViolation.put(212,"Food handling procedures not provided.");
//        shortViolation.put(301,"Equipment/utensils/food not sanitary.");
//        shortViolation.put(302,"Equipment/utensils/food not properly washed/sanitized.");
//        shortViolation.put(303,"Equipment/facilities/Water for sanitation not proper.");
//        shortViolation.put(304,"Location contains pests.");
//        shortViolation.put(305,"Conditions observed may cultivate pests.");
//        shortViolation.put(306,"Food premise not sanitary.");
//        shortViolation.put(307,"Equipment/utensils/food contact surfaces bad design/material");
//        shortViolation.put(308,"Equipment/utensils/food contact surfaces not working.");
//        shortViolation.put(309,"Chemical cleansers stored or labelled improperly.");
//        shortViolation.put(310,"Single use items used more than once.);
//        shortViolation.put(311,"Premise not maintained according to plan.");
//        shortViolation.put(312,"Items unrelated to food business on premise.");
//        shortViolation.put(313, "Live animals on premise.");
//        shortViolation.put(314,"Approved sanitation procedures not provided.");
//        shortViolation.put(315,"Inaccurate thermometers.");
//        shortViolation.put(401,"Proper handwashing station not present.");
//        shortViolation.put(402,"Hands not washed adequately or frequently enough.");
//        shortViolation.put(403,"Employees lack hygiene.");
//        shortViolation.put(404,"Employees smoking in not proper areas.");
//        shortViolation.put(501,"FOODSAFE level 1 or Equivalent not present.");
//        shortViolation.put(502,"In Operator's absence, nobody has FOODSAFE level 1 or Equivalent.");
    //  }
//public static HashMap<Integer, String> natureViolation = new HashMap<Integer, String>();
    //    private static void populateShortViolation(){
//        //TODO: Add boolean as second field based on criticallity
//        natureViolation.put(101, "");
//        natureViolation.put(102,"");
//        natureViolation.put(103,"");
//        natureViolation.put(104,"");
//        natureViolation.put(201, "");
//        natureViolation.put(202,"");
//        natureViolation.put(203,"");
//        natureViolation.put(204,"");
//        natureViolation.put(205,"");
//        natureViolation.put(206,");
//        natureViolation.put(208,"");
//        natureViolation.put(209,"");
//        natureViolation.put(210,"");
//        natureViolation.put(211,"");
//        natureViolation.put(212,"");
//        natureViolation.put(301,"");
//        natureViolation.put(302,"");
//        natureViolation.put(303,"");
//        natureViolation.put(304,"");
//        natureViolation.put(305,"");
//        natureViolation.put(306,"");
//        natureViolation.put(307,"");
//        natureViolation.put(308,"");
//        natureViolation.put(309,"");
//        natureViolation.put(310,"");
//        natureViolation.put(311,"");
//        natureViolation.put(312,"");
//        natureViolation.put(313,"");
//        natureViolation.put(314,"");
//        natureViolation.put(315,"");
//        natureViolation.put(401,"");
//        natureViolation.put(402,"");
//        natureViolation.put(403,"");
//        natureViolation.put(404,"");
//        natureViolation.put(501,"");
//        natureViolation.put(502,"");
    //  }
}

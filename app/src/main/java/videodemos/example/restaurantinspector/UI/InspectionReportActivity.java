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

import videodemos.example.restaurantinspector.Model.Inspection;
import videodemos.example.restaurantinspector.Model.Restaurant;
import videodemos.example.restaurantinspector.Model.RestaurantManager;
import videodemos.example.restaurantinspector.Model.Violation;
import videodemos.example.restaurantinspector.Model.ViolationMaps;
import videodemos.example.restaurantinspector.R;

import static videodemos.example.restaurantinspector.Model.RestaurantManager.getInstance;

public class InspectionReportActivity extends AppCompatActivity {
    private List<Violation> violationList = new ArrayList<Violation>();

    String restaurantName = getIntent().getStringExtra("restaurantName");
    String inspectionDate = getIntent().getStringExtra("inspectionDate");

    RestaurantManager restaurantManager = RestaurantManager.getInstance(this);
    Restaurant currentRestaurant;

    ArrayList<Inspection> inspectionsList;
    Inspection currentInspection;

    List<Integer> violationCodes;
    ViolationMaps maps = ViolationMaps.getInstance();

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
        violationCodes = currentInspection.getViolationList();
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
        for(Restaurant foundRestaurant: restaurantManager.getRestaurantList()){
            if(foundRestaurant.getName() == restaurantName){
                currentRestaurant = foundRestaurant;
            }
        }

    }
    private void populateViolationList() {
        for(Integer violationHashCode: violationCodes){
            String violation = maps.shortViolation.get(violationHashCode);
            String idToImage = maps.natureViolation.get(violationHashCode);
            boolean severityToImage = maps.severity.get(violationHashCode);
            Violation newViolate = new Violation(violation, idToImage, severityToImage);
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
                severity.setImageResource(R.drawable.critical_icon); // cirtical.
            }
            else{
                severity.setImageResource(R.drawable.non_critical_icon); //nonCritical ID
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
//        shortViolation.put(208,"Food obtained from unapproved sources.");         //
//        shortViolation.put(209,"Food not protected from contamination.");         //
//        shortViolation.put(210,"Food not thawed properly.");                      //
//        shortViolation.put(211,"Frozen food not stored properly.");               //
//        shortViolation.put(212,"Food handling procedures not provided.");         //
//        shortViolation.put(301,"Equipment/utensils/food not sanitary.");          //
//        shortViolation.put(302,"Equipment/utensils/food not properly washed/sanitized."); //
//        shortViolation.put(303,"Equipment/facilities/Water for sanitation not proper.");
//        shortViolation.put(304,"Location contains pests.");                       //
//        shortViolation.put(305,"Conditions observed may cultivate pests.");       //
//        shortViolation.put(306,"Food premise not sanitary.");                     //
//        shortViolation.put(307,"Equipment/utensils/food contact surfaces bad design/material");   //
//        shortViolation.put(308,"Equipment/utensils/food contact surfaces not working.");          //
//        shortViolation.put(309,"Chemical cleansers stored or labelled improperly.");              //
//        shortViolation.put(310,"Single use items used more than once.);                           //
//        shortViolation.put(311,"Premise not maintained according to plan.");                      //
//        shortViolation.put(312,"Items unrelated to food business on premise.");                   //
//        shortViolation.put(313, "Live animals on premise.");                                      //
//        shortViolation.put(314,"Approved sanitation procedures not provided.");                   //
//        shortViolation.put(315,"Inaccurate thermometers.");                                       //
//        shortViolation.put(401,"Proper handwashing station not present.");                        //
//        shortViolation.put(402,"Hands not washed adequately or frequently enough.");              //
//        shortViolation.put(403,"Employees lack hygiene.");                                        //
//        shortViolation.put(404,"Employees smoking in not proper areas.");                         //
//        shortViolation.put(501,"FOODSAFE level 1 or Equivalent not present.");                    //
//        shortViolation.put(502,"In Operator's absence, nobody has FOODSAFE level 1 or Equivalent."); //
    //  }
//public static HashMap<Integer, String> natureViolation = new HashMap<Integer, String>();
    //    private static void populateShortViolation(){
//        //TODO: Add boolean as second field based on criticallity
//        natureViolation.put(101, "miscellaneous_icon");
//        natureViolation.put(102,"miscellaneous_icon");
//        natureViolation.put(103,"miscellaneous_icon");
//        natureViolation.put(104,"miscellaneous_icon");
//        natureViolation.put(201, "unfit_consumption_icon");
//        natureViolation.put(202,"miscellaneous_icon");
//        natureViolation.put(203,"thaw_hazard_icon");
//        natureViolation.put(204,"unfit_consumption_icon");
//        natureViolation.put(205,"miscellaneous_icon");
//        natureViolation.put(206,"miscellaneous_icon");
//        natureViolation.put(208,"miscellaneous_icon");
//        natureViolation.put(209,"contamination_icon");
//        natureViolation.put(210,"thaw_hazard_icon");
//        natureViolation.put(211,"miscellaneous_icon");
//        natureViolation.put(212,"miscellaneous_icon");
//        natureViolation.put(301,"sanitory_condition_icon");
//        natureViolation.put(302,"sanitory_condition_icon");
//        natureViolation.put(303,"sanitory_condition_icon");
//        natureViolation.put(304,"pests_icon");
//        natureViolation.put(305,"pests_icon");
//        natureViolation.put(306,"sanitory_condition_icon");
//        natureViolation.put(307,"working_order");
//        natureViolation.put(308,"working_order");
//        natureViolation.put(309,"miscellaneous_icon");
//        natureViolation.put(310,"miscellaneous_icon");
//        natureViolation.put(311,"miscellaneous_icon");
//        natureViolation.put(312,"miscellaneous_icon");
//        natureViolation.put(313,"miscellaneous_icon");
//        natureViolation.put(314,"sanitory_condition_icon");
//        natureViolation.put(315,"working_order");
//        natureViolation.put(401,"sanitory_condition_icon");
//        natureViolation.put(402,"sanitory_condition_icon");
//        natureViolation.put(403,"unhygienic_icon");
//        natureViolation.put(404,"miscellaneous_icon");
//        natureViolation.put(501,"miscellaneous_icon");
//        natureViolation.put(502,"miscellaneous_icon");
    //  }
}

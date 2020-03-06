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

    int restaurantName = getIntent().getIntExtra("Restaurant", 0);
    int inspectionIndex = getIntent().getIntExtra("Inspection", 0);

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

        setTextViews();

        populateViolationList();
        populateListView();
        //registerClickCallback();
    }

    private void setTextViews() {
        TextView dateOfInspection = findViewById(R.id.dateOfInspection);
        TextView inspectionType = findViewById(R.id.inspectionType);
        TextView criticalIssues = findViewById(R.id.criticalIssues);
        TextView nonCriticalIssues = findViewById(R.id.nonCriticalIssues);

        dateOfInspection.setText(currentInspection.getInspectionDate());
        inspectionType.setText(currentInspection.getInspType());
        criticalIssues.setText(currentInspection.getNumCritical());
        nonCriticalIssues.setText(currentInspection.getNumNonCritical());
    }

    private void getViolationCodes() {
        violationCodes = currentInspection.getViolationList();
    }

    private void getCurrentInspectionReport() {
        inspectionsList = currentRestaurant.getInspections();
        currentInspection = inspectionsList.get(inspectionIndex);
    }

    private void getCurrentRestaurant(){
        currentRestaurant = restaurantManager.getRestaurantList().get(restaurantName);
    }
    private void populateViolationList() {
        for(Integer violationHashCode: violationCodes){
            String violation = maps.shortViolation.get(violationHashCode);
            int idToImage = maps.natureViolation.get(violationHashCode);
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

            violationType.setImageResource(currentViolation.getIdToImage());



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

}

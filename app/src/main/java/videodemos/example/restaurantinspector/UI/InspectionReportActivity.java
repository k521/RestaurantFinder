package videodemos.example.restaurantinspector.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
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

import videodemos.example.restaurantinspector.Model.DataHandling.DateCalculations;
import videodemos.example.restaurantinspector.Model.DataHandling.Inspection;
import videodemos.example.restaurantinspector.Model.DataHandling.Restaurant;
import videodemos.example.restaurantinspector.Model.RestaurantManager;
import videodemos.example.restaurantinspector.Model.DataHandling.Violation;
import videodemos.example.restaurantinspector.Model.DataHandling.ViolationMaps;
import videodemos.example.restaurantinspector.R;

/**
 * Displays individual inspection reports.
 */

public class InspectionReportActivity extends AppCompatActivity {

    public static Intent makeIntent(Context c, String trackingNumber, int indexOfInspection) {
        Intent intentThirdActivity = new Intent(c, InspectionReportActivity.class);
        intentThirdActivity.putExtra(TAG_RESTAURANT, trackingNumber);
        intentThirdActivity.putExtra(TAG_INSPECTION, indexOfInspection);
        return intentThirdActivity;

    }

    private static final String TAG_RESTAURANT = "Restaurant";
    private static final String TAG_INSPECTION = "Inspection";

    private List<Violation> violationList = new ArrayList<Violation>();

    private String restaurantName;
    private int inspectionIndex;


    private RestaurantManager restaurantManager = RestaurantManager.getInstance();
    private Restaurant currentRestaurant;

    private List<Inspection> inspectionsList;
    private Inspection currentInspection;

    private List<Integer> violationCodes;
    private ViolationMaps maps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_report);

        //restaurantName = getIntent().getIntExtra(TAG_RESTAURANT, 0);
        restaurantName = getIntent().getStringExtra(TAG_RESTAURANT);
        inspectionIndex = getIntent().getIntExtra(TAG_INSPECTION, 0);
        maps = new ViolationMaps(this);

        getCurrentRestaurant();
        getCurrentInspectionReport();
        setupToolbar();
        getViolationCodes();
        setTextViews();
        setupHazardInfo();
        populateViolationList();
        populateListView();
        registerClickCallback();
    }

    private void getCurrentRestaurant() {
        for(Restaurant r : restaurantManager.getRestaurantList()){
            if(r.getTrackingNumber().equals(restaurantName)){
                currentRestaurant = r;
            }
        }
    }

    private void getCurrentInspectionReport() {
        inspectionsList = currentRestaurant.getInspections();
        currentInspection = inspectionsList.get(inspectionIndex);
    }


    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.inspection_report_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView title = findViewById(R.id.tv_inspection_details_restaurant_title);
        title.setText(currentRestaurant.getName());
    }


    private void getViolationCodes() {
        violationCodes = currentInspection.getViolationList();
    }

    private void setTextViews() {
        TextView dateOfInspection = findViewById(R.id.dateOfInspection);
        TextView inspectionType = findViewById(R.id.inspectionType);
        TextView criticalIssues = findViewById(R.id.criticalIssues);
        TextView nonCriticalIssues = findViewById(R.id.nonCriticalIssues);
        dateOfInspection.setText(getDateForDisplay());
        inspectionType.setText(currentInspection.getInspType());
        criticalIssues.setText(Integer.toString(currentInspection.getNumCritical()));
        nonCriticalIssues.setText(Integer.toString(currentInspection.getNumNonCritical()));
    }

    private void setupHazardInfo() {
        String hazardRating = currentInspection.getHazardRating();
        ConstraintLayout hazardBackground = findViewById(R.id.cl_hazard_background);
        TextView hazardDescription = findViewById(R.id.tv_hazard_level_text);
        ImageView hazardIcon = findViewById(R.id.iv_inspection_hazard_icon);

        if (hazardRating.equals("Low")) {
            int lowHazardColor = ContextCompat.getColor(this, R.color.colorLowHazard);
            hazardBackground.setBackgroundColor(lowHazardColor);
            hazardIcon.setImageResource(R.drawable.low_hazard_icon);
        } else if (hazardRating.equals("Moderate")) {
            int lowMedColor = ContextCompat.getColor(this, R.color.colorMedHazard);
            hazardBackground.setBackgroundColor(lowMedColor);
            hazardIcon.setImageResource(R.drawable.med_hazard_icon);
        } else {
            int lowHighColor = ContextCompat.getColor(this, R.color.colorHighHazard);
            hazardBackground.setBackgroundColor(lowHighColor);
            hazardIcon.setImageResource(R.drawable.high_hazard_icon);
        }

        hazardDescription.setText(getString(R.string.hazard_level, hazardRating));
    }

    private void populateViolationList() {
        ViolationMaps violationMaps = new ViolationMaps(this);
        for (Integer violationHashCode : violationCodes) {
            String violation = violationMaps.getShortViolationCodeDescription(violationHashCode);

            int idToImage = violationMaps.getNatureViolation(violationHashCode);
            boolean severityToImage = violationMaps.getSeverity(violationHashCode);

            Violation newViolate = new Violation(violation, idToImage, severityToImage);
            violationList.add(newViolate);
        }
    }

    private void populateListView() {
        ArrayAdapter<Violation> adapter = new MyListAdapter();
        ListView list = findViewById(R.id.violationsListView);
        list.setAdapter(adapter);

    }

    private void registerClickCallback() {
        ListView list = findViewById(R.id.violationsListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> paret, View viewClicked, int position, long id) {
                int index = violationCodes.get(position);
                ViolationMaps violationMaps = new ViolationMaps(InspectionReportActivity.this);
                String message = violationMaps.getFullViolationCodeDescription(index);

                Toast.makeText(InspectionReportActivity.this, message, Toast.LENGTH_LONG).show();

            }

        });
    }

    private String getDateForDisplay() {
        // Get the number of days since the inspection
        String inspectionDate;
        String date = currentInspection.getInspectionDate();
        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);

        int monthInteger = Integer.parseInt(month);
        int dayInteger = Integer.parseInt(day);

        DateCalculations dateCalculations = new DateCalculations(this);
        String monthName = dateCalculations.getMonthName(monthInteger);
        inspectionDate = monthName + " " + dayInteger + ", " + year;

        return inspectionDate;
    }


    private class MyListAdapter extends ArrayAdapter<Violation> {

        public MyListAdapter() {
            super(InspectionReportActivity.this, R.layout.item_view, violationList);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.item_view, parent, false);
            }


            Violation currentViolation = violationList.get(position);

            ImageView severity = itemView.findViewById(R.id.severity);
            if (currentViolation.isSevere() == true) {
                severity.setImageResource(R.drawable.critical_icon);
            } else {
                severity.setImageResource(R.drawable.non_critical_icon);
            }

            ImageView violationType = itemView.findViewById(R.id.violationType);

            violationType.setImageResource(currentViolation.getIdToImage());


            TextView description = itemView.findViewById(R.id.violationDescription);
            description.setText(currentViolation.getViolation());

            return itemView;
        }

    }

}

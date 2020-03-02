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

import videodemos.example.restaurantinspector.Model.Violation;
import videodemos.example.restaurantinspector.R;

public class InspectionReportActivity extends AppCompatActivity {
    private List<Violation> violationList = new ArrayList<Violation>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_report);

        populateViolationList();
        populateListView();
        //registerClickCallback();
    }

    private void populateViolationList() {
        // populate violationList using for each loop item in violationMaps for each inspection
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




}

package videodemos.example.restaurantinspector.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import videodemos.example.restaurantinspector.R;

public class InspectionReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_report);

        populateListView();
        registerClickCallback():
    }



    private void populateListView() {
        String[] myItems = new String[getNumberOfViolations];

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.activity_inspection_report, myItems);

        ListView list = (ListView) findViewById(R.id.violations);
        list.setAdapter(adapter);

    }
    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.violations);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> paret, View viewClicked, int position, long id){
                TextView textView = (TextView) viewClicked;
                String message = "a";//the detailed description of the violation
                Toast.makeText(InspectionReportActivity.this, message, Toast.LENGTH_LONG);
            }





        });
    }


    public Inspection getInspection(){

    }

}

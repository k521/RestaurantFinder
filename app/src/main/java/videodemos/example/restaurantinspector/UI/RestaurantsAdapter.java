package videodemos.example.restaurantinspector.UI;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;

import videodemos.example.restaurantinspector.Model.Inspection;
import videodemos.example.restaurantinspector.Model.Restaurant;
import videodemos.example.restaurantinspector.R;

public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.RestaurantsViewHolder> {

    private List<Restaurant> restaurantDataset;


    public static class RestaurantsViewHolder extends RecyclerView.ViewHolder {
        TextView restaurantName;
        TextView numOfIssues;
        TextView lastInspection;

        public RestaurantsViewHolder(View itemView) {
            super(itemView);
            restaurantName = itemView.findViewById(R.id.tv_card_restaraunt_name);
            numOfIssues = itemView.findViewById(R.id.tv_card_number_issues);
            lastInspection = itemView.findViewById(R.id.tv_card_last_inspection);
        }
    }

    public RestaurantsAdapter(List<Restaurant> restaurantDataset) {
        this.restaurantDataset = restaurantDataset;
    }

    @Override
    public int getItemCount() {
        return restaurantDataset.size();
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantsViewHolder holder, int position) {
        holder.restaurantName.setText(restaurantDataset.get(position).getName());
        Restaurant restaurantInQuestion = restaurantDataset.get(position);
        Log.d("Adapter Class",restaurantInQuestion.toString());
        int sizeOfInspections = restaurantInQuestion.getInspections().size();
        Inspection latestInspection = restaurantInQuestion.getInspections().get(sizeOfInspections - 1);
        Calendar mostRecentDate = latestInspection.getInspectionDate();
        holder.lastInspection.setText(mostRecentDate.toString());
        int numOfIssues = latestInspection.getNumCritical() + latestInspection.getNumNonCritical();
        holder.numOfIssues.setText("" + numOfIssues);



    }

    @NonNull
    @Override
    public RestaurantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurant_item, parent, false);

        RestaurantsViewHolder vh = new RestaurantsViewHolder(view);
        return vh;
    }



}

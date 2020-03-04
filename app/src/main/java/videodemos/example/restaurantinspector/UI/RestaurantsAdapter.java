package videodemos.example.restaurantinspector.UI;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;

import videodemos.example.restaurantinspector.Model.Inspection;
import videodemos.example.restaurantinspector.Model.Restaurant;
import videodemos.example.restaurantinspector.R;

public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.RestaurantsViewHolder> {

    private List<Restaurant> restaurantDataset;
    private Context context;


    public static class RestaurantsViewHolder extends RecyclerView.ViewHolder {
        TextView restaurantName;
        TextView numOfIssues;
        TextView lastInspection;
        CardView cardViewBackground;

        public RestaurantsViewHolder(View itemView) {
            super(itemView);
            restaurantName = itemView.findViewById(R.id.tv_card_restaraunt_name);
            numOfIssues = itemView.findViewById(R.id.tv_card_number_issues);
            lastInspection = itemView.findViewById(R.id.tv_card_last_inspection);
            cardViewBackground = itemView.findViewById(R.id.cv_restaurant_card);

        }
    }

    public RestaurantsAdapter(List<Restaurant> restaurantDataset, Context context) {
        this.restaurantDataset = restaurantDataset;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return restaurantDataset.size();
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantsViewHolder holder, int position) {
        holder.restaurantName.setText(restaurantDataset.get(position).getName());
        Restaurant restaurantInQuestion = restaurantDataset.get(position);
        int sizeOfInspections = restaurantInQuestion.getInspections().size();
        if(sizeOfInspections > 0){
            Inspection latestInspection = restaurantInQuestion.getInspections().get(sizeOfInspections - 1);
            holder.lastInspection.setText(latestInspection.getInspectionDate());
            int numOfIssues = latestInspection.getNumCritical() + latestInspection.getNumNonCritical();

            holder.numOfIssues.setText(context.getResources().getString(R.string.number_of_issues) + numOfIssues);

            if (latestInspection.getHazardRating().equals("Low")){
                int lowHazardColor = context.getResources().getColor(R.color.colorLowHazard);
                holder.cardViewBackground.setCardBackgroundColor(lowHazardColor);
            } else if (latestInspection.getHazardRating().equals("Moderate")){
                int medHazardColor = context.getResources().getColor(R.color.colorMedHazard);
                holder.cardViewBackground.setCardBackgroundColor(medHazardColor);
            } else {
                int highHazardColor = context.getResources().getColor(R.color.colorHighHazard);
                holder.cardViewBackground.setCardBackgroundColor(highHazardColor);
            }
        }
        else{
            holder.lastInspection.setText(" No inspection so far");
            holder.numOfIssues.setText("0");
        }





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

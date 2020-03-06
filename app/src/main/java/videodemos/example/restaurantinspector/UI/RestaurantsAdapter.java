package videodemos.example.restaurantinspector.UI;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import videodemos.example.restaurantinspector.Model.Inspection;
import videodemos.example.restaurantinspector.Model.Restaurant;
import videodemos.example.restaurantinspector.Model.ViolationMaps;
import videodemos.example.restaurantinspector.R;

public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.RestaurantsViewHolder> {

    private List<Restaurant> restaurantDataset;
    private Context context;
    private OnRestaurantListener onRestaurantListener;


    public class RestaurantsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView restaurantName;
        TextView numOfIssues;
        TextView lastInspection;
        CardView cardViewBackground;
        OnRestaurantListener onRestaurantListener;

        public RestaurantsViewHolder(View itemView, OnRestaurantListener onRestaurantListener) {
            super(itemView);
            restaurantName = itemView.findViewById(R.id.tv_card_restaraunt_name);
            numOfIssues = itemView.findViewById(R.id.tv_card_number_issues);
            lastInspection = itemView.findViewById(R.id.tv_card_last_inspection);
            cardViewBackground = itemView.findViewById(R.id.cv_restaurant_card);
            this.onRestaurantListener = onRestaurantListener;

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onRestaurantListener.onRestaurantClick(getAdapterPosition());

        }
    }

    public RestaurantsAdapter(List<Restaurant> restaurantDataset, Context context, OnRestaurantListener onRestaurantListener) {
        this.restaurantDataset = restaurantDataset;
        this.context = context;
        this.onRestaurantListener = onRestaurantListener;
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
        if (sizeOfInspections > 0) {
           // Inspection latestInspection = restaurantInQuestion.getInspections().get(sizeOfInspections - 1);
            Inspection latestInspection = restaurantInQuestion.getInspections().get(0);


            // Get the number of days since the last inspection
            int numOfDaysSinceLastInspection = ViolationMaps.daysInBetween(latestInspection.getInspectionDate());

            // Check to see if it happened in the last 30 days
            if(numOfDaysSinceLastInspection <= 30){
                holder.lastInspection.setText("Last Inspection : " + numOfDaysSinceLastInspection + " days ago. ");
            }
            else if(numOfDaysSinceLastInspection <= 365){
                String date = latestInspection.getInspectionDate();
                String month = date.substring(4,6);
                String day = date.substring(6,8);

                int monthInteger = Integer.parseInt(month);
                int dayInteger = Integer.parseInt(day);

                String monthName = ViolationMaps.months.get(monthInteger);
                holder.lastInspection.setText("Last Inspection : " + monthName + " " + dayInteger);

            }else{
                String date = latestInspection.getInspectionDate();
                String year = date.substring(0,4);
                String month = date.substring(4,6);

                String monthName = ViolationMaps.months.get(Integer.parseInt(month));
                holder.lastInspection.setText("Last Inspection : " + monthName + " " + year);

            }
            int numOfIssues = latestInspection.getNumCritical() + latestInspection.getNumNonCritical();

            holder.numOfIssues.setText(context.getResources().getString(R.string.number_of_issues, numOfIssues));

            if (latestInspection.getHazardRating().equals("Low")) {
                int lowHazardColor = ContextCompat.getColor(context, R.color.colorLowHazard);
                holder.cardViewBackground.setCardBackgroundColor(lowHazardColor);
            } else if (latestInspection.getHazardRating().equals("Moderate")) {
                int medHazardColor = ContextCompat.getColor(context, R.color.colorMedHazard);
                holder.cardViewBackground.setCardBackgroundColor(medHazardColor);
            } else {
                int highHazardColor = ContextCompat.getColor(context, R.color.colorHighHazard);
                holder.cardViewBackground.setCardBackgroundColor(highHazardColor);
            }
        } else {
            holder.lastInspection.setText(context.getResources().getString(R.string.no_inspections_so_far));
            holder.numOfIssues.setText(context.getResources().getString(R.string.number_of_issues, 0));
        }

    }

    @NonNull
    @Override
    public RestaurantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurant_item, parent, false);

        RestaurantsViewHolder vh = new RestaurantsViewHolder(view, onRestaurantListener);
        return vh;
    }

    public interface OnRestaurantListener {
        void onRestaurantClick(int position);
    }
}

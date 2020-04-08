package videodemos.example.restaurantinspector.UI.Adapters;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import videodemos.example.restaurantinspector.Model.DataHandling.DateCalculations;
import videodemos.example.restaurantinspector.Model.DataHandling.Inspection;
import videodemos.example.restaurantinspector.Model.DataHandling.Restaurant;
import videodemos.example.restaurantinspector.Model.RestaurantManager;
import videodemos.example.restaurantinspector.R;

/**
 * Restaurants adapter for RecyclerView
 */

public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.RestaurantsViewHolder> {

    private List<Restaurant> restaurantDataset;
    private Context context;
    private OnRestaurantListener onRestaurantListener;

    public class RestaurantsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView restaurantName;
        TextView numOfIssues;
        TextView lastInspection;
        ImageView hazardIcon;
        CardView cardViewBackground;
        ImageView restaurantIcon;
        ImageView favouriteIcon;
        OnRestaurantListener onRestaurantListener;

        public RestaurantsViewHolder(View itemView, OnRestaurantListener onRestaurantListener) {
            super(itemView);
            restaurantName = itemView.findViewById(R.id.tv_card_restaraunt_name);
            numOfIssues = itemView.findViewById(R.id.tv_card_number_issues);
            lastInspection = itemView.findViewById(R.id.tv_card_last_inspection);
            cardViewBackground = itemView.findViewById(R.id.cv_fragment_card);
            hazardIcon = itemView.findViewById(R.id.iv_card_hazard_icon);
            restaurantIcon = itemView.findViewById(R.id.iv_restaurant_icon);
            favouriteIcon = itemView.findViewById(R.id.iv_card_favourite);
            this.onRestaurantListener = onRestaurantListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Restaurant selectedRestaurant = restaurantDataset.get(getAdapterPosition());
            String trackingNumber = selectedRestaurant.getTrackingNumber();
            onRestaurantListener.onRestaurantClick(trackingNumber);

        }
    }

    public RestaurantsAdapter(List<Restaurant> restaurantDataset, Context context,
                              OnRestaurantListener onRestaurantListener) {
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
        Restaurant restaurantInQuestion = restaurantDataset.get(position);

            final int LESS_THAN_A_MONTH = 30;
            final int LESS_THAN_A_YEAR = 365;

            holder.restaurantName.setText(restaurantDataset.get(position).getName());
            restaurantInQuestion = restaurantDataset.get(position);

            int sizeOfInspections = restaurantInQuestion.getInspections().size();
            if (sizeOfInspections > 0) {
                Inspection latestInspection = restaurantInQuestion.getInspections().get(0);
                // Get the number of days since the last inspection
                DateCalculations dateCalculations = new DateCalculations(context);
                int numOfDaysSinceLastInspection = dateCalculations
                        .daysInBetween(latestInspection.getInspectionDate());

                // Check to see if it happened in the last 30 days
                if (numOfDaysSinceLastInspection <= LESS_THAN_A_MONTH) {
                    holder.lastInspection
                            .setText(context.getString(R.string.date_of_inspection_days_ago, numOfDaysSinceLastInspection));
                } else if (numOfDaysSinceLastInspection <= LESS_THAN_A_YEAR) {
                    String date = latestInspection.getInspectionDate();
                    String month = date.substring(4, 6);
                    String day = date.substring(6, 8);

                    int monthInteger = Integer.parseInt(month);
                    int dayInteger = Integer.parseInt(day);

                    String monthName = dateCalculations.getMonthName(monthInteger);
                    holder.lastInspection.setText(context.getString(R.string.date_of_inspection_with_params, monthName, dayInteger));

                } else {
                    String date = latestInspection.getInspectionDate();
                    String year = date.substring(0, 4);
                    String month = date.substring(4, 6);

                    String monthName = dateCalculations.getMonthName(Integer.parseInt(month));
                    holder.lastInspection
                            .setText(context.getString(R.string.date_of_inspection_with_params, monthName, Integer.parseInt(year)));

                }
                int numOfIssues = latestInspection.getNumCritical() +
                        latestInspection.getNumNonCritical();

                holder.numOfIssues.setText(context.getResources().getString(R.string.number_of_issues, numOfIssues));

                if (latestInspection.getHazardRating().equals("Low")) {
                    int lowHazardColor = ContextCompat.getColor(context, R.color.colorLowHazard);
                    holder.cardViewBackground.setCardBackgroundColor(lowHazardColor);
                    holder.hazardIcon.setImageResource(R.drawable.low_hazard_icon);
                } else if (latestInspection.getHazardRating().equals("Moderate")) {
                    int medHazardColor = ContextCompat.getColor(context, R.color.colorMedHazard);
                    holder.cardViewBackground.setCardBackgroundColor(medHazardColor);
                    holder.hazardIcon.setImageResource(R.drawable.med_hazard_icon);
                } else {
                    int highHazardColor = ContextCompat.getColor(context, R.color.colorHighHazard);
                    holder.cardViewBackground.setCardBackgroundColor(highHazardColor);
                    holder.hazardIcon.setImageResource(R.drawable.high_hazard_icon);
                }
            } else {
                holder.lastInspection.setText(context.getResources().getString(R.string.no_inspections_so_far));
                holder.numOfIssues.setText(context.getResources().getString(R.string.number_of_issues, 0));
            }


            String[] restaurantsWithCustomIcons = context
                    .getResources()
                    .getStringArray(R.array.restaurants_with_custom_icons);

            String[] restaurantsIconIds = context
                    .getResources()
                    .getStringArray(R.array.restaurants_image_ids);

            holder.restaurantIcon.setImageResource(R.drawable.ic_restaurant_white_24dp);
            for (int i = 0; i < restaurantsWithCustomIcons.length; i++) {
                if (restaurantInQuestion.getName().contains(restaurantsWithCustomIcons[i])) {
                    String idName = restaurantsIconIds[i];
                    int id = context
                            .getResources()
                            .getIdentifier(idName, "drawable", context.getPackageName());
                    holder.restaurantIcon.setImageResource(id);
                    break;
                }
            }

            if (restaurantInQuestion.isFavourite()){
                holder.favouriteIcon.setVisibility(View.VISIBLE);
                holder.favouriteIcon.setImageResource(R.drawable.star_filled);
            } else {
                holder.favouriteIcon.setVisibility(View.INVISIBLE);
            }
    }

    @NonNull
    @Override
    public RestaurantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurant_item, parent, false);

        return new RestaurantsViewHolder(view, onRestaurantListener);
    }


    public interface OnRestaurantListener {
        void onRestaurantClick(String trackingNumber);
    }
}

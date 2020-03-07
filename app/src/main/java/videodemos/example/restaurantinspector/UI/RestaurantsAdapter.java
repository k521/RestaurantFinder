package videodemos.example.restaurantinspector.UI;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import videodemos.example.restaurantinspector.Model.Inspection;
import videodemos.example.restaurantinspector.Model.Restaurant;
import videodemos.example.restaurantinspector.R;

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
        OnRestaurantListener onRestaurantListener;

        public RestaurantsViewHolder(View itemView, OnRestaurantListener onRestaurantListener) {
            super(itemView);
            restaurantName = itemView.findViewById(R.id.tv_card_restaraunt_name);
            numOfIssues = itemView.findViewById(R.id.tv_card_number_issues);
            lastInspection = itemView.findViewById(R.id.tv_card_last_inspection);
            cardViewBackground = itemView.findViewById(R.id.cv_restaurant_card);
            hazardIcon = itemView.findViewById(R.id.iv_card_hazard_icon);
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
            Inspection latestInspection = restaurantInQuestion.getInspections().get(sizeOfInspections - 1);
            holder.lastInspection.setText(latestInspection.getInspectionDate());
            int numOfIssues = latestInspection.getNumCritical() + latestInspection.getNumNonCritical();

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

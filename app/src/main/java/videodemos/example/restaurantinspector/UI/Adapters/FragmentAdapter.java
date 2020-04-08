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
import videodemos.example.restaurantinspector.R;

/**
 * An adapter to show favourite restaurants with new inspections.
 */

public class FragmentAdapter extends RecyclerView.Adapter<FragmentAdapter.RestaurantsViewHolder> {


    private List<Restaurant> restaurantDataset;
    private Context context;

    public class RestaurantsViewHolder extends RecyclerView.ViewHolder {
        TextView restaurantName;
        TextView numOfIssues;
        TextView lastInspection;
        CardView cardViewBackground;

        public RestaurantsViewHolder(View itemView) {
            super(itemView);
            restaurantName = itemView.findViewById(R.id.tv_card_restaraunt_name);
            numOfIssues = itemView.findViewById(R.id.tv_card_number_issues);
            lastInspection = itemView.findViewById(R.id.tv_card_last_inspection);
            cardViewBackground = itemView.findViewById(R.id.cv_fragment_card);
        }

    }

    public FragmentAdapter(List<Restaurant> restaurantDataset, Context context) {
        this.restaurantDataset = restaurantDataset;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return restaurantDataset.size();
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantsViewHolder holder, int position) {
        final String LOW_HAZARD = "Low";
        final String MODERATE_HAZARD = "Moderate";

        Restaurant restaurantInQuestion = restaurantDataset.get(position);
        holder.restaurantName.setText(restaurantInQuestion.getName());

        Inspection latestInspection = restaurantInQuestion.getInspections().get(0);

        String dateInput = latestInspection.getInspectionDate();

        String year = dateInput.substring(0, 4);
        String month = dateInput.substring(4, 6);
        String date = dateInput.substring(6, 8);
        String dateInQuestion = year + "-" + month + "-" + date;

        holder.lastInspection.setText(dateInQuestion);

        int numOfIssues = latestInspection.getNumCritical() + latestInspection.getNumNonCritical();

        holder.numOfIssues.setText(context.getResources().getString(R.string.number_of_issues, numOfIssues));

        if (latestInspection.getHazardRating().equals(LOW_HAZARD)) {
            int lowHazardColor = ContextCompat.getColor(context, R.color.colorLowHazard);
            holder.cardViewBackground.setCardBackgroundColor(lowHazardColor);
        } else if (latestInspection.getHazardRating().equals(MODERATE_HAZARD)) {
            int medHazardColor = ContextCompat.getColor(context, R.color.colorMedHazard);
            holder.cardViewBackground.setCardBackgroundColor(medHazardColor);
        } else {
            int highHazardColor = ContextCompat.getColor(context, R.color.colorHighHazard);
            holder.cardViewBackground.setCardBackgroundColor(highHazardColor);
        }

    }

    @NonNull
    @Override
    public RestaurantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);

        return new RestaurantsViewHolder(view);
    }

}

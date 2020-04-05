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

    private List<Restaurant> restaurantDataset = new ArrayList<>();
    private List<Restaurant> restaurantsFullList = new ArrayList<>();
    private boolean[] restaurantsHazardFilter;
    private boolean[] restaurantsTextFilter;
    private boolean[] restaurantsCriticalFilter;
    private boolean[] restaurantsFavourites;
    private Context context;
    private OnRestaurantListener onRestaurantListener;

    private List<Restaurant> textFilterList = new ArrayList<>();

    private boolean areFiltersOn = false;

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

            Log.d("Adapter_Init", "Init arrays");
            Log.d("order", "Start of ViewHolder");

//            restaurantsHazardFilter = new boolean[restaurantsFullList.size()];
//            restaurantsTextFilter = new boolean[restaurantsFullList.size()];
//            restaurantsCriticalFilter = new boolean[restaurantsFullList.size()];
//            restaurantsFavourites = new boolean[restaurantsFullList.size()];
//
//            setAllValuesToTrue(restaurantsHazardFilter);
//            setAllValuesToTrue(restaurantsTextFilter);
//            setAllValuesToTrue(restaurantsCriticalFilter);
//            setAllValuesToTrue(restaurantsFavourites);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Restaurant selectedRestaurant = restaurantDataset.get(getAdapterPosition());
            String trackingNumber = selectedRestaurant.getTrackingNumber();
            onRestaurantListener.onRestaurantClick(trackingNumber);

        }
    }

    public RestaurantsAdapter(List<Restaurant> restaurantsFullList, List<Restaurant> restaurantDataset, Context context, OnRestaurantListener onRestaurantListener) {
        this.restaurantDataset = restaurantDataset;
        this.context = context;
        this.onRestaurantListener = onRestaurantListener;
       // Toast.makeText(context, "Constructor, restaurantDataSet size : " + restaurantDataset.size(), Toast.LENGTH_SHORT).show();

        this.restaurantsFullList.addAll(restaurantsFullList);
//        RestaurantManager manager = RestaurantManager.getInstance();
//        restaurantsFullList.addAll(manager.getRestaurantList());
//        restaurantDataset.addAll(manager.getRestaurantList());

        restaurantsHazardFilter = new boolean[restaurantsFullList.size()];
        restaurantsTextFilter = new boolean[restaurantsFullList.size()];
        restaurantsCriticalFilter = new boolean[restaurantsFullList.size()];
        restaurantsFavourites = new boolean[restaurantsFullList.size()];

        setAllValuesToTrue(restaurantsHazardFilter);
        setAllValuesToTrue(restaurantsTextFilter);
        setAllValuesToTrue(restaurantsCriticalFilter);
        setAllValuesToTrue(restaurantsFavourites);
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
                int numOfDaysSinceLastInspection = dateCalculations.daysInBetween(latestInspection.getInspectionDate());

                // Check to see if it happened in the last 30 days
                if (numOfDaysSinceLastInspection <= LESS_THAN_A_MONTH) {
                    holder.lastInspection.setText(context.getString(R.string.date_of_inspection_days_ago, numOfDaysSinceLastInspection));
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
                    holder.lastInspection.setText(context.getString(R.string.date_of_inspection_with_params, monthName, Integer.parseInt(year)));

                }
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


            String[] restaurantsWithCustomIcons = context.getResources().getStringArray(R.array.restaurants_with_custom_icons);
            String[] restaurantsIconIds = context.getResources().getStringArray(R.array.restaurants_image_ids);
            holder.restaurantIcon.setImageResource(R.drawable.ic_restaurant_white_24dp);
            for (int i = 0; i < restaurantsWithCustomIcons.length; i++) {
                if (restaurantInQuestion.getName().contains(restaurantsWithCustomIcons[i])) {
                    String idName = restaurantsIconIds[i];
                    int id = context.getResources().getIdentifier(idName, "drawable", context.getPackageName());
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

    public void filterByName(String text) {

        restaurantsTextFilter = new boolean[restaurantsFullList.size()];

        if(text.isEmpty()){
            setAllValuesToTrue(restaurantsTextFilter);
        } else{
            text = text.toLowerCase();
            for (int i = 0; i < restaurantsFullList.size(); i++){
                Restaurant r = restaurantsFullList.get(i);

                if(r.getName().toLowerCase().contains(text)){
                    restaurantsTextFilter[i] = true;
                }
            }
        }

        filterAll();
    }

    public void filterByFavourites(boolean doFilter){
        restaurantsFavourites = new boolean[restaurantsFullList.size()];

        if (!doFilter){
            setAllValuesToTrue(restaurantsFavourites);
            filterAll();
            return;
        }

        for (int i = 0; i < restaurantsFullList.size(); i++){
            if (restaurantsFullList.get(i).isFavourite()){
                restaurantsFavourites[i] = true;
            }
        }

        filterAll();
    }


    public void filterByHazardLevel(String hazardLevel){

        restaurantsHazardFilter = new boolean[restaurantsFullList.size()];

        if (hazardLevel.equals("None")){
            setAllValuesToTrue(restaurantsHazardFilter);
            filterAll();
            return;
        }

        for (int i = 0; i < restaurantsFullList.size(); i++){
            Restaurant r = restaurantsFullList.get(i);

            if(r.getInspections().isEmpty()){
                Log.d("ListActivity",r.getName() + " has no inspections");
                continue;
            }
            Inspection mostRecentInspection = r.getInspections().get(0);
            if(mostRecentInspection.getHazardRating().equals(hazardLevel)){
                //r.setVisible(false);
                restaurantsHazardFilter[i] = true;
            }
        }

        filterAll();

    }

    public void filterByCriticalViolations(String criticalViolations, boolean isGreaterThan){

        restaurantsCriticalFilter = new boolean[restaurantsFullList.size()];

        if (criticalViolations.isEmpty()) {
            setAllValuesToTrue(restaurantsCriticalFilter);
            filterAll();
            return;
        }

        int criticalViolation = Integer.parseInt(criticalViolations);

        for (int j = 0; j < restaurantsFullList.size(); j++){
            Restaurant r = restaurantsFullList.get(j);
            int numOfCriticalViolationsFound = 0;
            for(Inspection i : r.getInspections()){
                String dateOfInspection = i.getInspectionDate();
                DateCalculations dc = new DateCalculations();
                int numOfDays = dc.daysInBetween(dateOfInspection);
                if(numOfDays <= 365){
                    numOfCriticalViolationsFound += i.getNumCritical();
                }
                else{
                    break;
                }
            }
            if(isGreaterThan && numOfCriticalViolationsFound >= criticalViolation){
                Log.d("ListActivity",r.getName() + " : " + numOfCriticalViolationsFound);
                restaurantsCriticalFilter[j] = true;
            } else if (!isGreaterThan && numOfCriticalViolationsFound <= criticalViolation){
                restaurantsCriticalFilter[j] = true;
            }
        }

        filterAll();
    }


    private void setAllValuesToTrue(boolean[] list){
        for (int i = 0; i < list.length; i++){
            list[i] = true;
        }
    }

    public void filterAll(){
        String rShown = "";
        restaurantDataset.clear();
        for (int i = 0; i < restaurantsFullList.size(); i++){
            if (restaurantsHazardFilter[i] && restaurantsTextFilter[i]
                    && restaurantsCriticalFilter[i] && restaurantsFavourites[i]){
                restaurantDataset.add(restaurantsFullList.get(i));
                rShown += restaurantsFullList.get(i).getName() + " ";

            }
        }

        //notifyItemRangeChanged(0, restaurantsFullList.size());
        notifyDataSetChanged();
    }

    public interface OnRestaurantListener {
        void onRestaurantClick(String trackingNumber);
    }
}

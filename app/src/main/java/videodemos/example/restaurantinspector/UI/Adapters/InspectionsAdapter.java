package videodemos.example.restaurantinspector.UI.Adapters;


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

import videodemos.example.restaurantinspector.Model.DataHandling.DateCalculations;
import videodemos.example.restaurantinspector.Model.DataHandling.Inspection;
import videodemos.example.restaurantinspector.R;

/**
 * Inspection adapter for RecyclerView.
 */

public class InspectionsAdapter extends RecyclerView.Adapter<InspectionsAdapter.InspectionsViewHolder> {

    private List<Inspection> inspectionDataset;
    private Context context;
    private OnInspectionListener onInspectionListener;

    public class InspectionsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView numOfCritIssues;
        private TextView numOfNonCritIssues;
        private TextView daysSinceInspection;
        private ImageView hazardIcon;
        private CardView cardViewBackground;
        private OnInspectionListener onInspectionListener;

        public InspectionsViewHolder(View itemView, OnInspectionListener onInspectionListener) {
            super(itemView);

            numOfCritIssues = itemView.findViewById(R.id.tv_card_number_crit_issues);
            numOfNonCritIssues = itemView.findViewById(R.id.tv_card_number_non_crit_issues);
            daysSinceInspection = itemView.findViewById(R.id.tv_card_days_from_inspection);
            hazardIcon = itemView.findViewById(R.id.iv_card_inspection_hazard_icon);
            cardViewBackground = itemView.findViewById(R.id.cv_inspection_card);
            this.onInspectionListener = onInspectionListener;

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onInspectionListener.onInspectionClick(getAdapterPosition());
        }
    }

    public InspectionsAdapter(List<Inspection> inspectionDataset, Context context,
                              OnInspectionListener onInspectionListener) {
        this.inspectionDataset = inspectionDataset;
        this.context = context;
        this.onInspectionListener = onInspectionListener;
    }

    @Override
    public int getItemCount() {
        return inspectionDataset.size();
    }

    @Override
    public void onBindViewHolder(@NonNull InspectionsViewHolder holder, int position) {
        final int LESS_THAN_A_MONTH = 30;
        final int LESS_THAN_A_YEAR = 365;

        Inspection inspectionInQuestion = inspectionDataset.get(position);
        int critIssues = inspectionInQuestion.getNumCritical();
        int nonCritIssues = inspectionInQuestion.getNumNonCritical();

        holder.numOfCritIssues.setText(context.getResources().getString(R.string.number_crit_issues, critIssues));
        holder.numOfNonCritIssues.setText(context.getResources().getString(R.string.number_non_crit_issues, nonCritIssues));

        DateCalculations dateCalculations = new DateCalculations(context);
        int numOfDaysSinceLastInspection = dateCalculations.daysInBetween(inspectionInQuestion.getInspectionDate());

        if (numOfDaysSinceLastInspection <= LESS_THAN_A_MONTH) {
            holder.daysSinceInspection.setText(context.getString(R.string.date_of_inspection_days_ago, numOfDaysSinceLastInspection));
        } else if (numOfDaysSinceLastInspection <= LESS_THAN_A_YEAR) {
            String date = inspectionInQuestion.getInspectionDate();
            String month = date.substring(4, 6);
            String day = date.substring(6, 8);

            int monthInteger = Integer.parseInt(month);
            int dayInteger = Integer.parseInt(day);

            String monthName = dateCalculations.getMonthName(monthInteger);
            holder.daysSinceInspection.setText(context.getString(R.string.date_of_inspection_with_params, monthName, dayInteger));

        } else {
            String date = inspectionInQuestion.getInspectionDate();
            String year = date.substring(0, 4);
            String month = date.substring(4, 6);

            String monthName = dateCalculations.getMonthName(Integer.parseInt(month));
            holder.daysSinceInspection.setText(context.getString(R.string.date_of_inspection_with_params, monthName, Integer.parseInt(year)));

        }

        String hazardLevel = inspectionInQuestion.getHazardRating();
        int hazardColor;
        if (hazardLevel.equals("Low")){
            hazardColor= ContextCompat.getColor(context, R.color.colorLowHazard);
            holder.cardViewBackground.setCardBackgroundColor(hazardColor);
            holder.hazardIcon.setImageResource(R.drawable.low_hazard_icon);
        } else if(hazardLevel.equals("Moderate")){
            hazardColor = ContextCompat.getColor(context, R.color.colorMedHazard);
            holder.cardViewBackground.setCardBackgroundColor(hazardColor);
            holder.hazardIcon.setImageResource(R.drawable.med_hazard_icon);
        } else {
            hazardColor = ContextCompat.getColor(context, R.color.colorHighHazard);
            holder.cardViewBackground.setCardBackgroundColor(hazardColor);
            holder.hazardIcon.setImageResource(R.drawable.high_hazard_icon);
        }

    }

    @NonNull
    @Override
    public InspectionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.report_item, parent, false);

        return new InspectionsViewHolder(view, onInspectionListener);
    }

    public interface OnInspectionListener {
        void onInspectionClick(int position);
    }
}

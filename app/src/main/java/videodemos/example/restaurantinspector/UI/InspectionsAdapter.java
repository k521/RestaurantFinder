package videodemos.example.restaurantinspector.UI;


import android.content.Context;
import android.util.Log;
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
import videodemos.example.restaurantinspector.R;

public class InspectionsAdapter extends RecyclerView.Adapter<InspectionsAdapter.InspectionsViewHolder> {

    private List<Inspection> inspectionDataset;
    private Context context;
    private OnInspectionListener onInspectionListener;


    public class InspectionsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView numOfCritIssues;
        TextView numOfNonCritIssues;
        TextView daysSinceInspection;
        CardView cardViewBackground;
        OnInspectionListener onInspectionListener;

        public InspectionsViewHolder(View itemView, OnInspectionListener onInspectionListener) {
            super(itemView);

            numOfCritIssues = itemView.findViewById(R.id.tv_card_number_crit_issues);
            numOfNonCritIssues = itemView.findViewById(R.id.tv_card_number_non_crit_issues);
            daysSinceInspection = itemView.findViewById(R.id.tv_card_days_from_inspection);
            cardViewBackground = itemView.findViewById(R.id.cv_inspection_card);
            this.onInspectionListener = onInspectionListener;

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onInspectionListener.onInspectionClick(getAdapterPosition());

        }
    }

    public InspectionsAdapter(List<Inspection> inspectionDataset, Context context, OnInspectionListener onInspectionListener) {
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
        Inspection inspectionInQuestion = inspectionDataset.get(position);
        int critIssues = inspectionInQuestion.getNumCritical();
        int nonCritIssues = inspectionInQuestion.getNumNonCritical();
        String daysFrom = inspectionInQuestion.getInspectionDate();

        holder.numOfCritIssues.setText(context.getResources().getString(R.string.number_crit_issues, critIssues));
        holder.numOfNonCritIssues.setText(context.getResources().getString(R.string.number_non_crit_issues, nonCritIssues));
        holder.daysSinceInspection.setText(daysFrom);

        String hazardLevel = inspectionInQuestion.getHazardRating();
        int hazardColor;
        if (hazardLevel.equals("Low")){
            hazardColor= ContextCompat.getColor(context, R.color.colorLowHazard);
            holder.cardViewBackground.setCardBackgroundColor(hazardColor);
        } else if(hazardLevel.equals("Moderate")){
            hazardColor = ContextCompat.getColor(context, R.color.colorMedHazard);
            holder.cardViewBackground.setCardBackgroundColor(hazardColor);
        } else {
            hazardColor = ContextCompat.getColor(context, R.color.colorHighHazard);
            holder.cardViewBackground.setCardBackgroundColor(hazardColor);
        }

    }

    @NonNull
    @Override
    public InspectionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.report_item, parent, false);

        InspectionsViewHolder vh = new InspectionsViewHolder(view, onInspectionListener);
        return vh;
    }

    public interface OnInspectionListener {
        void onInspectionClick(int position);
    }
}

package videodemos.example.restaurantinspector.UI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import videodemos.example.restaurantinspector.Model.Restaurant;
import videodemos.example.restaurantinspector.R;

public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.RestaurantsViewHolder> {

    private List<Restaurant> restaurantDataset;


    public static class RestaurantsViewHolder extends RecyclerView.ViewHolder {
        TextView myTextView;

        public RestaurantsViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.tv_text);
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
        holder.myTextView.setText(restaurantDataset.get(position).getName());
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

package videodemos.example.restaurantinspector.UI.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import videodemos.example.restaurantinspector.Model.DataHandling.Inspection;
import videodemos.example.restaurantinspector.Model.DataHandling.Restaurant;
import videodemos.example.restaurantinspector.Model.RestaurantManager;
import videodemos.example.restaurantinspector.R;
import videodemos.example.restaurantinspector.UI.Adapters.FragmentAdapter;
import videodemos.example.restaurantinspector.UI.Adapters.InspectionsAdapter;
import videodemos.example.restaurantinspector.UI.Adapters.RestaurantsAdapter;
import videodemos.example.restaurantinspector.UI.MapsActivity;

public class NewFavouriteInspectionFragment extends AppCompatDialogFragment{

    private RestaurantManager manager = RestaurantManager.getInstance();


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.new_fav_info_fragment, null);

        setUpRestaurantsRecylerView(v);

        DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        };

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton(android.R.string.ok, okListener)
                .create();
    }

    private void setUpRestaurantsRecylerView(View rootView) {
        RecyclerView restaurantsRecyclerView = rootView.findViewById(R.id.rv_fav_fragment);

        restaurantsRecyclerView.setHasFixedSize(true);
        restaurantsRecyclerView.setItemViewCacheSize(20);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        restaurantsRecyclerView.setLayoutManager(layoutManager);

        HashMap<String, Inspection> hashMap = manager.getFavouritesMap();
        List<Restaurant> favRestaurants = new ArrayList<>();

        Iterator iterator = hashMap.entrySet().iterator();
        while (iterator.hasNext()){
            Log.d("test", "WHILE LOOP");
            HashMap.Entry entry = (HashMap.Entry)iterator.next();
            Restaurant restaurant = new Restaurant();
            restaurant.setTrackingNumber((String)entry.getKey());
            if (manager.getRestaurantList().contains(restaurant)){
                Log.d("test", "Found something " + restaurant.getTrackingNumber());
                int indexOfRestaurant = manager.getRestaurantList().indexOf(restaurant);
                favRestaurants.add(manager.getRestaurant(indexOfRestaurant));
            }

        }

        FragmentAdapter restaurantsAdapter = new FragmentAdapter(favRestaurants, rootView.getContext());
        restaurantsRecyclerView.setAdapter(restaurantsAdapter);

    }

}

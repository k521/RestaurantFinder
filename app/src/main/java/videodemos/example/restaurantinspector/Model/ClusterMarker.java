package videodemos.example.restaurantinspector.Model;


import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import videodemos.example.restaurantinspector.Model.DataHandling.Restaurant;

public class ClusterMarker implements ClusterItem {


    private LatLng position;
    private String title = "";
    private String snippet = "";
    private int iconPicture;
    private Restaurant restaurant;

    public ClusterMarker(LatLng position, String title, String snippet, int iconPicture, Restaurant restaurant) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.iconPicture = iconPicture;
        this.restaurant = restaurant;
    }

    public ClusterMarker() {
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public int getIconPicture() {
        return iconPicture;
    }

    public void setIconPicture(int iconPicture) {
        this.iconPicture = iconPicture;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}

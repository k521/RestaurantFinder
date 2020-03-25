package videodemos.example.restaurantinspector.Model;


import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import videodemos.example.restaurantinspector.Model.DataHandling.Restaurant;

/**
 * A class that implements a custom Marker to store extra info.
 */
public class ClusterMarker implements ClusterItem {

    private LatLng position;
    private String title = "";
    private String snippet = "";
    private int iconPicture;
    private String trackingNumber;

    public ClusterMarker(LatLng position, String title, String snippet, int iconPicture, String trackingNumber) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.iconPicture = iconPicture;
        this.trackingNumber = trackingNumber;
    }

    public ClusterMarker() {

    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public int getIconPicture() {
        return iconPicture;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

}

package videodemos.example.restaurantinspector.Utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

import videodemos.example.restaurantinspector.Model.ClusterMarker;
import videodemos.example.restaurantinspector.R;

//
//public class MyClusterManagerRenderer extends DefaultClusterRenderer<ClusterMarker> {
//
//    private final IconGenerator iconGenerator;
//    private final ImageView imageView;
//    private final int markerWidth;
//    private final int markerHeight;
//
//    public MyClusterManagerRenderer(Context context, GoogleMap map, ClusterManager<ClusterMarker> clusterManager) {
//        super(context, map, clusterManager);
//
//        iconGenerator = new IconGenerator(context.getApplicationContext());
//        imageView = new ImageView(context.getApplicationContext());
//        markerWidth = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
//        markerHeight = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
//        imageView.setLayoutParams(new ViewGroup.LayoutParams(markerWidth, markerHeight));
//        int padding = (int) context.getResources().getDimension(R.dimen.custom_marker_padding);
//        imageView.setPadding(padding, padding, padding, padding);
//        iconGenerator.setContentView(imageView);
//    }
//
//    @Override
//    protected void onBeforeClusterItemRendered(ClusterMarker item, MarkerOptions markerOptions) {
//        imageView.setImageResource(item.getIconPicture());
//        Bitmap icon = iconGenerator.makeIcon();
//        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle());
//    }
//
//    @Override // Lets multiple restaurants be clustered
//    protected boolean shouldRenderAsCluster(Cluster<ClusterMarker> cluster) {
//        return false;
//    }
//}




public class MyClusterManagerRenderer extends DefaultClusterRenderer<ClusterMarker> {
    private final IconGenerator iconGenerator;
    private final ImageView imageView;
    //private final int mDimension;
    private final int markerWidth;
    private final int markerHeight;
    private GoogleMap map;


    public MyClusterManagerRenderer(Context context, GoogleMap map, ClusterManager<ClusterMarker> clusterManager) {
        super(context, map, clusterManager);
        iconGenerator = new IconGenerator(context.getApplicationContext());
        imageView = new ImageView(context.getApplicationContext());
        markerWidth = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        markerHeight = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(markerWidth, markerHeight));
        int padding = (int) context.getResources().getDimension(R.dimen.custom_marker_padding);
        imageView.setPadding(padding, padding, padding, padding);
        iconGenerator.setContentView(imageView);
        this.map  = map;

        //View multiProfile = getLayoutInflater().inflate(R.layout.multi_profile, null);

        //mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);

    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterMarker clusterMarker, MarkerOptions markerOptions) {
        imageView.setImageResource(clusterMarker.getIconPicture());
        Bitmap icon = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(clusterMarker.getTitle());
    }

//    @Override
//    protected void onBeforeClusterRendered(Cluster<Person> cluster, MarkerOptions markerOptions) {
//        // Draw multiple people.
//        // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
//        List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
//        int width = mDimension;
//        int height = mDimension;
//
//        for (Person p : cluster.getItems()) {
//            // Draw 4 at most.
//            if (profilePhotos.size() == 4) break;
//            Drawable drawable = getResources().getDrawable(p.profilePhoto);
//            drawable.setBounds(0, 0, width, height);
//            profilePhotos.add(drawable);
//        }
//        MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
//        multiDrawable.setBounds(0, 0, width, height);
//
//        mClusterImageView.setImageDrawable(multiDrawable);
//        Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
//        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
//    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        // Always render clusters.
        return cluster.getSize() > 1;
    }

}


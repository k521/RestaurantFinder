package videodemos.example.restaurantinspector.Model.DataHandling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import videodemos.example.restaurantinspector.Model.DataHandling.Inspection;

/**
 * Class to hold restaurant information.
 */

public class Restaurant {

    private String trackingNumber;
    private String name;
    private String physicalAddress;
    private String physicalCity;
    private String factype;
    private double latitude;
    private double longitude;
    private boolean isFavourite;
    private boolean isVisible = true;

    private List<Inspection> inspections = new ArrayList<>();

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getPhysicalAddress() {
        return physicalAddress;
    }

    public void setPhysicalAddress(String physicalAddress) {
        this.physicalAddress = physicalAddress;
    }



    public void setPhysicalCity(String physicalCity) {
        this.physicalCity = physicalCity;
    }

    public void setFactype(String factype) {
        this.factype = factype;
    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }



    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }


    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }



    public void addInspection(Inspection i) {
        inspections.add(i);
    }

    public List<Inspection> getInspections() {
        return inspections;
    }


    public void sortByInspectionDate() {
        Comparator<Inspection> comparatorName = new Comparator<Inspection>() {
            @Override
            public int compare(Inspection r1, Inspection r2) {
                int r1InspectionDate = Integer.parseInt(r1.getInspectionDate());
                int r2InspectionDate = Integer.parseInt(r2.getInspectionDate());
                return r2InspectionDate - r1InspectionDate;
            }
        };

        Collections.sort(inspections, comparatorName);
    }


    @Override
    public String toString() {
        return "Restaurant{" +
                "trackingNumber='" + trackingNumber + '\'' +
                ", name='" + name + '\'' +
                ", physicalAddress='" + physicalAddress + '\'' +
                ", physicalCity='" + physicalCity + '\'' +
                ", factype='" + factype + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", inspections=" + inspections +
                '}';
    }


}

package videodemos.example.restaurantinspector.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Restaurant {

    private String trackingNumber;
    private String name;
    private String physicalAddress;
    private String physicalCity;
    private String factype;
    private double latitude;
    private double longitude;

    private ArrayList<Inspection> inspections = new ArrayList<>();

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

    public String getPhysicalCity() {
        return physicalCity;
    }

    public void setPhysicalCity(String physicalCity) {
        this.physicalCity = physicalCity;
    }

    public String getFactype() {
        return factype;
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

    public ArrayList<Inspection> getInspections() {
        return inspections;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void addInspection(Inspection i) {
        inspections.add(i);
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

    public void sortByInspectionDate() {
        Comparator<Inspection> comparatorName = new Comparator<Inspection>() {
            @Override
            public int compare(Inspection r1, Inspection r2) {
                return r1.getInspectionDate().compareTo(r2.getInspectionDate());
            }
        };

        Collections.sort(inspections, comparatorName);
    }


}

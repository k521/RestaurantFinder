package videodemos.example.restaurantinspector.Model;

import java.util.ArrayList;
import java.util.List;

public class Inspection{


    private String hazardRating;
    private String InspectionDate;
    private String InspType;
    private int NumCritical;
    private int NumNonCritical;

    private ArrayList<Integer> violationCodes = new ArrayList<>();

    public String getInspectionDate() {
        return InspectionDate;
    }

    public void setInspectionDate(String inspectionDate) {
        InspectionDate = inspectionDate;
    }

    public String getInspType() {
        return InspType;
    }

    public void setInspType(String inspType) {
        InspType = inspType;
    }

    public int getNumCritical() {
        return NumCritical;
    }

    public void setNumCritical(int numCritical) {
        NumCritical = numCritical;
    }

    public int getNumNonCritical() {
        return NumNonCritical;
    }

    public void setNumNonCritical(int numNonCritical) {
        NumNonCritical = numNonCritical;
    }


    private List<Integer> violationList = new ArrayList<>();

    public String getHazardRating() {
        return hazardRating;
    }

    public void setHazardRating(String hazardRating) {
        this.hazardRating = hazardRating;
    }

    public void addViolation(int violationCode){
        violationList.add(violationCode);
    }



}


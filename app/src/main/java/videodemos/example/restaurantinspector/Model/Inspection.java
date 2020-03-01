package videodemos.example.restaurantinspector.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Inspection{


    private String hazardRating;
    private String InspectionDate;
    private String InspType;
    private int NumCriticalType;
    private int NumNonCritical;
    private String ViolLump;


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

    public int getNumCriticalType() {
        return NumCriticalType;
    }

    public void setNumCriticalType(int numCriticalType) {
        NumCriticalType = numCriticalType;
    }

    public int getNumNonCritical() {
        return NumNonCritical;
    }

    public void setNumNonCritical(int numNonCritical) {
        NumNonCritical = numNonCritical;
    }

    public String getViolLump() {
        return ViolLump;
    }

    public void setViolLump(String violLump) {
        ViolLump = violLump;
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

